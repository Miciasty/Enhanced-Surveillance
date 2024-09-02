package nsk.enhanced.System.Utils;

import org.bukkit.Material;

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

    /**
     * Determines if a given {@link Material} is either placeable, usable, or interactable.
     *
     * <p>This method checks whether the specified {@link Material} meets any of the following conditions:
     * <ul>
     *     <li>It is a block ({@link Material#isBlock()}).</li>
     *     <li>It is edible ({@link Material#isEdible()}).</li>
     *     <li>It is interactable ({@link Material#isInteractable()}).</li>
     * </ul>
     * If any of these conditions are true, the method returns {@code true}; otherwise, it returns {@code false}.
     *
     * @param hand the {@link Material} to check
     * @return {@code true} if the material is placeable, edible, or interactable; {@code false} otherwise
     */
    public static boolean isPlaceableOrUsable(Material hand) {

        if (hand == Material.AIR) return false;

        if (hand.isBlock()) return true;
        if (hand.isEdible()) return true;
        if (hand.isInteractable()) return true;

        return false;
    }
}
