package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link TeleportEvent} class listens for the {@link PlayerTeleportEvent} in Minecraft and handles the event
 * based on the configuration. It captures data when a player teleports, such as the cause of the teleportation
 * and the locations involved.
 */
public class TeleportEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerTeleportEvent}. This method processes the event when a player teleports,
     * capturing relevant data based on the configured detail level.
     *
     * @param event the {@link PlayerTeleportEvent} triggered when a player teleports
     */
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        if (!config.getBoolean("events.PlayerTeleportEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerTeleportEvent.level", 0);
        if (level == 1) {

            String cause = event.getCause().toString();

            eventData.put("cause", cause);
            EnhancedLogger.log().config("cause: <red>" + cause + "</red>");

            EnhancedLogger.log().config("getFrom: <aqua>" + event.getFrom() + "</aqua>");
            EnhancedLogger.log().config("getTo: <aqua>" + event.getTo() + "</aqua>");


        } else if (!Check.inRange(0, 2, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerTeleportEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }

        Location to = event.getTo();

        Event e = new Event("teleport", player, to, eventData);

        try {
            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/teleport - " + ex.getMessage());
        }
    }

}
