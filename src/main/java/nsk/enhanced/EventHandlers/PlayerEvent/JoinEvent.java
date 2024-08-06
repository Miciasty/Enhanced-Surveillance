package nsk.enhanced.EventHandlers.PlayerEvent;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class JoinEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("ip", event.getPlayer().getAddress().getAddress().getHostAddress());
        eventData.put("world", player.getWorld().getName());
        eventData.put("location", String.format("{x: %d, y: %d, z: %d}", player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));

        try {
            MonitorManager.saveEvent(player, "PlayerEvents/join", eventData);
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/join - " + e.getMessage());
        }
    }

}
