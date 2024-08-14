package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.Hibernate.EventEntity;
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

        Player player  = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        String message = event.getMessage();
        String format  = event.getFormat();

        eventData.put("event_message", message);

        Set<Player> recipients = event.getRecipients();
        int amount = recipients.size();

        StringBuilder formattedRecipientsAsName = new StringBuilder();
        StringBuilder formattedRecipientsAsUUID = new StringBuilder();
        for (Player recipient : recipients) {
            formattedRecipientsAsName.append( recipient.getName()     ).append(", ");
            formattedRecipientsAsUUID.append( recipient.getUniqueId() ).append(", ");
        }
        formattedRecipientsAsName.setLength(formattedRecipientsAsName.length()-2);
        formattedRecipientsAsUUID.setLength(formattedRecipientsAsUUID.length()-2);

        eventData.put("event_size_recipients", String.valueOf(amount));
        eventData.put("event_name_recipients", String.format("{%s}", formattedRecipientsAsName) );
        eventData.put("event_uuid_recipients", String.format("{%s}", formattedRecipientsAsUUID) );

        EventEntity e = new EventEntity("chat", player.getUniqueId().toString(), player.getWorld().getName(), eventData);

        try {

            MonitorManager.saveEvent(e);

        } catch (Exception ex) {
            ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/chat - " + ex.getMessage());
        }

    }
}
