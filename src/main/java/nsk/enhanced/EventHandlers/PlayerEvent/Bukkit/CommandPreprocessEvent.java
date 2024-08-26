package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.Hibernate.ChatEvent.Command;
import nsk.enhanced.System.Hibernate.ChatEvent.Message;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommandPreprocessEvent implements Listener {

    private static final FileConfiguration config = ES.getInstance().getBukkitEventsFile();

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {

        if (!config.getBoolean("events.PlayerCommandPreprocessEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();
        String message = event.getMessage();

        Map<String, String> eventData = new LinkedHashMap<>();

        /*
        int level = config.getInt("events.PlayerCommandPreprocessEvent.level", 0);
        if (level > 0 && level < 4) {

            // Place for new features.

        }
        */


        try {

            ES.getInstance().saveEntity( new Command(player, message) );

            Event e = new Event("preCommand", player, player.getLocation(), eventData);

            MonitorManager.saveEvent(e);
        } catch (Exception ex) {
            ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/preCommand - " + ex.getMessage());
        }
    }

}
