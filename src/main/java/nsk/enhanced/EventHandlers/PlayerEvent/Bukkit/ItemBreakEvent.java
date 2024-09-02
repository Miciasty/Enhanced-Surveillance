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
import org.bukkit.event.player.PlayerItemBreakEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemBreakEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    @EventHandler
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {

        if (!config.getBoolean("events.PlayerItemBreakEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerItemBreakEvent.level", 0);
        if (level == 1) {

            eventData.put( EventData.ITEM.name(),                event.getBrokenItem().getType().name() );

            EnhancedLogger.log().config(EventData.ITEM      + ": <gold>" + event.getBrokenItem().getType().name());

        } else if (!Check.inRange(0, 1, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerItemBreakEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }

        Event e = new Event("itemBreak", player, eventData);

        try {
            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/itemBreak - " + ex.getMessage());
        }
    }

}
