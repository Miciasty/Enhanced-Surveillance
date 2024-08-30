package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.EventHandlers.PlayerEvent.Bukkit.Extended.ExtMove;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link MoveEvent} class listens for the {@link PlayerMoveEvent} in Minecraft and handles the event
 * based on the configuration. It captures data about significant player movements, such as
 * distance traveled, direction, and speed.
 */
public class MoveEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();
    private static final double MIN_DISTANCE = config.getInt("events.PlayerMoveEvent.distance", 15);

    /**
     * Stores the last known significant position of each player, based on movement events.
     * The key is the player, and the value is an instance of {@link ExtMove} containing movement details.
     */
    private final Map<Player, ExtMove> lastPositions = new LinkedHashMap<>();

    /**
     * Handles the {@link PlayerMoveEvent}. This method processes player movement events by capturing
     * significant movement data, depending on the configured detail level.
     *
     * @param event the {@link PlayerMoveEvent} triggered when a player moves
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (!config.getBoolean("events.PlayerMoveEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        ExtMove lastPosition = lastPositions.get(player);

        if (lastPosition != null) {
            if (lastPosition.getTo().distance(to) < MIN_DISTANCE) {
                return;
            } else {
                lastPositions.put(player, new ExtMove(player, from, to));
            }
        } else {
            lastPositions.put(player, new ExtMove(player, from, to));
            lastPosition = lastPositions.get(player);
        }

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerMoveEvent.level", 0);
        if (Check.inRange(1, 3, true, level)) {

            if (!lastPosition.getTo().equals(player.getLocation())) {
                eventData.put("distance",           String.valueOf(lastPosition.getTo().distance(to)));
                if (ES.debugMode()) EnhancedLogger.log().info("Distance: <gold>" + lastPosition.getTo().distance(to));
            }

            if (level > 1) {
                if (lastPosition.getTo().distance(to) > MIN_DISTANCE + 2) {
                    eventData.put("teleported",     "TRUE");
                    if (ES.debugMode()) EnhancedLogger.log().info("Teleported: <gold>TRUE");
                }

                Vector direction = to.toVector().subtract(lastPosition.getTo().toVector()).normalize();
                eventData.put("direction",          direction.toString().toUpperCase());
                if (ES.debugMode()) EnhancedLogger.log().info("Direction: <gold>" + direction);
            }

            if (level > 2) {
                long timeElapsed = System.currentTimeMillis() - lastPosition.getTimestamp();
                double speed = lastPosition.getTo().distance(to) / (timeElapsed / 1000.0);

                eventData.put("speed",              String.valueOf(speed));
                if (ES.debugMode()) EnhancedLogger.log().info("Speed: <gold>" + speed);
            }

        } else if (Check.inRange(0, 3, false, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerMoveEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }

        Event e = new Event("move", player, lastPosition.getTo(), eventData);

        try {
            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/move - " + ex.getMessage());
        }

    }

}
