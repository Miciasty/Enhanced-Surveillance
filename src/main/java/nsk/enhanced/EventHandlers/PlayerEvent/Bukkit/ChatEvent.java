package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.Hibernate.ChatEvent.Message;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ChatEvent implements Listener {

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {

        if (ES.getInstance().getBukkitEventsFile().getBoolean("events.PlayerChatEvent.enabled", false)) {

            Player player  = event.getPlayer();

            Map<String, String> eventData = new LinkedHashMap<>();

            String message = event.getMessage();


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
