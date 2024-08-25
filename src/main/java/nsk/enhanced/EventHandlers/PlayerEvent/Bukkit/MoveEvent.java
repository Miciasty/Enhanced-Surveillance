package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.EventHandlers.PlayerEvent.Bukkit.Extended.ExtMoveEvent;
import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.LinkedHashMap;
import java.util.Map;

public class MoveEvent implements Listener {

    private static final FileConfiguration config = ES.getInstance().getBukkitEventsFile();
    private static final double MIN_DISTANCE = config.getInt("events.PlayerMoveEvent.distance", 15);

    private final Map<Player, ExtMoveEvent> lastPositions = new LinkedHashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (ES.getInstance().getBukkitEventsFile().getBoolean("events.PlayerMoveEvent.enabled", false)) {

            Player player = event.getPlayer();

            Location from = event.getFrom();
            Location to = event.getTo();

            if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
                return;
            }
            ExtMoveEvent lastPosition = lastPositions.get(player);

            if (lastPosition != null) {
                if (lastPosition.getTo().distance(to) < MIN_DISTANCE) {
                    return;
                } else {
                    lastPositions.put(player, new ExtMoveEvent(player, from, to));
                }
            } else {
                lastPositions.put(player, new ExtMoveEvent(player, from, to));
                lastPosition = lastPositions.get(player);
            }

            Map<String, String> eventData = new LinkedHashMap<>();

            int level = config.getInt("events.PlayerMoveEvent.level", 0);
            if (level > 0 && level < 4) {

                if (!lastPosition.getTo().equals(player.getLocation())) {
                    eventData.put("distance",           String.valueOf(lastPosition.getTo().distance(to)));
                }

                if (level > 1) {
                    if (lastPosition.getTo().distance(to) > MIN_DISTANCE) {
                        eventData.put("teleported",     "TRUE");
                    }

                    Vector direction = to.toVector().subtract(lastPosition.getTo().toVector()).normalize();
                    eventData.put("direction",          direction.toString().toUpperCase());
                }

                if (level > 2) {
                    long timeElapsed = System.currentTimeMillis() - lastPosition.getTimestamp();
                    double speed = lastPosition.getTo().distance(to) / (timeElapsed / 1000.0);

                    eventData.put("speed",              String.valueOf(speed));
                }

            }

            Event e = new Event("move", player, lastPosition.getTo(), eventData);

            try {
                MonitorManager.saveEvent(e);
            } catch (Exception ex) {
                ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/move - " + ex.getMessage());
            }
        }

    }

}
