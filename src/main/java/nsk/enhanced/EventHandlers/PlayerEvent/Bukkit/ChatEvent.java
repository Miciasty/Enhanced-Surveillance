package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
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

        Player player  = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        String message = event.getMessage();

        eventData.put("e_message", message);

        Set<Player> recipients = event.getRecipients();
        int amount = recipients.size();

        StringBuilder formattedRecipientsAsName = new StringBuilder();
        StringBuilder formattedRecipientsAsUUID = new StringBuilder();
        for (Player recipient : recipients) {
            formattedRecipientsAsName.append( recipient.getName()     ).append(",");
            formattedRecipientsAsUUID.append( recipient.getUniqueId() ).append(",");
        }
        formattedRecipientsAsName.setLength(formattedRecipientsAsName.length()-1);
        formattedRecipientsAsUUID.setLength(formattedRecipientsAsUUID.length()-1);

        eventData.put("e_size_recipients", String.valueOf(amount));
        eventData.put("e_uuid_recipients", String.format("{%s}", formattedRecipientsAsUUID) );

        Event e = new Event("chat", player, player.getLocation(), eventData);

        try {

            MonitorManager.saveEvent(e);

        } catch (Exception ex) {
            ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/chat - " + ex.getMessage());
        }

    }
}
