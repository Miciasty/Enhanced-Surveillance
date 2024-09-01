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
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class RespawnEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

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
                flag = "anchor";
            } else if (event.isBedSpawn()) {
                flag = "bed";
            } else {
                flag = null;
            }

            eventData.put("flag", flag);
            EnhancedLogger.log().config("flag: <red>" + flag + "</red>");

            if (level > 1) {
                String reason = event.getRespawnReason().toString();

                eventData.put("reason", reason);
                EnhancedLogger.log().config("reason: <green>" + reason + "</green>");
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
