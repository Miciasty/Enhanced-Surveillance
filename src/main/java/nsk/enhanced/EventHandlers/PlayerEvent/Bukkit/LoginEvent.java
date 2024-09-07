package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.EventHandlers.EventData;
import nsk.enhanced.EventHandlers.PlayerEvent.Bukkit.Enum.HostData;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.Hibernate.Base.Messages.Event.Kick;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link LoginEvent} class listens for the {@link PlayerLoginEvent} in Minecraft and handles the event
 * based on the configuration. It captures data when a player attempts to log in to the server, including
 * the result of the login attempt, IP address, hostname, and other relevant details depending on the configuration level.
 */
public class LoginEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerLoginEvent}. This method processes the event when a player attempts to log in,
     * capturing relevant data based on the configured detail level.
     *
     * @param event the {@link PlayerLoginEvent} triggered when a player attempts to log in to the server
     */
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {

        if (!config.getBoolean("events.PlayerLoginEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerLoginEvent.level", 0);
        if (Check.inRange(1, 2, level)) {

            eventData.put(EventData.RESULT.name(),              event.getResult().toString()                .toUpperCase());

            EnhancedLogger.log().config(EventData.RESULT.name()         + ": <green>" + event.getResult() + "</green>");

            if (level > 1) {
                eventData.put(HostData.IP.name(),               event.getRealAddress().getHostAddress()                   );
                eventData.put(HostData.REALHOST.name(),         event.getRealAddress().getHostName()        .toUpperCase());
                eventData.put(HostData.HOSTNAME.name(),         event.getHostname()                         .toUpperCase());

                EnhancedLogger.log().config(HostData.IP.name()          + ": <aqua>" + event.getRealAddress().getHostAddress() + "</aqua>");
                EnhancedLogger.log().config(HostData.REALHOST.name()    + ": <green>" + event.getRealAddress().getHostName() + "</green>");
                EnhancedLogger.log().config(HostData.HOSTNAME.name()    + ": <red>" + event.getHostname() + "</red>");
            }

        } else if (!Check.inRange(0, 2, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerLoginEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }

        Event e = new Event("login", player, player.getLocation(), eventData);

        try {
            if (event.getResult().toString().equalsIgnoreCase("ALLOWED")) {
                MemoryService.logEventAsync(() -> {
                    DatabaseService.saveEntity(e);
                });
            } else {

                Kick  k = new Kick(e, event.kickMessage().toString());

                MemoryService.logEventAsync(() -> {
                    DatabaseService.saveEntity(k);
                    DatabaseService.saveEntity(e);
                });
            }
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/login - " + ex.getMessage());
        }
    }

}
