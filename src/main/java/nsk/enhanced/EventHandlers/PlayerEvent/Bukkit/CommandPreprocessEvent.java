package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Base.Messages.Event.Command;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link CommandPreprocessEvent} class listens for the {@link PlayerCommandPreprocessEvent} in Minecraft
 * and handles the event based on the configuration. It logs command preprocessing events
 * and stores the event data in the database.
 */
public class CommandPreprocessEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerCommandPreprocessEvent}. This method processes command preprocessing events
     * by capturing the player who issued the command and the command itself, then logs these details.
     *
     * @param event the {@link PlayerCommandPreprocessEvent} triggered when a player issues a command
     */
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

            Event e = new Event("preCommand", player, eventData);
            Command c = new Command(e, message);

            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
                DatabaseService.saveEntity(c);
            });

        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/preCommand - " + ex.getMessage());
        }
    }

}
