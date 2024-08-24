package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
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

    private static final FileConfiguration config = ES.getInstance().getBukkitEventsFile();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (config.getBoolean("events.PlayerInteractEvent.enabled", false)) {

            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            Action action = event.getAction();

            Location location;

            Map<String, String> eventData = new LinkedHashMap<>();

            int level = config.getInt("events.PlayerInteractEvent.level", 0);
            if ( level > 0 && level < 4) {

                if ( event.getHand() == EquipmentSlot.HAND ) {
                    eventData.put("action",             action.name().toUpperCase() );
                } else {
                    return;
                }

                if (level > 1) {
                    if (event.getItem() != null || !event.getItem().getType().equals(Material.AIR)) {
                        eventData.put("item",            event.getItem().toString().toUpperCase() );
                    }

                    if (block != null) {
                        eventData.put("event_block",     block.getType().toString().toUpperCase() );
                    }
                }

                if (level > 2) {
                    if (block != null) {

                        eventData.put("event_face",     event.getBlockFace().toString().toUpperCase() );

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
                    }
                }

            } else if (level < 0 || level > 3) {
                ES.getInstance().getEnhancedLogger().warning("<green>'events.PlayerChatEvent.level'</green> can only be set to a maximum of 2. The provided value is invalid, so the event will default to level 0.");
            }

            if (block != null) {
                location = block.getLocation();
            } else {
                location = null;
            }

            try {

                Event lastEvent = Event.getLastEventByType(Character.getCharacter(player), "interact");
                Map<String, String> lastEventData = lastEvent.getDecompressedEventData();

                if (lastEventData != null && "LEFT_CLICK_BLOCK".equals(lastEventData.get("action")) && "LEFT_CLICK_AIR".equals(action.name()) ) {
                    return;
                }

                if (lastEventData != null && "RIGHT_CLICK_BLOCK".equals(lastEventData.get("action")) && "RIGHT_CLICK_AIR".equals(action.name()) ) {
                    return;
                }

                Event e;

                if (location != null) {
                    e = new Event("interact", player, location, eventData);
                } else {
                    e = new Event("interact", player, eventData);
                }

                MonitorManager.saveEvent(e);
            } catch (Exception e) {
                ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/interact - " + e.getMessage());
            }

        }

    }
}
