package nsk.enhanced.System.Utils;

/**
 * The {@link Check} class provides a collection of static utility methods performing boolean checks.
 */
public class Check {

    /**
     * <p>
     * Checks if a given value is either within or outside a specified range.
     * </p>
     *
     * <p>Example usage:</p>
     * <pre>
     *    boolean result = Check.inRange(0, 3, true, 5)   // false;
     *    boolean result = Check.inRange(0, 3, true, 2)   // true;
     *
     *    boolean result = Check.inRange(0, 3, false, 1)  // false;
     *    boolean result = Check.inRange(0, 3, false, -2) // true;
     * </pre>
     *
     * @param a the minimum value of the range
     * @param b the maximum value of the range
     * @param in if true, checks if the value is inside the range (inclusive),
     *           if false, checks if the value is outside the range (inclusive)
     * @param value value to check
     * @return true if the value meets the criteria (inside or outside the range) as specified by the `in` parameter, false otherwise
     */
    public static boolean inRange(double a, double b, boolean in, double value) {
        if (in) {
            return a <= value && value <= b;
        } else {
            return value <= a || b <= value;
        }
    }
}
