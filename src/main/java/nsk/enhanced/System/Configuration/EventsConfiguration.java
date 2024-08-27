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

public class EventsConfiguration {

    private static final EnhancedSurveillance plugin = ES.getInstance();
    private static final EnhancedLogger enhancedLogger = EnhancedLogger.log();

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private static FileConfiguration bukkitEvents;
    private static FileConfiguration paperEvents;

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

    private static void loadBukkitPlayerEventListeners() {
        enhancedLogger.warning("Preparing to load Bukkit PlayerEvent listeners...");

        /*try {getServer().getPluginManager().registerEvents(new JoinEvent(), this);} catch (Exception e) {
            enhancedLogger.severe("JoinEvent listener is not loaded!");
        }
        try {getServer().getPluginManager().registerEvents(new QuitEvent(), this);} catch (Exception e) {
            enhancedLogger.severe("QuitEvent listener is not loaded!");
        }
        try {getServer().getPluginManager().registerEvents(new MoveEvent(), this);} catch (Exception e) {enhancedLogger.severe("MoveEvent listener is not loaded!");
        }
        try {getServer().getPluginManager().registerEvents(new ChatEvent(), this);} catch (Exception e) {
            enhancedLogger.severe("ChatEvent listener is not loaded!");
        }
        try {getServer().getPluginManager().registerEvents(new CommandPreprocessEvent(), this);} catch (Exception e) {
            enhancedLogger.severe("CommandPreprocessEvent listener is not loaded!");
        }
        try {getServer().getPluginManager().registerEvents(new InteractEvent(), this);} catch (Exception e) {
            enhancedLogger.severe("InteractEvent listener is not loaded!");
        }
        try {getServer().getPluginManager().registerEvents(new InteractEntityEvent(), this);} catch (Exception e) {
            enhancedLogger.severe("InteractEntityEvent listener is not loaded!");
        }*/

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

    private static void loadBukkitEvents() {
        enhancedLogger.warning("Loading bukkit events...");
        File bukkitEventsFile = new File(plugin.getDataFolder(), "events/player/bukkit-events.yml");
        if (!bukkitEventsFile.exists()) {
            bukkitEventsFile.getParentFile().mkdirs();
            plugin.saveResource("events/player/bukkit-events.yml", false);
        }

        bukkitEvents = YamlConfiguration.loadConfiguration(bukkitEventsFile);
    }
    public static FileConfiguration getBukkitEventsFile() {
        return bukkitEvents;
    }

    private static void loadPaperEvents() {
        enhancedLogger.warning("Loading bukkit events...");
        File paperEventsFile = new File(plugin.getDataFolder(), "events/player/paper-events.yml");
        if (!paperEventsFile.exists()) {
            paperEventsFile.getParentFile().mkdirs();
            plugin.saveResource("events/player/paper-events.yml", false);
        }

        paperEvents = YamlConfiguration.loadConfiguration(paperEventsFile);
    }
    public static FileConfiguration getPaperEventsFile() {
        return paperEvents;
    }
}
