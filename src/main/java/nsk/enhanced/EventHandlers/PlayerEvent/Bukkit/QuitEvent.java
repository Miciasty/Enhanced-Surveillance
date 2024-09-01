package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import nsk.enhanced.System.Utils.Tools;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link QuitEvent} class listens for the {@link PlayerQuitEvent} in Minecraft and handles the event
 * based on the configuration. It captures data about the player when they leave the server,
 * such as health, hunger, experience, and connection details.
 */
public class QuitEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerQuitEvent}. This method processes player quit events by capturing relevant data
     * depending on the configured detail level.
     *
     * @param event the {@link PlayerQuitEvent} triggered when a player leaves the server
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (!config.getBoolean("events.PlayerJoinQuitEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerJoinQuitEvent.level", 0);
        if (Check.inRange(1, 2, level)) {

            eventData.put("health",     String.valueOf(Tools.roundTo(player.getHealth(), 2))      .toUpperCase());
            eventData.put("hunger",     String.valueOf(player.getFoodLevel())   .toUpperCase());

            EnhancedLogger.log().config("health: <red>" + Tools.roundTo(player.getHealth(), 2) + "</red>");
            EnhancedLogger.log().config("hunger: <red>" + player.getFoodLevel() + "</red>");

            if (level > 1) {
                eventData.put("exp",    String.valueOf(Tools.roundTo(player.getExp(), 2))         .toUpperCase());
                eventData.put("mode",   String.valueOf(player.getGameMode())    .toUpperCase());
                eventData.put("op",     String.valueOf(player.isOp())           .toUpperCase());

                EnhancedLogger.log().config("exp: <red>" + Tools.roundTo(player.getExp(), 2) + "</red>");
                EnhancedLogger.log().config("mode: <red>" + player.getGameMode() + "</red>");
                EnhancedLogger.log().config("op: <red>" + player.isOp() + "</red>");
            }

        } else if (!Check.inRange(0, 2, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerJoinQuitEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }

        Event e = new Event("quit", player, player.getLocation(), eventData);

        try {
            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/quit - " + ex.getMessage());
        }
    }

}
