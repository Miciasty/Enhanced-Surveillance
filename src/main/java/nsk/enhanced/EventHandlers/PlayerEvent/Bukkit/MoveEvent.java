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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        Location location = player.getLocation();

        Location startingLocation = event.getFrom();
        Location endingLocation = event.getTo();

        if (isTheSameLocation(startingLocation, endingLocation)) {
            return;
        }

        Map<String, Object> eventData = new LinkedHashMap<>();

        String actual_location = String.format("{x: %d, y: %d, z: %d}", location.getBlockX(), location.getBlockY(), location.getBlockZ()).toUpperCase();

        eventData.put("event_start", String.format("{x: %f, y: %f, z: %f, pitch: %f, yaw: %f", startingLocation.getX(), startingLocation.getY(), startingLocation.getZ(), startingLocation.getPitch(), startingLocation.getYaw() ) );
        eventData.put("event_end",   String.format("{x: %f, y: %f, z: %f, pitch: %f, yaw: %f", endingLocation.getX(), endingLocation.getY(), endingLocation.getZ(), endingLocation.getPitch(), endingLocation.getYaw() ) );

        try {

            //Map<String, Object> lastEvent = MonitorManager.getEvent(player, "PlayerEvents/move");

            //if ( lastEvent != null && actual_location.equalsIgnoreCase(lastEvent.get("player_location").toString()) )  {
            //    return;
            //}

            MonitorManager.saveEvent(player, "PlayerEvents/move", eventData);
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/move - " + e.getMessage());
        }

    }

    private boolean isTheSameLocation(Location start, Location end) {

        double s_X = start.getBlockX();     double e_X = end.getBlockX();
        double s_Y = start.getBlockY();     double e_Y = end.getBlockY();
        double s_Z = start.getBlockZ();     double e_Z = end.getBlockZ();

        return s_X == e_X && s_Y == e_Y && s_Z == e_Z;

    }
}
