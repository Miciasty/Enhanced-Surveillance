package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import io.papermc.paper.event.player.PlayerPickItemEvent;
import nsk.enhanced.EventHandlers.PlayerEvent.Bukkit.Enum.EventData;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.Hibernate.MessageHandler.Kick;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link KickEvent} class listens for the {@link PlayerKickEvent} in Minecraft and handles the event
 * based on the configuration. It captures data when a player is kicked from the server, such as the cause
 * and the reason provided for the kick.
 */
public class KickEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerKickEvent}. This method processes the event when a player is kicked,
     * capturing relevant data based on the configured detail level.
     *
     * @param event the {@link PlayerKickEvent} triggered when a player is kicked from the server
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {

        if (!config.getBoolean("events.PlayerKickEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerKickEvent.level", 0);
        if (level == 1) {

            eventData.put(EventData.CAUSE.name(),          event.getCause().name());

            EnhancedLogger.log().config(EventData.CAUSE.name() + ": <red>" + event.getCause().name() + "</red>");

        } else if (!Check.inRange(0, 1, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerKickEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }

        Kick  k = new Kick(player, event.getReason());
        Event e = new Event("kick", player, eventData);

        try {
            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(k);
                DatabaseService.saveEntity(e);
            });
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/kick - " + ex.getMessage());
        }
    }

}
