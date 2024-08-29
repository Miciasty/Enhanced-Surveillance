package nsk.enhanced;

import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.Configuration.ServerConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Character;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.Hibernate.WorldEntity;
import nsk.enhanced.System.MemoryService;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * EnhancedSurveillance is a main plugin class.
 *
 * @author Mateusz Aftanas (Miciasty)
 */
public final class EnhancedSurveillance extends JavaPlugin {

    /**
     * {@link EnhancedLogger} is a plugin's logger.
     */
    private EnhancedLogger enhancedLogger;

    /**
     * onEnable() is called on server startup.
     */
    @Override
    public void onEnable() {
        ES.setInstance(this);
        enhancedLogger = new EnhancedLogger(this);
        ServerConfiguration.load();

        FileConfiguration config = ServerConfiguration.getConfig();

        int services = config.getInt("EnhancedSurveillance.memory-service.services", 1);
        int threads = config.getInt("EnhancedSurveillance.memory-service.threads", 1);

        MemoryService.initializeServices(services, threads);

        DatabaseService.configureHibernate();

        DatabaseService.loadEntityFromDatabase(Character.class, Character.getCharacters());
        DatabaseService.loadEntityFromDatabase(WorldEntity.class, WorldEntity.getWorlds());

        EventsConfiguration.load();
    }

    /**
     * onDisable() is called when plugin is disabling.
     */
    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {

            Map<String, String> eventData = new LinkedHashMap<>();
            eventData.put("ip", player.getAddress().getAddress().getHostAddress());

            Event e = new Event("quit", player, player.getLocation(), eventData);

            try {
                MemoryService.logEventAsync(() -> {
                    DatabaseService.saveEntity(e);
                });
            } catch (Exception ex) {
                enhancedLogger.severe("Failed to save PlayerEvents/quit - " + ex.getMessage());
            }

        }

        MemoryService.shutdownAllServices();
        DatabaseService.close();
    }

}
