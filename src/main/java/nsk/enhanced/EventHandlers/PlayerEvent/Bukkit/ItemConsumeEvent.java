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
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemConsumeEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {

        if (!config.getBoolean("events.PlayerItemConsumeEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerItemConsumeEvent.level", 0);
        if (Check.inRange(1, 2, level)) {

            eventData.put( EventData.ITEM.name(),               event.getItem().toString());

            EnhancedLogger.log().config(EventData.ITEM + ": <gold>" + event.getItem());

            if (level > 1 && event.getReplacement() != null) {

                eventData.put( EventData.HAND.name(),           event.getHand().name());
                eventData.put( EventData.REPLACEMENT.name(),    event.getReplacement().getType().name());

                EnhancedLogger.log().config(EventData.HAND.name() + ": <gold>" + event.getHand().name());
                EnhancedLogger.log().config(EventData.REPLACEMENT.name() + ": <gold>" + event.getReplacement().getType().name());

            }

        } else if (!Check.inRange(0, 2, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerItemConsumeEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }

        Event e = new Event("itemConsume", player, eventData);

        try {
            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/itemConsume - " + ex.getMessage());
        }
    }
}
