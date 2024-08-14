package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.Hibernate.Character;
import nsk.enhanced.System.Hibernate.EventEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class JoinEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();
        eventData.put("ip", event.getPlayer().getAddress().getAddress().getHostAddress());

        EventEntity e = new EventEntity("join", player.getUniqueId().toString(), player.getWorld().getName(), eventData);

        try {
            MonitorManager.saveEvent(e);
        } catch (Exception ex) {
            ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/join - " + ex.getMessage());
        }
    }

}
