package nsk.enhanced.EventHandlers.PlayerEvent;

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

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                eventData.put("action",     action.name().toUpperCase() );
                break;
            case RIGHT_CLICK_BLOCK:
                if ( event.getHand() == EquipmentSlot.HAND )  {
                    eventData.put("action", action.name().toUpperCase() );
                }
                break;
        }

        eventData.put("hand",               event.getHand().toString().toUpperCase() );

        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR)) {
            eventData.put("item",           "AIR" );
        } else {
            eventData.put("item",           event.getItem().toString() );
        }

        eventData.put("event_block",        block.getType().toString().toUpperCase() );
        eventData.put("event_world",        block.getLocation().getWorld().getName().toUpperCase() );
        eventData.put("event_location",     String.format("{x: %d, y: %d, z: %d}", location.getBlockX(), location.getBlockY(), location.getBlockZ() ));

        eventData.put("player_world",       player.getWorld().getName() );
        eventData.put("player_location",    String.format("{x: %d, y: %d, z: %d}", player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));

        try {
            MonitorManager.saveEvent(player, "PlayerEvents/interact", eventData);
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/interact - " + e.getMessage());
        }

    }
}
