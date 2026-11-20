package digitz;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * The calculator's brain. It remembers the number being typed, the running total and
 * the pending operation, and turns key presses into results.
 *
 * <h2>No user interface code</h2>
 * <p>This class does not use Swing at all. The window ({@link CalculatorWindow})
 * forwards each key press to one method here, then reads {@link #displayText()} and
 * {@link #historyText()} to redraw itself. Because of that split, the engine can be
 * driven and tested from plain code without opening a window.
 *
 * <h2>Which method each key calls</h2>
 * <table class="striped">
 *   <caption>Keys and their engine methods</caption>
 *   <thead><tr><th scope="col">Key</th><th scope="col">Method</th></tr></thead>
 *   <tbody>
 *     <tr><td>{@code 0}–{@code 9}</td><td>{@link #digit(int)}</td></tr>
 *     <tr><td>{@code .}</td><td>{@link #decimalPoint()}</td></tr>
 *     <tr><td>{@code + − × ÷ ^}</td><td>{@link #binary(BinaryOp)}</td></tr>
 *     <tr><td>{@code √ x² sin} and other one-number keys</td><td>{@link #unary(UnaryOp)}</td></tr>
 *     <tr><td>{@code =}</td><td>{@link #equalsKey()}</td></tr>
 *     <tr><td>{@code ⌫}</td><td>{@link #backspace()}</td></tr>
 *     <tr><td>{@code C}</td><td>{@link #clear()}</td></tr>
 *   </tbody>
 * </table>
 *
 * <h2>How calculations chain</h2>
 * <p>Operations are evaluated immediately from left to right, like a pocket calculator.
 * There is no operator precedence: {@code 2 + 3 × 4 =} gives {@code 20}
 * ({@code (2 + 3) × 4}), not {@code 14}. Pressing a second operator before typing a
 * new number just replaces the first one, so {@code 2 + × 3 =} gives {@code 6}.
 *
 * <h2>Errors</h2>
 * <p>When a result is not a finite number, such as {@code 1 ÷ 0} or {@code √(-4)}, the
 * engine enters an error state: {@link #displayText()} returns {@link #ERROR_TEXT},
 * operator keys are ignored, and typing a digit, the decimal point, {@code ⌫} or
 * {@code C} starts afresh. Methods in this class never throw for user input; only
 * programming mistakes (such as {@code digit(12)}) throw exceptions.
 *
 * <h2>Display formatting</h2>
 * <p>Results are rounded to 12 significant digits, so {@code 0.1 + 0.2} shows
 * {@code 0.3} rather than {@code 0.30000000000000004}. Very large or very small
 * results switch to E notation, such as {@code 1E+20}.
 *
 * <h2>Example</h2>
 * {@snippet :
 * CalculatorEngine engine = new CalculatorEngine();
 * engine.digit(1);
 * engine.digit(2);
 * engine.binary(BinaryOp.ADD);
 * engine.digit(3);
 * engine.equalsKey();
 *
 * engine.displayText();   // "15"
 * engine.historyText();   // "12 + 3 ="
 * }
 *
 * <h2>Threading</h2>
 * <p>This class is not thread-safe. In the app it is only ever used from the Swing
 * Event Dispatch Thread, so no locking is needed.
 *
 * @see CalculatorWindow
 * @see BinaryOp
 * @see UnaryOp
 */
public final class CalculatorEngine {

    /**
     * The text shown on the display when a calculation has no valid result,
     * for example after dividing by zero. Compare against this constant rather than
     * a literal {@code "Error"} string, or use {@link #hasError()}.
     */
    public static final String ERROR_TEXT = "Error";

    private static final int MAX_INPUT_LENGTH = 16;
    private static final MathContext DISPLAY_PRECISION = new MathContext(12);
    private static final double PLAIN_NOTATION_MAX = 1e15;
    private static final double PLAIN_NOTATION_MIN = 1e-9;

    private final StringBuilder input = new StringBuilder("0");
    private String history = "";

    private double accumulator;
    private BinaryOp pendingOp;           // null when no operation is waiting for its second number
    private boolean startNewEntry = true; // the next digit replaces the display instead of appending
    private boolean awaitingOperand;      // an operator was pressed and no new number has been typed yet
    private boolean error;

    /**
     * Creates a calculator in its starting state: the display shows {@code 0}, the
     * history line is empty, and no operation is pending.
     */
    public CalculatorEngine() {
    }

    /**
     * Handles a digit key.
     *
     * <p>Behaviour depends on the current state:
     * <ul>
     *   <li>After a result, an operator or an error, the digit starts a new number.</li>
     *   <li>A leading {@code 0} is replaced, so typing {@code 0} then {@code 7} shows {@code 7}.</li>
     *   <li>Once the number is 16 characters long, more digits are ignored.</li>
     * </ul>
     *
     * @param digit the digit pressed, from {@code 0} to {@code 9}
     * @throws IllegalArgumentException if {@code digit} is not between {@code 0} and {@code 9};
     *         this means a bug in the calling code, never a user mistake
     */
    public void digit(int digit) {
        if (digit < 0 || digit > 9) {
            throw new IllegalArgumentException("Not a single digit: " + digit);
        }
        beginEntryIfNeeded();
        if (input.length() >= MAX_INPUT_LENGTH) {
            return;
        }
        if (input.toString().equals("0")) {
            input.setLength(0);
        }
        input.append(digit);
    }

    /**
     * Handles the decimal point key.
     *
     * <p>A number can contain only one point, so further presses are ignored. Pressing it
     * to start a new number shows {@code 0.}, ready for the digits after the point.
     */
    public void decimalPoint() {
        beginEntryIfNeeded();
        if (input.indexOf(".") < 0 && input.length() < MAX_INPUT_LENGTH) {
            input.append('.');
        }
    }

    /**
     * Handles the backspace key ({@code ⌫}): removes the last typed character.
     *
     * <ul>
     *   <li>While typing, removes one character. Removing the last one leaves {@code 0}.</li>
     *   <li>When a result is shown, does nothing, because a result cannot be edited.</li>
     *   <li>When an error is shown, clears everything, exactly like {@link #clear()}.</li>
     * </ul>
     */
    public void backspace() {
        if (error) {
            clear();
            return;
        }
        if (startNewEntry) {
            return;
        }
        input.deleteCharAt(input.length() - 1);
        if (input.isEmpty() || input.toString().equals("-")) {
            setInput("0");
        }
    }

    /**
     * Handles an operator key that needs two numbers: {@code + − × ÷ ^}.
     *
     * <ul>
     *   <li>If another operation is already waiting and a new number has been typed,
     *       that operation is calculated first and its result is shown. This is what makes
     *       {@code 2 + 3 ×} show {@code 5}.</li>
     *   <li>If the user presses two operators in a row, the second replaces the first.</li>
     *   <li>The history line is updated to show the running total and the new operator,
     *       for example {@code "5 ×"}.</li>
     *   <li>Ignored while an error is shown.</li>
     * </ul>
     *
     * @param op the operator pressed; must not be {@code null}
     */
    public void binary(BinaryOp op) {
        if (error) {
            return;
        }
        double value = currentValue();

        if (pendingOp == null) {
            accumulator = value;
        } else if (!awaitingOperand) {
            accumulator = pendingOp.apply(accumulator, value);
            if (!showResult(accumulator)) {
                return;
            }
        }

        pendingOp = op;
        history = format(accumulator) + " " + op.symbol();
        awaitingOperand = true;
        startNewEntry = true;
    }

    /**
     * Handles a key that works on one number, such as {@code √}, {@code sin} or {@code ±}.
     *
     * <p>The operation is applied to the number on the display and the result is shown
     * immediately. If an operation is pending, the result becomes its second number, so
     * {@code 9 + 16 √ =} gives {@code 13}.
     *
     * <p>{@link UnaryOp#NEGATE} is special while a number is being typed: it only toggles
     * the minus sign, so the user can keep typing digits afterwards.
     *
     * <p>Ignored while an error is shown. An invalid result, such as {@code √(-4)},
     * puts the engine into the error state.
     *
     * @param op the operation pressed; must not be {@code null}
     */
    public void unary(UnaryOp op) {
        if (error) {
            return;
        }
        if (op == UnaryOp.NEGATE && !startNewEntry) {
            toggleSign();
            return;
        }

        double value = currentValue();
        String expression = op.describe(format(value));
        history = pendingOp == null ? expression : format(accumulator) + " " + pendingOp.symbol() + " " + expression;

        showResult(op.apply(value));
        awaitingOperand = false;
        startNewEntry = true;
    }

    /**
     * Handles the {@code =} key: calculates the pending operation and shows the result.
     *
     * <p>The history line shows the full calculation, for example {@code "12 + 3 ="}.
     * The result can then be used as the first number of a new calculation by pressing
     * an operator, or replaced by typing a new number.
     *
     * <p>Does nothing when no operation is pending or an error is shown. If the user
     * presses {@code =} straight after an operator, the number on the display is used
     * for both sides, so {@code 4 + =} gives {@code 8}.
     */
    public void equalsKey() {
        if (error || pendingOp == null) {
            return;
        }
        double value = currentValue();
        history = format(accumulator) + " " + pendingOp.symbol() + " " + format(value) + " =";

        double result = pendingOp.apply(accumulator, value);
        pendingOp = null;
        awaitingOperand = false;
        startNewEntry = true;
        showResult(result);
    }

    /**
     * Handles the {@code C} key: resets the calculator to its starting state.
     *
     * <p>The display returns to {@code 0}, the history line empties, any pending operation
     * is forgotten and any error is cleared.
     */
    public void clear() {
        setInput("0");
        history = "";
        accumulator = 0.0;
        pendingOp = null;
        startNewEntry = true;
        awaitingOperand = false;
        error = false;
    }

    /**
     * Returns the text for the main display.
     *
     * <p>While the user is typing, this is exactly what they typed, including a trailing
     * point such as {@code "3."}. After a calculation it is the formatted result, such as
     * {@code "0.3"} or {@code "1E+20"}.
     *
     * @return the display text, or {@link #ERROR_TEXT} in the error state; never {@code null}
     */
    public String displayText() {
        return error ? ERROR_TEXT : input.toString();
    }

    /**
     * Returns the small line shown above the display, describing the latest step.
     *
     * <p>Examples: {@code "12 +"} after pressing an operator, {@code "12 + 3 ="} after
     * {@code =}, or {@code "√(81)"} after a one-number operation. After an error it still
     * shows the step that failed, so the user can see what went wrong.
     *
     * @return the history text, or an empty string when there is nothing to show; never {@code null}
     */
    public String historyText() {
        return history;
    }

    /**
     * Tells whether the calculator is showing an error.
     *
     * <p>While this is {@code true}, operator keys and {@code =} are ignored. Typing a digit
     * or decimal point, or pressing {@code ⌫} or {@code C}, clears the error.
     *
     * @return {@code true} if {@link #displayText()} is currently {@link #ERROR_TEXT}
     */
    public boolean hasError() {
        return error;
    }

    private void beginEntryIfNeeded() {
        if (error) {
            clear();
        }
        if (startNewEntry) {
            setInput("0");
            startNewEntry = false;
        }
        awaitingOperand = false;
    }

    private void toggleSign() {
        if (input.charAt(0) == '-') {
            input.deleteCharAt(0);
        } else if (!input.toString().equals("0")) {
            input.insert(0, '-');
        }
    }

    private double currentValue() {
        return Double.parseDouble(input.toString());
    }

    // Returns false if the result was NaN or infinite and the engine is now in the error state.
    private boolean showResult(double result) {
        if (!Double.isFinite(result)) {
            error = true;
            pendingOp = null;
            awaitingOperand = false;
            startNewEntry = true;
            return false;
        }
        setInput(format(result));
        return true;
    }

    private void setInput(String text) {
        input.setLength(0);
        input.append(text);
    }

    // Rounding to 12 significant digits hides double noise: 0.30000000000000004 becomes "0.3".
    static String format(double value) {
        BigDecimal rounded = BigDecimal.valueOf(value).round(DISPLAY_PRECISION).stripTrailingZeros();
        double magnitude = Math.abs(rounded.doubleValue());

        boolean readableAsPlain = magnitude == 0.0
                || (magnitude >= PLAIN_NOTATION_MIN && magnitude < PLAIN_NOTATION_MAX);
        return readableAsPlain ? rounded.toPlainString() : rounded.toString();
    }
}
