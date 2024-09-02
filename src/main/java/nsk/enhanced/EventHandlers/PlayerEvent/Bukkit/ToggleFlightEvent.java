package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.EventHandlers.PlayerEvent.Bukkit.Enum.EventData;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link ToggleFlightEvent} class listens for the {@link PlayerToggleFlightEvent} in Minecraft and handles the event
 * based on the configuration. It captures data when a player toggles their flight mode, such as whether
 * the player is flying or not.
 */
public class ToggleFlightEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerToggleFlightEvent}. This method processes the event when a player toggles their flight mode,
     * capturing relevant data based on the configured detail level.
     *
     * @param event the {@link PlayerToggleFlightEvent} triggered when a player starts or stops flying
     */
    @EventHandler
    public void onPlayerFlight(PlayerToggleFlightEvent event) {

        if (!config.getBoolean("events.PlayerToggleFlightEvent.enabled", false)) {
            return;
        }

        Player player  = event.getPlayer();
        boolean isFlying = event.isFlying();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerToggleFlightEvent.level", 0);
        if ( level == 1 ) {

            if (isFlying) {
                eventData.put(EventData.FLIGHT.name(), "TRUE");
                EnhancedLogger.log().config(EventData.FLIGHT.name() + ": <red>TRUE</red>");
            } else {
                eventData.put(EventData.FLIGHT.name(), "FALSE");
                EnhancedLogger.log().config(EventData.FLIGHT.name() + ": <red>FALSE</red>");
            }

        } else if (!Check.inRange(0, 1, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerToggleFlightEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }


        try {

            Event e = new Event("toggleFlight", player, eventData);

            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });

        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/toggleFlight - " + ex.getMessage());
        }

    }
}
