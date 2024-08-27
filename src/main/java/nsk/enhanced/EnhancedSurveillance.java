package nsk.enhanced;

import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.Configuration.ServerConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Character;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.Hibernate.WorldEntity;
import nsk.enhanced.System.MemoryService;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EnhancedSurveillance extends JavaPlugin {

    private EnhancedLogger enhancedLogger;

    @Override
    public void onEnable() {

        ES.setInstance(this);
        enhancedLogger = new EnhancedLogger(this);

        MemoryService.initializeServices(1, 1);

        ServerConfiguration.load();

        DatabaseService.configureHibernate();

        DatabaseService.loadEntityFromDatabase(Character.class, Character.getCharacters());
        DatabaseService.loadEntityFromDatabase(WorldEntity.class, WorldEntity.getWorlds());

        EventsConfiguration.load();

    }

    @Override
    public void onDisable() {

        for (Player player : getServer().getOnlinePlayers()) {

            Map<String, String> eventData = new LinkedHashMap<>();
            eventData.put("ip", player.getAddress().getAddress().getHostAddress());

            Event e = new Event("quit", player, player.getLocation(), eventData);

            try {
                MonitorManager.saveEvent(e);
            } catch (Exception ex) {
                enhancedLogger.severe("Failed to save PlayerEvents/quit - " + ex.getMessage());
            }

        }

        MemoryService.shutdownAllServices();
        DatabaseService.close();
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //


}
