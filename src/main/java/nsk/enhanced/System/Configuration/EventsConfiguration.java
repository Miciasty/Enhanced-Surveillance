package nsk.enhanced.System.Configuration;

import nsk.enhanced.EnhancedSurveillance;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * The {@link EventsConfiguration} class manages the loading and registration of event listeners
 * and configurations for both Bukkit and Paper events. It loads the event configurations
 * from <strong>bukkit-events.yml</strong> and <strong>paper-events.yml</strong> files and dynamically registers
 * event listeners found in the specified package.
 *
 * @see nsk.enhanced.EventHandlers.PlayerEvent.Bukkit
 * @see nsk.enhanced.EventHandlers.PlayerEvent.Paper
 */
public class EventsConfiguration {

    private static final EnhancedSurveillance plugin = ES.getInstance();
    private static final EnhancedLogger enhancedLogger = EnhancedLogger.log();

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private static FileConfiguration bukkitEvents;
    private static FileConfiguration paperEvents;

    /**
     * Loads the event configurations and registers the event listeners.
     * This method loads the <strong>bukkit-events.yml</strong> and <strong>paper-events.yml</strong> files,
     * and then registers all found PlayerEvent listeners.
     *
     * @see nsk.enhanced.EventHandlers.PlayerEvent
     */
    public static void load() {
        try {
            loadBukkitEvents();
            loadPaperEvents();

            loadBukkitPlayerEventListeners();
        } catch (Exception e) {
            enhancedLogger.severe(e.getMessage());
        }
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    /**
     * Loads and registers all Bukkit PlayerEvent listeners found in the specified package.
     * The listeners are dynamically discovered and registered with the server's PluginManager.
     *
     * @see nsk.enhanced.EventHandlers.PlayerEvent.Bukkit
     */
    private static void loadBukkitPlayerEventListeners() {
        enhancedLogger.warning("Preparing to load Bukkit PlayerEvent listeners...");

        try {

            Reflections reflections = new Reflections("nsk.enhanced.EventHandlers.PlayerEvent.Bukkit");
            Set<Class<? extends Listener>> eventListeners = reflections.getSubTypesOf(Listener.class);

            EnhancedLogger.log().info("Found " + eventListeners.size() + " Bukkit PlayerEvent listeners.");
            int n = 0;

            for (Class<? extends Listener> listener : eventListeners) {
                if (!Modifier.isAbstract(listener.getModifiers())) {
                    Listener instance = listener.getDeclaredConstructor().newInstance();
                    try {
                        plugin.getServer().getPluginManager().registerEvents(instance, plugin);
                        n++;
                    } catch (Exception ex) {
                        enhancedLogger.severe("Failed to load " + listener.getSimpleName() + " listener. - " + ex.getMessage());
                    }
                }
            }

            EnhancedLogger.log().fine("Loaded " + n + " Bukkit PlayerEvent listeners.");

        } catch (Exception e) {
            enhancedLogger.severe("Failed to load Bukkit PlayerEvents - " + e.getMessage());
        }

    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    /**
     * Loads the <strong>bukkit-events.yml</strong> file from the plugin's data folder.
     * If the file does not exist, it is created from the plugin's resources.
     */
    private static void loadBukkitEvents() {
        enhancedLogger.warning("Loading bukkit events...");
        File bukkitEventsFile = new File(plugin.getDataFolder(), "events/player/bukkit-events.yml");
        if (!bukkitEventsFile.exists()) {
            bukkitEventsFile.getParentFile().mkdirs();
            plugin.saveResource("events/player/bukkit-events.yml", false);
        }

        bukkitEvents = YamlConfiguration.loadConfiguration(bukkitEventsFile);
    }

    /**
     * Returns the loaded <strong>bukkit-events.yml</strong> configuration.
     *
     * @return the <strong>bukkit-events.yml</strong> {@link FileConfiguration} object
     */
    public static FileConfiguration getBukkitEventsFile() {
        return bukkitEvents;
    }

    /**
     * Loads the <strong>paper-events.yml</strong> file from the plugin's data folder.
     * If the file does not exist, it is created from the plugin's resources.
     */
    private static void loadPaperEvents() {
        enhancedLogger.warning("Loading bukkit events...");
        File paperEventsFile = new File(plugin.getDataFolder(), "events/player/paper-events.yml");
        if (!paperEventsFile.exists()) {
            paperEventsFile.getParentFile().mkdirs();
            plugin.saveResource("events/player/paper-events.yml", false);
        }

        paperEvents = YamlConfiguration.loadConfiguration(paperEventsFile);
    }

    /**
     * Returns the loaded <strong>paper-events.yml</strong> configuration.
     *
     * @return the <strong>paper-events.yml</strong> {@link FileConfiguration} object
     */
    public static FileConfiguration getPaperEventsFile() {
        return paperEvents;
    }
}
