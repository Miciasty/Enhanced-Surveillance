package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

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
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.*;

/**
 * The {@link ToggleSneakEvent} class listens for the {@link PlayerToggleSneakEvent} in Minecraft and handles the event
 * based on the configuration. It captures data when a player toggles their sneaking mode, such as whether
 * the player is sneaking or not.
 */
public class ToggleSneakEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerToggleSneakEvent}. This method processes the event when a player toggles their sneaking mode,
     * capturing relevant data based on the configured detail level.
     *
     * @param event the {@link PlayerToggleSneakEvent} triggered when a player starts or stops sneaking
     */
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {

        if (!config.getBoolean("events.PlayerToggleSneakEvent.enabled", false)) {
            return;
        }

        Player player  = event.getPlayer();
        boolean isSneaking = event.isSneaking();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerToggleSneakEvent.level", 0);
        if ( level == 1 ) {

            if (isSneaking) {
                eventData.put("sneaking", "TRUE");
            } else {
                eventData.put("sneaking", "FALSE");
            }

        } else if (Check.inRange(0, 1, false, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerToggleSneakEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }


        try {

            Event e = new Event("toggleSneak", player, eventData);

            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });

        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/toggleSneak - " + ex.getMessage());
        }

    }

}
