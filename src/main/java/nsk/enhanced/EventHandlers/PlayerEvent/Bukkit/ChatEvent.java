package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import jdk.jfr.internal.settings.EnabledSetting;
import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.Configuration.ServerConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.ChatEvent.Message;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class ChatEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        if (!config.getBoolean("events.AsyncPlayerChatEvent.enabled", false)) {
            return;
        }

        Player player  = event.getPlayer();
        String message = event.getMessage();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.AsyncPlayerChatEvent.level", 0);
        if ( level > 0 && level < 3) {

            Set<Player> recipients = event.getRecipients();

            List<Double> distances = new ArrayList<>();
            double totalDistance = 0;

            for (Player recipient : recipients) {
                if (recipient != player) {

                    Location recipientLocation = recipient.getLocation();
                    double distance = recipientLocation.distance(player.getLocation());
                    totalDistance += distance;
                    distances.add(distance);

                }
            }

            eventData.put("avgDist", String.valueOf(totalDistance / (recipients.size() - 1)) );

            if (level > 1) {

                double minDistance = 0;
                double maxDistance = 0;

                for (Double distance : distances) {
                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                    if (distance > maxDistance) {
                        maxDistance = distance;
                    }
                }

                eventData.put("minDist", String.valueOf(minDistance));
                eventData.put("maxDist", String.valueOf(maxDistance));
            }

        } else if (level < 0 || level > 2) {
            EnhancedLogger.log().warning("<green>'events.PlayerChatEvent.level'</green> can only be set to a maximum of 2. The provided value is invalid, so the event will default to level 0.");
        }


        try {

            DatabaseService.saveEntity( new Message(player, message, event.getRecipients().size()) );

            Event e = new Event("chat", player, player.getLocation(), eventData);

            MonitorManager.saveEvent(e);

        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/chat - " + ex.getMessage());
        }

    }
}
