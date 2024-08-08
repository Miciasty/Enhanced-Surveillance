package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.LinkedHashMap;
import java.util.Map;

public class InteractEvent implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Action action = event.getAction();
        Location location = event.getInteractionPoint();

        Map<String, Object> eventData = new LinkedHashMap<>();

        if ( event.getHand() == EquipmentSlot.HAND ) {
            eventData.put("action",     action.name().toUpperCase() );
        } else {
            return;
        }

        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR)) {
            eventData.put("item",            "AIR" );
        } else {
            eventData.put("item",            event.getItem().toString() );
        }

        if (block == null) {
            eventData.put("event_block",     "NULL or ENTITY" );
        } else {
            eventData.put("event_block",     block.getType().toString().toUpperCase() );
        }

        if (block != null) {
            eventData.put("event_world",     block.getWorld().getName().toUpperCase() );
            eventData.put("event_location",  String.format("{x: %d, y: %d, z: %d}", block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ() ).toUpperCase() );

        } else if (location == null) {
            eventData.put("event_world",     "NULL" );
            eventData.put("event_location",  "NULL" );

        } else {
            eventData.put("event_world",     location.getWorld().getName().toUpperCase() );
            eventData.put("event_location",  String.format("{x: %d, y: %d, z: %d}", location.getBlockX(), location.getBlockY(), location.getBlockZ() ).toUpperCase() );

        }


        try {

            Map<String, Object> lastEvent = MonitorManager.getEvent(player, "PlayerEvents/interact");

            if (lastEvent != null && "LEFT_CLICK_BLOCK".equals(lastEvent.get("action")) && "LEFT_CLICK_AIR".equals(action.name()) ) {
                return;
            }

            if (lastEvent != null && "RIGHT_CLICK_BLOCK".equals(lastEvent.get("action")) && "RIGHT_CLICK_AIR".equals(action.name()) ) {
                return;
            }

            MonitorManager.saveEvent(player, "PlayerEvents/interact", eventData);
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/interact - " + e.getMessage());
        }

    }
}
