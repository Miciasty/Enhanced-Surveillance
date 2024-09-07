package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.EventHandlers.EventData;
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
        if (Check.inRange(1, 2, level)) {

            eventData.put(EventData.HEALTH.name(),          String.valueOf(Tools.roundTo(player.getHealth(), 2))      .toUpperCase());
            eventData.put(EventData.HUNGER.name(),          String.valueOf(player.getFoodLevel())   .toUpperCase());

            EnhancedLogger.log().config(EventData.HEALTH.name() + ": <red>" + player.getHealth());
            EnhancedLogger.log().config(EventData.HUNGER.name() + ": <green>" + player.getFoodLevel());


            if (level > 1) {
                eventData.put(EventData.EXP.name(),         String.valueOf(Tools.roundTo(player.getExp(), 2))         .toUpperCase());
                eventData.put(EventData.GAMEMODE.name(),    player.getGameMode().name()             .toUpperCase());
                eventData.put(EventData.OP.name(),          String.valueOf(player.isOp())           .toUpperCase());

                EnhancedLogger.log().config(EventData.EXP.name() + ":  <aqua>" + player.getExp());
                EnhancedLogger.log().config(EventData.GAMEMODE.name() + ": <green>" + player.getGameMode().name());
                EnhancedLogger.log().config(EventData.OP.name() + ":   <green>" + player.isOp());

            }

        } else if (!Check.inRange(0, 2, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerJoinQuitEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
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
