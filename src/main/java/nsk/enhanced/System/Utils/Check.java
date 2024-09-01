package nsk.enhanced.System.Utils;

/**
 * The {@link Check} class provides a collection of static utility methods performing boolean checks.
 */
public class Check {

    /**
     * <p>
     * Checks if a given value is within a specified range (inclusive).
     * </p>
     *
     * <p>Example usage:</p>
     * <pre>
     *    boolean result =  Check.inRange(0, 3, 5)   // false;
     *    boolean result = !Check.inRange(0, 3, 5)   // true;
     * </pre>
     *
     * @param min the minimum value of the range (inclusive)
     * @param max the maximum value of the range (inclusive)
     * @param value the value to check
     * @return true if the value within the range <strong>[min, max]</strong>, false otherwise
     */
    public static boolean inRange(double min, double max, double value) {
        return min <= value && value <= max;
    }
}
