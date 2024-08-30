package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link JoinEvent} class listens for the {@link PlayerJoinEvent} in Minecraft and handles the event
 * based on the configuration. It captures data about the player when they join the server,
 * such as health, hunger, experience, and connection details.
 */
public class JoinEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerJoinEvent}. This method processes player join events by capturing relevant data
     * depending on the configured detail level.
     *
     * @param event the {@link PlayerJoinEvent} triggered when a player joins the server
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (!config.getBoolean("events.PlayerJoinQuitEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerJoinQuitEvent.level", 0);
        if (level > 0 && level < 4) {

            eventData.put("health",     String.valueOf(player.getHealth())      .toUpperCase());
            eventData.put("hunger",     String.valueOf(player.getFoodLevel())   .toUpperCase());

            if (ES.debugMode()) {
                EnhancedLogger.log().info("Health: <red>" + player.getHealth());
                EnhancedLogger.log().info("Hunger: <green>" + player.getFoodLevel());
            };

            if (level > 1) {
                eventData.put("exp",    String.valueOf(player.getExp())         .toUpperCase());
                eventData.put("mode",   String.valueOf(player.getGameMode())    .toUpperCase());
                eventData.put("op",     String.valueOf(player.isOp())           .toUpperCase());

                if (ES.debugMode()) {
                    EnhancedLogger.log().info("Exp:  <aqua>" + player.getExp());
                    EnhancedLogger.log().info("Mode: <green>" + player.getGameMode());
                    EnhancedLogger.log().info("Op:   <green>" + player.isOp());
                }
            }

            if (level > 2) {
                eventData.put("ip",     player.getAddress().getAddress().getHostAddress()     );
                eventData.put("port",   String.valueOf(player.getAddress().getPort())         );
                eventData.put("host",   player.getAddress().getHostName()       .toUpperCase());

                if (ES.debugMode()) {
                    EnhancedLogger.log().info("Ip:   <aqua>" + player.getAddress().getAddress().getHostAddress());
                    EnhancedLogger.log().info("Port: <green>" + player.getAddress().getPort());
                    EnhancedLogger.log().info("Host: <green>" + player.getAddress().getHostName());
                }
            }

        }

        Event e = new Event("join", player, player.getLocation(), eventData);

        try {
            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/join - " + ex.getMessage());
        }
    }

}
