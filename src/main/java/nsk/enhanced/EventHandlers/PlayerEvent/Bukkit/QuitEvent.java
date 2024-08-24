package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class QuitEvent implements Listener {

    private static final FileConfiguration config = ES.getInstance().getBukkitEventsFile();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (config.getBoolean("events.PlayerJoinQuitEvent.enabled", false)) {

            Player player = event.getPlayer();

            Map<String, String> eventData = new LinkedHashMap<>();

            int level = config.getInt("events.PlayerJoinQuitEvent.level", 0);
            if (level > 0 && level < 4) {

                eventData.put("health",     String.valueOf(player.getHealth())      .toUpperCase());
                eventData.put("hunger",     String.valueOf(player.getFoodLevel())   .toUpperCase());

                if (level > 1) {
                    eventData.put("exp",    String.valueOf(player.getExp())         .toUpperCase());
                    eventData.put("mode",   String.valueOf(player.getGameMode())    .toUpperCase());
                    eventData.put("op",     String.valueOf(player.isOp())           .toUpperCase());
                }

                if (level > 2) {
                    eventData.put("ip",     player.getAddress().getAddress().getHostAddress()     );
                    eventData.put("port",   String.valueOf(player.getAddress().getPort())         );
                    eventData.put("host",   player.getAddress().getHostName()       .toUpperCase());
                }

            }

            Event e = new Event("quit", player, player.getLocation(), eventData);

            try {
                MonitorManager.saveEvent(e);
            } catch (Exception ex) {
                ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/quit - " + ex.getMessage());
            }

        }
    }

}
