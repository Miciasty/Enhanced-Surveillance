package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.ChatEvent.Message;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

/**
 * The {@link ChatEvent} class listens for the {@link AsyncPlayerChatEvent} and handles the event based on the configuration.
 * It logs chat events, calculates recipient distances, and stores the event data in the database.
 */
public class ChatEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link AsyncPlayerChatEvent}. This method processes chat events by logging details such as
     * the average, minimum, and maximum distance between the player and the recipients of the chat message,
     * depending on the configured level of detail.
     *
     * @param event the {@link AsyncPlayerChatEvent} triggered when a player sends a chat message
     */
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

            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });

        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/chat - " + ex.getMessage());
        }

    }
}
