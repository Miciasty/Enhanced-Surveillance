package nsk.enhanced.System.Utils;

import nsk.enhanced.System.Configuration.ServerConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Character;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link DevTools} class provides a collection of developer tools for
 * simulating and managing within the plugin system.
 */
public class DevTools {

    private static final FileConfiguration config = ServerConfiguration.getConfig();

    /**
     * Checks if the {@link DevTools} feature is enabled in the configuration.
     *
     * @return {@code true} if {@link DevTools} is enabled, {@code false} otherwise.
     */
    public static boolean isActive() {
        return config.getBoolean("EnhancedSurveillance.DevTools.enabled", false);
    }

    /**
     * Simulates a new event for the specified {@link Player}. Depending on the
     * provided parameters, it may also log additional event data.
     *
     * @param player The {@link Player} for whom the event is simulated.
     * @param isEventData If {@code true}, additional event data will be logged.
     */
    public static void simulateNewEvent(Player player, boolean isEventData) {
        if (!isActive()) return;

        Map<String, String> eventData = new LinkedHashMap<>();

        if (isEventData) {
            eventData.put("N1", "simulation");
            eventData.put("N2", player.getName());

            EnhancedLogger.log().config("N2: <red>simulation</red>");
            EnhancedLogger.log().config("N2: <red>" + player.getName() + "</red>");
        }

        try {
            Event e = new Event("test", player, eventData);

            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });

        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save <green>DevTools/simulateNewEvent</green> - " + ex.getMessage());
        }
    }

    /**
     * Loads the last {@link Event} of a specified type for the given {@link Player}.
     *
     * @param player The {@link Player} for whom the event is loaded.
     * @param type The type of {@link Event} to load.
     * @return The last {@link Event} of the specified type, or {@code null} if none exists or {@link DevTools} is not active.
     */
    public static Event simulateLoadEventByType(Player player, String type) {
        if (!isActive()) return null;

        return Event.getLastEventByType( Character.getCharacter(player), type);
    }


}
