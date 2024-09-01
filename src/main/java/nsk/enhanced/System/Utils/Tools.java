package nsk.enhanced.System.Utils;

import org.bukkit.Location;

public class Tools {

    /**
     * Returns a simplified string representation of the given {@link Location}.
     * The world name and coordinates (x, y, z) are included, with coordinates
     * rounded to 2 decimal places.
     *
     * @param l The {@link Location} to be simplified.
     * @return A string representing the location.
     */
    public static String getSimplifiedLocation(Location l) {

        return  l.getWorld().getName() + "," +
                roundTo(l.getX(), 2) + "," +
                roundTo(l.getY(), 2) + "," +
                roundTo(l.getZ(), 2);
    }

    /**
     * Rounds the given value to the specified number of decimal places.
     *
     * @param value The double value to be rounded.
     * @param places The number of decimal places to keep.
     * @return The rounded double value.
     */
    public static double roundTo(double value, int places) {
        if (places < 0) throw new IllegalArgumentException("Decimal places must be non-negative.");

        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

}
