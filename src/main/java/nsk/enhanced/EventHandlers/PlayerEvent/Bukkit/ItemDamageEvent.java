package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.EventHandlers.PlayerEvent.Bukkit.Enum.EventData;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import nsk.enhanced.System.Utils.Tools;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link ItemDamageEvent} class listens for the {@link PlayerItemDamageEvent} in Minecraft and handles the event
 * based on the configuration. It captures data about the damage dealt to the item, including the original damage value,
 * and logs the event accordingly.
 */
public class ItemDamageEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerItemDamageEvent}. This method processes the event where a player's item takes damage,
     * capturing relevant data depending on the configured detail level.
     *
     * @param event the {@link PlayerItemDamageEvent} triggered when a player's item takes damage
     */
    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {

        if (!config.getBoolean("events.PlayerItemDamageEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerItemDamageEvent.level", 0);
        if (Check.inRange(1, 2, level)) {

            eventData.put( EventData.ITEM.name(),                event.getItem().getType().name());

            EnhancedLogger.log().config(EventData.ITEM      + ": <gold>" + event.getItem().getType().name());

            if (level > 1) {

                eventData.put( EventData.DAMAGE.name(),             String.valueOf( Tools.roundTo(event.getDamage(), 3) ));
                eventData.put( EventData.ORIGINAL_DAMAGE.name(),    String.valueOf( Tools.roundTo(event.getOriginalDamage(), 3) ));

                EnhancedLogger.log().config(EventData.DAMAGE.name()             + ": <gold>" + Tools.roundTo(event.getDamage(), 3) );
                EnhancedLogger.log().config(EventData.ORIGINAL_DAMAGE.name()    + ": <gold>" + Tools.roundTo(event.getOriginalDamage(), 3) );

            }

        } else if (!Check.inRange(0, 2, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerItemDamageEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }

        Event e = new Event("itemDamage", player, eventData);

        try {
            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/itemDamage - " + ex.getMessage());
        }
    }

}
