package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import org.bukkit.Location;
import org.bukkit.block.Bed;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class BedLeaveEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {

        if (!config.getBoolean("events.PlayerBedLeaveEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerBedLeaveEvent.level", 0);
        if (level == 1) {

            Bed bed = (Bed) event.getBed();
            String color = bed.getColor().getColor().toString();

            eventData.put("color", color);

            EnhancedLogger.log().config("color: <gold>" + color + "</gold>");

        } else if (!Check.inRange(0, 1, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerBedLeaveEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }

        Location location = event.getBed().getLocation();

        Event e = new Event("bedLeave", player, location, eventData);

        try {
            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/bedLeave - " + ex.getMessage());
        }
    }

}
