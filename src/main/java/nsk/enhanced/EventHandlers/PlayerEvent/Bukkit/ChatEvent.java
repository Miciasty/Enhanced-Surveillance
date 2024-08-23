package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.Hibernate.ChatEvent.Message;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.*;

public class ChatEvent implements Listener {

    private static final FileConfiguration config = ES.getInstance().getBukkitEventsFile();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        if (config.getBoolean("events.AsyncPlayerChatEvent.enabled", false)) {

            Player player  = event.getPlayer();

            Map<String, String> eventData = new LinkedHashMap<>();

            String message = event.getMessage();

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
                ES.getInstance().getEnhancedLogger().warning("<green>'events.PlayerChatEvent.level'</green> can only be set to a maximum of 2. The provided value is invalid, so the event will default to level 0.");
            }


            try {

                ES.getInstance().saveEntity( new Message(player, message, event.getRecipients().size()) );

                Event e = new Event("chat", player, player.getLocation(), eventData);

                MonitorManager.saveEvent(e);

            } catch (Exception ex) {
                ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/chat - " + ex.getMessage());
            }

        }

    }
}
