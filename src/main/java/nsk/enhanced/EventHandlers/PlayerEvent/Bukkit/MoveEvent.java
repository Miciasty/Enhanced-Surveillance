package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class MoveEvent implements Listener {

    private static final double MIN_DISTANCE = 10;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        Location location = player.getLocation();

        Location startingLocation = event.getFrom();
        Location endingLocation = event.getTo();

        if (startingLocation.distance(endingLocation) < MIN_DISTANCE) {
            return;
        }

        Map<String, Object> eventData = new LinkedHashMap<>();

        eventData.put("event_start", String.format("{x: %f.2, y: %f.2, z: %f.2, pitch: %f.3, yaw: %f.3}", startingLocation.getX(), startingLocation.getY(), startingLocation.getZ(), startingLocation.getPitch(), startingLocation.getYaw() ) );
        eventData.put("event_end",   String.format("{x: %f.2, y: %f.2, z: %f.2, pitch: %f.3, yaw: %f.3}", endingLocation.getX(), endingLocation.getY(), endingLocation.getZ(), endingLocation.getPitch(), endingLocation.getYaw() ) );

        try {
            MonitorManager.saveEvent(player, "PlayerEvents/move", eventData);
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/move - " + e.getMessage());
        }

    }

}
