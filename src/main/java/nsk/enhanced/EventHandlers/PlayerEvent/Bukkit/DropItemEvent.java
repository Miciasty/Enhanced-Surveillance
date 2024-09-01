package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link DropItemEvent} class listens for the {@link PlayerDropItemEvent} in Minecraft and handles the event
 * based on the configuration. It captures data when a player drops an item, such as the item's name,
 * amount, and other attributes like whether it can be picked up by mobs or players.
 */
public class DropItemEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerDropItemEvent}. This method processes the event when a player drops an item,
     * capturing relevant data based on the configured detail level.
     *
     * @param event the {@link PlayerDropItemEvent} triggered when a player drops an item
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (!config.getBoolean("events.PlayerDropItemEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerDropItemEvent.level", 0);
        if (Check.inRange(1, 3, level)) {

            Item item = event.getItemDrop();
            ItemStack itemStack = item.getItemStack();

            eventData.put("itemName",           itemStack.getType().name());
            eventData.put("amount",             String.valueOf(itemStack.getAmount()).toUpperCase());

            EnhancedLogger.log().config("itemName: <red>" + itemStack.getType().name() + "</red>");
            EnhancedLogger.log().config("amount: <green>" + itemStack.getAmount() + "</green>");

            if (level > 1) {
                eventData.put("mobPickup",          String.valueOf(item.canMobPickup()).toUpperCase() );
                eventData.put("playerPickup",       String.valueOf(item.canPlayerPickup()).toUpperCase() );

                EnhancedLogger.log().config("mobPickup: <red>" + item.canMobPickup() + "</red>");
                EnhancedLogger.log().config("playerPickup: <red>" + item.canPlayerPickup() + "</red>");
            }

            if (level > 2) {
                eventData.put("itemDamage",         String.valueOf(itemStack.getDurability()).toUpperCase());

                EnhancedLogger.log().config("itemDamage: <red>" + itemStack.getDurability() + "</red>");
            }


        } else if (!Check.inRange(0, 3, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerDropItemEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }

        Location location = event.getItemDrop().getLocation();

        Event e = new Event("dropItem", player, location, eventData);

        try {
            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/dropItem - " + ex.getMessage());
        }
    }

}
