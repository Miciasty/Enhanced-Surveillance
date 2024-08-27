package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.Configuration.ServerConfiguration;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Character;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.LinkedHashMap;
import java.util.Map;

public class InteractEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (!config.getBoolean("events.PlayerInteractEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Action action = event.getAction();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerInteractEvent.level", 0);
        if ( level > 0 && level < 4) {

            if ( event.getHand() == EquipmentSlot.HAND ) {
                eventData.put("action",             action.name().toUpperCase() );
                if (ES.debugMode()) EnhancedLogger.log().info("Action: <gold>" + action.name().toUpperCase());

                if (level > 1) {
                    if (event.getItem() != null && !event.getItem().getType().equals(Material.AIR)) {
                        eventData.put("item",            event.getItem().toString().toUpperCase() );

                        if (ES.debugMode()) EnhancedLogger.log().info("Item: <gold>" + event.getItem().toString().toUpperCase());
                    }
                }

            } else if ( event.getHand() == EquipmentSlot.OFF_HAND ) {
                eventData.put("action",             action.name().toUpperCase() );
                if (ES.debugMode()) EnhancedLogger.log().info("Offhand Action: <gold>" + action.name().toUpperCase());

                if (level > 1) {
                    if (event.getItem() != null && !event.getItem().getType().equals(Material.AIR)) {
                        eventData.put("item",            event.getItem().toString().toUpperCase() );

                        if (ES.debugMode()) EnhancedLogger.log().info("Item: <gold>" + event.getItem().toString().toUpperCase());
                    }
                }
            } else {
                return;
            }

            if (level > 1) {

                if (block != null) {
                    eventData.put("event_block",     block.getType().toString().toUpperCase() );

                    if (ES.debugMode()) EnhancedLogger.log().info("Block: <gold>" + block.getType().toString().toUpperCase());
                }
            }

            if (level > 2) {
                if (block != null) {

                    eventData.put("event_face",     event.getBlockFace().toString().toUpperCase() );

                    if (ES.debugMode()) EnhancedLogger.log().info("BlockFace: <gold>" + event.getBlockFace().toString().toUpperCase());

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

                    if (ES.debugMode()) EnhancedLogger.log().info("Redstone nearby: <gold>" + rdst_nrb);
                }
            }

        } else if (level < 0 || level > 3) {
            EnhancedLogger.log().warning("<green>'events.PlayerChatEvent.level'</green> can only be set to a maximum of 2. The provided value is invalid, so the event will default to level 0.");
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

            MonitorManager.saveEvent(e);

        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/interact - " + e.getMessage());
        }

    }
}
