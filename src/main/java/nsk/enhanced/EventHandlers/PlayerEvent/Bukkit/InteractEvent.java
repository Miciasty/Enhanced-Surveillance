package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.Hibernate.Event;
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

        Location location;

        Map<String, String> eventData = new LinkedHashMap<>();

        if ( event.getHand() == EquipmentSlot.HAND ) {
            eventData.put("action",         action.name().toUpperCase() );
        } else {
            return;
        }

        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR)) {
            eventData.put("item",            null );
        } else {
            eventData.put("item",            event.getItem().toString() );
        }

        if (block == null) {
            eventData.put("event_block",     null );
        } else {
            eventData.put("event_block",     block.getType().toString().toUpperCase() );
        }

        if (block != null) {
            location = block.getLocation();
        } else {
            location = null;
        }

        try {

            Map<String, Object> lastEvent = MonitorManager.getEvent(player, "PlayerEvents/interact");

            if (lastEvent != null && "LEFT_CLICK_BLOCK".equals(lastEvent.get("action")) && "LEFT_CLICK_AIR".equals(action.name()) ) {
                return;
            }

            if (lastEvent != null && "RIGHT_CLICK_BLOCK".equals(lastEvent.get("action")) && "RIGHT_CLICK_AIR".equals(action.name()) ) {
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
