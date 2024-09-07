package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.EventHandlers.EventData;
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
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link RespawnEvent} class listens for the {@link PlayerRespawnEvent} in Minecraft and handles the event
 * based on the configuration. It captures data when a player respawns, such as the respawn location,
 * the reason for respawning, and whether the respawn was triggered by an anchor or a bed.
 */
public class RespawnEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerRespawnEvent}. This method processes the event when a player respawns,
     * capturing relevant data based on the configured detail level.
     *
     * @param event the {@link PlayerRespawnEvent} triggered when a player respawns
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        if (!config.getBoolean("events.PlayerRespawnEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();
        Location location = event.getRespawnLocation();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerRespawnEvent.level", 0);
        if (Check.inRange(1, 2, level)) {
            String flag;

            if (event.isAnchorSpawn()) {
                flag = EventData.ANCHOR.name();
            } else if (event.isBedSpawn()) {
                flag = EventData.BED.name();
            } else {
                flag = null;
            }

            eventData.put(EventData.FLAG.name(), flag);
            EnhancedLogger.log().config(EventData.FLAG.name() + ": <red>" + flag + "</red>");

            if (level > 1) {
                String cause = event.getRespawnReason().name();

                eventData.put(EventData.CAUSE.name(), cause);
                EnhancedLogger.log().config(EventData.CAUSE.name() + ": <green>" + cause + "</green>");
            }

        } else if (!Check.inRange(0, 2, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerRespawnEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }


        Event e = new Event("respawn", player, location, eventData);

        try {
            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/respawn - " + ex.getMessage());
        }
    }
}
