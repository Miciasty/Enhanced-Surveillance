package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.ChatEvent.Command;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommandPreprocessEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

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

            DatabaseService.saveEntity( new Command(player, message) );

            Event e = new Event("preCommand", player, player.getLocation(), eventData);

            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });

        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/preCommand - " + ex.getMessage());
        }
    }

}
