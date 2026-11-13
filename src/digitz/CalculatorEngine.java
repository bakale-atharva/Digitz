package digitz;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * The calculator's brain. It remembers the number being typed, the running total and
 * the pending operation, and turns key presses into results.
 *
 * <h2>No User Interface Code</h2>
 * <p>This class does not use Swing at all. The window ({@link CalculatorWindow})
 * forwards each key press to one method here, then reads {@link #displayText()} and
 * {@link #historyText()} to redraw itself. Because of that split, the engine can be
 * driven and tested from plain code without opening a window.
 *
 * <h2>Which Method Each Key Calls</h2>
 * <table class="striped">
 *   <caption>Keys and their engine methods</caption>
 *   <thead><tr><th scope="col">Key</th><th scope="col">Method</th></tr></thead>
 *   <tbody>
 *     <tr><td>{@code 0}-{@code 9}</td><td>{@link #digit(int)}</td></tr>
 *     <tr><td>{@code .}</td><td>{@link #decimalPoint()}</td></tr>
 *     <tr><td>{@code + - * ÷ ^}</td><td>{@link #binary(BinaryOp)}</td></tr>
 *     <tr><td>{@code √ x² sin} and other one-number keys</td><td>{@link #unary(UnaryOp)}</td></tr>
 *     <tr><td>{@code =}</td><td>{@link #equalsKey()}</td></tr>
 *     <tr><td>{@code ⌫}</td><td>{@link #backspace()}</td></tr>
 *     <tr><td>{@code C}</td><td>{@link #clear()}</td></tr>
 *   </tbody>
 * </table>
 *
 * <h2>How Calculations Chain</h2>
 * <p>Operations are evaluated immediately from left to right, like a pocket calculator.
 * There is no operator precedence: {@code 2 + 3 x 4 =} gives {@code 20}
 * ({@code (2 + 3) x 4}), not {@code 14}. Pressing a second operator before typing a
 * new number just replaces the first one, so {@code 2 + x 3 =} gives {@code 6}.
 *
 * <h2>Errors</h2>
 * <p>When a result is not a finite number, such as {@code 1 ÷ 0} or {@code √(-4)}, the
 * engine enters an error state: {@link #displayText()} returns {@link #ERROR_TEXT},
 * operator keys are ignored, and typing a digit, the decimal point, {@code ⌫} or
 * {@code C} starts afresh. Methods in this class never throw for user input; only
 * programming mistakes (such as {@code digit(12)}) throw exceptions.
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
 * engine.displayText();  // "15"
 * engine.historyText();  // "12 + 3 ="
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
     * for example when dividing by zero. Compare against this constant rather than
     * a literal {@code "Error"} string, or use {@link #hasError()}.
     */
    public static final String ERROR_TEXT = "Error";

    // Best practice: named constants instead of "magic numbers" scattered through the code.

    /**
     * The most characters a typed number can have, including the decimal point and
     * minus sign. Further digits are ignored, so the display never overflows.
     */
    private static final int MAX_INPUT_LENGTH = 16;

    public CalculatorEngine() {
        // All fields start with the values set in their declarations.
    }
}
