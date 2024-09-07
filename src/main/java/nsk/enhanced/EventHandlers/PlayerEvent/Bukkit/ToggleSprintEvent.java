package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.EventHandlers.EventData;
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
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link ToggleSprintEvent} class listens for the {@link PlayerToggleSprintEvent} in Minecraft and handles the event
 * based on the configuration. It captures data when a player toggles their sprinting mode, such as whether
 * the player is sprinting or not.
 */
public class ToggleSprintEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerToggleSprintEvent}. This method processes the event when a player toggles their sprinting mode,
     * capturing relevant data based on the configured detail level.
     *
     * @param event the {@link PlayerToggleSprintEvent} triggered when a player starts or stops sprinting
     */
    @EventHandler
    public void onPlayerSprint(PlayerToggleSprintEvent event) {

        if (!config.getBoolean("events.PlayerToggleSprintEvent.enabled", false)) {
            return;
        }

        Player player  = event.getPlayer();
        boolean isSprinting = event.isSprinting();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerToggleSprintEvent.level", 0);
        if ( level == 1 ) {

            if (isSprinting) {
                eventData.put(EventData.SPRINT.name(), "TRUE");
                EnhancedLogger.log().config(EventData.SPRINT.name() + ": <red>TRUE</red>");
            } else {
                eventData.put(EventData.SPRINT.name(), "FALSE");
                EnhancedLogger.log().config(EventData.SPRINT.name() + ": <red>FALSE</red>");
            }

        } else if (!Check.inRange(0, 1, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerToggleSprintEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }


        try {

            Event e = new Event("toggleSprint", player, eventData);

            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });

        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/toggleSprint - " + ex.getMessage());
        }

    }
}
