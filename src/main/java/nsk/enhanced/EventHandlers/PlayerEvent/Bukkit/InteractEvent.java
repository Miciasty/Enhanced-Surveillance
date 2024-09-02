package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Character;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link InteractEvent} class listens for the {@link PlayerInteractEvent} in Minecraft and handles the event
 * based on the configuration. It captures data about player interactions, such as actions, items,
 * blocks, and nearby redstone components.
 */
public class InteractEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerInteractEvent}. This method processes player interactions, capturing relevant data
     * depending on the configured detail level.
     *
     * @param event the {@link PlayerInteractEvent} triggered when a player interacts with a block or item
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (!config.getBoolean("events.PlayerInteractEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR) {
            return;
        }

        Material mainHand = player.getInventory().getItemInMainHand().getType();
        Material offHand = player.getInventory().getItemInOffHand().getType();

        EnhancedLogger.log().info("Hand: <aqua>" + event.getHand());
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (!shouldContinue(mainHand, offHand, event.getHand())) return;
        }

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerInteractEvent.level", 0);
        if (Check.inRange(1, 3, level)) {

            if ( event.getHand() == EquipmentSlot.HAND ) {

                eventData.put("action",             action.name().toUpperCase() );
                EnhancedLogger.log().config("action: <gold>" + action.name().toUpperCase());

                if (level > 1) {
                    if (event.getItem() != null && !event.getItem().getType().equals(Material.AIR)) {
                        eventData.put("item",            event.getItem().toString().toUpperCase() );

                        EnhancedLogger.log().config("Item: <gold>" + event.getItem().toString().toUpperCase());
                    }
                }

            }

            if ( event.getHand() == EquipmentSlot.OFF_HAND ) {

                if (event.getItem() == null || event.getItem().getType().equals(Material.AIR)) {
                    return;
                }

                eventData.put("action",             action.name().toUpperCase() );
                EnhancedLogger.log().config("Offhand Action: <gold>" + action.name().toUpperCase());

                if (level > 1) {

                    eventData.put("item",            event.getItem().toString().toUpperCase() );
                    EnhancedLogger.log().config("Item: <gold>" + event.getItem().toString().toUpperCase());

                }
            }

            if (level > 1) {

                if (block != null) {
                    eventData.put("event_block",     block.getType().toString().toUpperCase() );

                    EnhancedLogger.log().config("Block: <gold>" + block.getType().toString().toUpperCase());
                }
            }

            if (level > 2) {
                if (block != null) {

                    eventData.put("event_face",     event.getBlockFace().toString().toUpperCase() );

                    EnhancedLogger.log().config("BlockFace: <gold>" + event.getBlockFace().toString().toUpperCase());

                    boolean rdst_nrb = false;

                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dz = -2; dz <= 2; dz++) {
                            Block relativeBlock = block.getRelative(dx, 0, dz);
                            Material type = relativeBlock.getType();

                            if (type == Material.REDSTONE_WIRE || type == Material.REPEATER || type == Material.REDSTONE_TORCH || type == Material.REDSTONE_BLOCK) {
                                rdst_nrb = true;
                                break;
                            }
                        }
                        if (rdst_nrb) break;
                    }

                    eventData.put("rdst_nrb",       String.valueOf(rdst_nrb).toUpperCase());

                    EnhancedLogger.log().config("Redstone nearby: <gold>" + rdst_nrb);
                }
            }

        } else if (!Check.inRange(0, 3, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerInteractEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }

        Location location;

        if (block != null) {
            location = block.getLocation();
        } else {
            location = null;
        }

        try {

            Event lastEvent = Event.getLastEventByType(Character.getCharacter(player), "interact");

            if (lastEvent != null) {

                Map<String, String> lastEventData = lastEvent.getDecompressedEventData();

                if (lastEventData != null && "LEFT_CLICK_BLOCK".equals(lastEventData.get("action")) && "LEFT_CLICK_AIR".equals(action.name()) ) {
                    return;
                }

                if (lastEventData != null && "RIGHT_CLICK_BLOCK".equals(lastEventData.get("action")) && "RIGHT_CLICK_AIR".equals(action.name()) ) {
                    return;
                }
            }

            Event e;

            if (location != null) {
                e = new Event("interact", player, location, eventData);
            } else {
                e = new Event("interact", player, eventData);
            }

            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });

        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/interact - " + e.getMessage());
        }

    }

    private static boolean shouldContinue(Material mainHand, Material offHand, EquipmentSlot hand) {

        boolean mainUse = Check.isPlaceableOrUsable(mainHand);
        boolean offUse = Check.isPlaceableOrUsable(offHand);

        if (hand == EquipmentSlot.HAND) {

            if (offUse) {
                return mainUse;
            }
            return true;
        }

        if (hand == EquipmentSlot.OFF_HAND) {
            return !mainUse && offUse;
        }

        return false;

    }
}
