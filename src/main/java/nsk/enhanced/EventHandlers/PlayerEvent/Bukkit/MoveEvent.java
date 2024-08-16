package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class MoveEvent implements Listener {

    private static final double MIN_DISTANCE = 15;

    private final Map<Player, Location> lastPositions = new LinkedHashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        Location location = player.getLocation();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        Location lastPosition = lastPositions.get(player);

        if (lastPosition != null) {
            if (lastPosition.distance(to) < MIN_DISTANCE) {
                return;
            } else {
                lastPositions.put(player, to);
            }
        } else {
            lastPositions.put(player, location);
            lastPosition = lastPositions.get(player);
        }

        Map<String, String> eventData = new LinkedHashMap<>();

        eventData.put("position",     String.valueOf( event.hasChangedPosition() ));

        //eventData.put("e_axis",     String.format("{x:%s,y:%s,z:%s}", to.getBlockX(), to.getBlockY(), to.getBlockZ()) );
        //eventData.put("e_orient",   String.format("{p:%.0f,y:%.0f}", to.getPitch(), to.getYaw()) );

        Event e = new Event("move", player, lastPosition, eventData);

        try {
            MonitorManager.saveEvent(e);
        } catch (Exception ex) {
            ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/move - " + ex.getMessage());
        }

    }

}
