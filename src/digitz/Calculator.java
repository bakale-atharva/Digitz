/**
 * @name Digitz
 * @package atharvabakale.calculator
 * @file Calculator.java
 * @author Atharva Bakale
 * @link https://github.com/bakale-atharva
 */

package digitz;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

public class Calculator {
    public enum BiOperatorModes {
        normal, add, minus, multiply, divide, xpowerofy
    }

    public enum BioOperatorModes {
        normal, add, minus, multiply, divide, xpowerofy
    }

    public enum MonoOperatorModes {
        square, squareRoot, oneDividedBy, cos, sin, tan, ln, log, rate, abs
    }

    private double storedValue;
    private BiOperatorModes pendingBiOperator;
    private boolean hasPendingBiOperator;

    public Calculator() {
        reset();
    }

    public Double calculateBi(BiOperatorModes operator, Double value) {
        if (!hasPendingBiOperator || pendingBiOperator == BiOperatorModes.normal) {
            storedValue = value;
            pendingBiOperator = operator;
            hasPendingBiOperator = true;
            return value;
        }

        double result = applyBiOperation(pendingBiOperator, storedValue, value);
        storedValue = result;
        pendingBiOperator = operator;
        hasPendingBiOperator = true;
        return result;
    }

    public Double calculateBI(BioOperatorModes operator, Double value) {
        return calculateBi(BiOperatorModes.valueOf(operator.name()), value);
    }

    public Double calculateMono(MonoOperatorModes operator, Double value) {
        return applyMonoOperation(operator, value);
    }

    public Double calculateEqual(Double value) {
        if (!hasPendingBiOperator || pendingBiOperator == BiOperatorModes.normal) {
            return value;
        }

        double result = applyBiOperation(pendingBiOperator, storedValue, value);
        storedValue = result;
        pendingBiOperator = BiOperatorModes.normal;
        hasPendingBiOperator = false;
        return result;
    }

    public Double reset() {
        storedValue = 0.0;
        pendingBiOperator = BiOperatorModes.normal;
        hasPendingBiOperator = false;
        return Double.NaN;
    }

    private double applyBiOperation(BiOperatorModes operator, double left, double right) {
        switch (operator) {
            case add:
                return left + right;
            case minus:
                return left - right;
            case multiply:
                return left * right;
            case divide:
                return right == 0.0 ? Double.NaN : left / right;
            case xpowerofy:
                return pow(left, right);
            case normal:
            default:
                return right;
        }
    }

    private double applyMonoOperation(MonoOperatorModes operator, double value) {
        switch (operator) {
            case square:
                return value * value;
            case squareRoot:
                return value < 0.0 ? Double.NaN : sqrt(value);
            case oneDividedBy:
                return value == 0.0 ? Double.NaN : 1.0 / value;
            case cos:
                return cos(value);
            case sin:
                return sin(value);
            case tan:
                return tan(value);
            case ln:
                return value <= 0.0 ? Double.NaN : log(value);
            case log:
                return value <= 0.0 ? Double.NaN : log10(value);
            case rate:
                return value / 100.0;
            case abs:
                return abs(value);
            default:
                return value;
        }
    }
}
