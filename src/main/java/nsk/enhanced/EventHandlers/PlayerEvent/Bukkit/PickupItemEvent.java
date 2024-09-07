package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.EventHandlers.EventData;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link PickupItemEvent} class listens for the {@link PlayerPickupItemEvent} in Minecraft and handles the event
 * based on the configuration. It captures data when a player picks up an item, such as the item's name,
 * amount, and other attributes like whether it can be picked up by mobs or players.
 */
public class PickupItemEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerPickupItemEvent}. This method processes the event when a player picks up an item,
     * capturing relevant data based on the configured detail level.
     *
     * @param event the {@link PlayerPickupItemEvent} triggered when a player picks up an item
     */
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

        if (!config.getBoolean("events.PlayerPickupItemEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerPickupItemEvent.level", 0);
        if (Check.inRange(1, 3, level)) {

            Item item = event.getItem();
            ItemStack itemStack = item.getItemStack();

            eventData.put(EventData.ITEM.name(),                    itemStack.getType().name());
            eventData.put(EventData.AMOUNT.name(),                  String.valueOf(itemStack.getAmount()).toUpperCase());

            EnhancedLogger.log().config(EventData.ITEM.name()               + ": <red>" + itemStack.getType().name() + "</red>");
            EnhancedLogger.log().config(EventData.AMOUNT.name()             + ": <green>" + itemStack.getAmount() + "</green>");

            if (level > 1) {
                eventData.put(EventData.MOB_PICKUP.name(),          String.valueOf(item.canMobPickup()).toUpperCase() );
                eventData.put(EventData.PLAYER_PICKUP.name(),       String.valueOf(item.canPlayerPickup()).toUpperCase() );

                EnhancedLogger.log().config(EventData.MOB_PICKUP.name()     + ": <red>" + item.canMobPickup() + "</red>");
                EnhancedLogger.log().config(EventData.PLAYER_PICKUP.name()  + ": <red>" + item.canPlayerPickup() + "</red>");
            }

            if (level > 2) {
                eventData.put(EventData.DURABILITY.name(),          String.valueOf(itemStack.getDurability()).toUpperCase());

                EnhancedLogger.log().config(EventData.DURABILITY.name()     + ": <red>" + itemStack.getDurability() + "</red>");
            }

        } else if (!Check.inRange(0, 3, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerPickupItemEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }


        Event e = new Event("pickupItem", player, player.getLocation(), eventData);

        try {
            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/pickupItem - " + ex.getMessage());
        }
    }

}
