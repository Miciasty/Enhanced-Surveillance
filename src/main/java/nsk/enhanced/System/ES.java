package nsk.enhanced.System;

import nsk.enhanced.EnhancedSurveillance;

/**
 * The ES serves as a singleton holder for the plugin's main instance.
 * It provides global access to the {@link EnhancedSurveillance} instance.
 */
public class ES {

    private static final boolean DEBUG = true;

    private static EnhancedSurveillance instance;

    /**
     * Returns the current instance of the {@link EnhancedSurveillance} plugin.
     * @return the current instance
     */
    public static EnhancedSurveillance getInstance() {
        return instance;
    }

    /**
     * Sets the instance of the {@link EnhancedSurveillance} plugin.
     * @param plugin the instance to be set
     */
    public static void setInstance(EnhancedSurveillance plugin) {
        instance = plugin;
    }

    /**
     * Checks if plugin is running in debug mode.
     * @return true if debug mode is enabled, false otherwise
     */
    public static boolean debugMode() {
        return DEBUG;
    }

}