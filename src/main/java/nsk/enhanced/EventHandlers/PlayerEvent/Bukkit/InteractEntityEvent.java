package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.Hibernate.ChatEvent.Command;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class InteractEntityEvent implements Listener {

    private static final FileConfiguration config = ES.getInstance().getBukkitEventsFile();

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        if (config.getBoolean("events.PlayerInteractEntityEvent.enabled", false)) {

            Player player = event.getPlayer();
            Entity target = event.getRightClicked();

            Map<String, String> eventData = new LinkedHashMap<>();

            int level = config.getInt("events.PlayerInteractEntityEvent.level", 0);
            if (level > 0 && level < 4) {

                if (target.getType().getEntityClass() != null) {
                    eventData.put("Type",   target.getType().getEntityClass().getSimpleName());
                }

            }


            try {

                Event e = new Event("interactEntity", player, target.getLocation(), eventData);

                MonitorManager.saveEvent(e);
            } catch (Exception ex) {
                ES.getInstance().getEnhancedLogger().severe("Failed to save PlayerEvents/interactEntity - " + ex.getMessage());
            }

        }
    }

}