package digitz;
import java.util.function.DoubleBinaryOperator;

/**
 * The calculator operations that combine <em>two</em> numbers, such as {@code 7 + 3}.
 *
 * <h2>How it is used</h2>
 * <p>When the user presses an operator key, {@link CalculatorEngine#binary(BinaryOp)}
 * remembers the operation. When the second number is complete (the user presses
 * {@code =} or another operator), the engine calls {@link #apply(double, double)}
 * with the left and right operands.
 *
 * <h2>Invalid results</h2>
 * <p>An operation never throws an exception. When a result is undefined, such as
 * dividing by zero, it returns {@link Double#NaN}. The engine then shows
 * {@link CalculatorEngine#ERROR_TEXT}.
 *
 * <h2>Example</h2>
 * {@snippet :
 * double result = BinaryOp.MULTIPLY.apply(6, 7); // 42.0
 * String label = BinaryOp.MULTIPLY.symbol();     // "×"
 * double broken = BinaryOp.DIVIDE.apply(1, 0);   // NaN
 * }
 *
 * @see UnaryOp
 * @see CalculatorEngine#binary(BinaryOp)
 */
public enum BinaryOp {
    /**
     * Addition: {@code left + right}.
     * <p>Shown as {@code +}. Example: {@code 2 + 3 = 5}.
     */
    ADD("+", (left, right) -> left + right),

    /**
     * Subtraction: {@code left - right}.
     * <p>Shown as true minus sign {@code -} (U=2212). The keyboard {@code -} key
     * also triggers it. Example: {@code 9 - 4 = 5}.
     */
    SUBTRACT("-", (left, right) -> left - right),

    /**
     * Multiplication: {@code left × right}.
     * <p>Shown as {@code ×}. They keyboard {@code *} key also triggers it.
     * Example: {@code 6 × 7 = 42}.
     */
    MULTIPLY("×", ((left, right) -> left * right)),

    /**
     * Division: {@code left ÷ right}.
     * <p>Shown as {@code ÷}. The keyboard {@code /} key also triggers it.
     * Dividing by zero returns {@link Double#NaN}, which the calculator shows as
     * {@code Error}, instead of Java's default of {@code Infinity}.
     * Example: {@code 10 ÷ 4 = 2.5}.
     */
    DIVIDE("÷", ((left, right) -> right == 0.0 ? Double.NaN : left / right)),

    /**
     * Exponentiation: {@code left} raised to the power of {@code right}.
     * <p>Shown as {@code ^} in the history line and as {@code xʸ} on its button.
     * Fractional powers work too: {@code 9 ^ 0.5 = 3}. Results that are not real
     * numbers, such as {@code (-8) ^ 0.5}, come back as {@link Double#NaN}.
     * Example: {@code 2 ^ 10 = 1024}.
     */
    POWER("^", Math::pow);

    private final String symbol;
    private final DoubleBinaryOperator function;

    BinaryOp(String symbol, DoubleBinaryOperator function) {
        this.symbol = symbol;
        this.function = function;
    }

    public String symbol() {
        return symbol;
    }

    /**
     * Runs this operation on two numbers.
     *
     * <p>The order of the arguments matters for {@link #SUBTRACT}, {@link #DIVIDE}
     * and {@link #POWER}: {@code SUBTRACT.apply(9, 4)} is {@code 5}, not {@code -5}.
     *
     * @param left  the first number, the one typed before the operator key
     * @param right the second number, the one typed after the operator key
     * @return the result, or {@link Double#NaN} when it is undefined (for example
     *         when dividing by zero). Very large results can also be infinite;
     *         check with {@link Double#isInfinite(double)}.
     */
    public double apply(double left, double right) {
        return function.applyAsDouble(left, right);
    }
}
