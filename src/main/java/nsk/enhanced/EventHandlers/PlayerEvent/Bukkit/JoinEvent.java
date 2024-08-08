package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
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

        Map<String, Object> eventData = new LinkedHashMap<>();
        eventData.put("ip", event.getPlayer().getAddress().getAddress().getHostAddress());

        try {
            MonitorManager.saveEvent(player, "PlayerEvents/join", eventData);
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/join - " + e.getMessage());
        }
    }

}
