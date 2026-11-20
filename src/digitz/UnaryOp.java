package digitz;

import java.util.function.DoubleUnaryOperator;

/**
 * The calculator operations that act on a <em>single</em> number, such as
 * {@code √9} or {@code sin(30)}.
 *
 * <h2>How it is used</h2>
 * <p>When the user presses one of these keys, {@link CalculatorEngine#unary(UnaryOp)}
 * takes the number currently on the display, calls {@link #apply(double)} on it and
 * shows the result straight away. No {@code =} is needed.
 *
 * <h2>Angles are in degrees</h2>
 * <p>{@link #SIN}, {@link #COS} and {@link #TAN} treat their input as
 * <strong>degrees</strong>, which is what most people expect from a desktop calculator.
 *
 * <h2>Invalid results</h2>
 * <p>An operation never throws an exception. When the input is outside the
 * operation's domain, such as the square root of a negative number, it returns
 * {@link Double#NaN}. The engine then shows {@link CalculatorEngine#ERROR_TEXT}.
 *
 * <h2>Example</h2>
 * {@snippet :
 * double root  = UnaryOp.SQUARE_ROOT.apply(81);      // 9.0
 * String text  = UnaryOp.SQUARE_ROOT.describe("81"); // "√(81)"
 * double error = UnaryOp.LOG10.apply(-1);            // NaN
 * }
 *
 * @see BinaryOp
 * @see CalculatorEngine#unary(UnaryOp)
 */
public enum UnaryOp {

    /**
     * Square: {@code x × x}.
     * <p>Button label {@code x²}. Example: {@code sqr(12) = 144}.
     */
    SQUARE("x²", value -> value * value),

    /**
     * Square root: {@code √x}.
     * <p>Button label {@code √}. Negative inputs return {@link Double#NaN}.
     * Example: {@code √(81) = 9}.
     */
    SQUARE_ROOT("√", value -> value < 0.0 ? Double.NaN : Math.sqrt(value)),

    /**
     * Reciprocal: {@code 1 ÷ x}.
     * <p>Button label {@code 1/x}. An input of zero returns {@link Double#NaN}.
     * Example: {@code 1/(4) = 0.25}.
     */
    RECIPROCAL("1/x", value -> value == 0.0 ? Double.NaN : 1.0 / value),

    /**
     * Sine of an angle given in <strong>degrees</strong>.
     * <p>Button label {@code sin}. Example: {@code sin(30) = 0.5}.
     */
    SIN("sin", value -> Math.sin(Math.toRadians(value))),

    /**
     * Cosine of an angle given in <strong>degrees</strong>.
     * <p>Button label {@code cos}. Example: {@code cos(60) = 0.5}.
     */
    COS("cos", value -> Math.cos(Math.toRadians(value))),

    /**
     * Tangent of an angle given in <strong>degrees</strong>.
     * <p>Button label {@code tan}. Angles where the tangent is undefined
     * ({@code 90}, {@code 270}, {@code -90} and so on) return {@link Double#NaN}.
     * Example: {@code tan(45) = 1}.
     */
    TAN("tan", UnaryOp::tangentInDegrees),

    /**
     * Base-10 logarithm: the power of 10 that gives {@code x}.
     * <p>Button label {@code log}. Zero and negative inputs return {@link Double#NaN}.
     * Example: {@code log(1000) = 3}.
     */
    LOG10("log", value -> value <= 0.0 ? Double.NaN : Math.log10(value)),

    /**
     * Natural logarithm: the power of <i>e</i> (about 2.718) that gives {@code x}.
     * <p>Button label {@code ln}. Zero and negative inputs return {@link Double#NaN}.
     * Example: {@code ln(1) = 0}.
     */
    LN("ln", value -> value <= 0.0 ? Double.NaN : Math.log(value)),

    /**
     * Percent: divides {@code x} by 100.
     * <p>Button label {@code %}. Example: {@code 25% = 0.25}.
     */
    PERCENT("%", value -> value / 100.0),

    /**
     * Absolute value: {@code x} without its sign.
     * <p>Button label {@code |x|}. Example: {@code |-7| = 7}.
     */
    ABSOLUTE("|x|", Math::abs),

    /**
     * Negation: flips the sign of {@code x}.
     * <p>Button label {@code ±}. While the user is still typing a number, the engine
     * just toggles the minus sign so typing can continue.
     * See {@link CalculatorEngine#unary(UnaryOp)}. Example: {@code negate(5) = -5}.
     */
    NEGATE("±", value -> -value);

    // cos(90°) is never exactly 0 in floating point, so values this small count as 0.
    private static final double COSINE_EPSILON = 1e-12;

    private final String label;
    private final DoubleUnaryOperator function;

    UnaryOp(String label, DoubleUnaryOperator function) {
        this.label = label;
        this.function = function;
    }

    /**
     * Returns the text shown on this operation's button.
     *
     * @return the button label, never {@code null}; for example {@code "x²"} for {@link #SQUARE}
     */
    public String label() {
        return label;
    }

    /**
     * Runs this operation on a number.
     *
     * @param value the number currently on the display
     * @return the result, or {@link Double#NaN} when the input is outside the
     *         operation's domain (for example {@code √} of a negative number,
     *         {@code log} of zero, or {@code tan(90)})
     */
    public double apply(double value) {
        return function.applyAsDouble(value);
    }

    /**
     * Writes this operation and its operand as readable text for the history line
     * above the display.
     *
     * <p>Most operations use the form {@code label(operand)}, such as
     * {@code "sin(30)"}. A few use a more natural form:
     * <ul>
     *   <li>{@link #SQUARE}: {@code "sqr(12)"}</li>
     *   <li>{@link #RECIPROCAL}: {@code "1/(4)"}</li>
     *   <li>{@link #PERCENT}: {@code "25%"}</li>
     *   <li>{@link #ABSOLUTE}: {@code "|-7|"}</li>
     *   <li>{@link #NEGATE}: {@code "negate(5)"}</li>
     * </ul>
     *
     * @param operand the operand, already formatted for display, for example {@code "9"}
     * @return the readable expression, for example {@code "√(9)"}; never {@code null}
     */
    public String describe(String operand) {
        return switch (this) {
            case SQUARE -> "sqr(" + operand + ")";
            case RECIPROCAL -> "1/(" + operand + ")";
            case PERCENT -> operand + "%";
            case ABSOLUTE -> "|" + operand + "|";
            case NEGATE -> "negate(" + operand + ")";
            default -> label + "(" + operand + ")";
        };
    }

    private static double tangentInDegrees(double degrees) {
        double radians = Math.toRadians(degrees);
        return Math.abs(Math.cos(radians)) < COSINE_EPSILON ? Double.NaN : Math.tan(radians);
    }
}
