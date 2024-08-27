package nsk.enhanced.System.Configuration;

import nsk.enhanced.EnhancedSurveillance;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ServerConfiguration {

    private static final EnhancedSurveillance plugin = ES.getInstance();
    private static final EnhancedLogger enhancedLogger = EnhancedLogger.log();

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private static FileConfiguration config;
    private static FileConfiguration translations;

    public static void load() {
        loadConfiguration();
        //loadTranslations();
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private static void loadConfiguration() {
        enhancedLogger.warning("Loading configuration...");
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }
    public static FileConfiguration getConfig() {
        return config;
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private static void loadTranslations() {
        enhancedLogger.warning("Loading translations...");
        File translationsFile = new File(plugin.getDataFolder(), "translations.yml");
        if (!translationsFile.exists()) {
            translationsFile.getParentFile().mkdirs();
            plugin.saveResource("translations.yml", false);
        }

        translations = YamlConfiguration.loadConfiguration(translationsFile);
    }
    public static FileConfiguration getTranslations() {
        return translations;
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private void reloadConfiguration() {
        try {
            load();

            enhancedLogger.fine("Reloaded configuration");

        } catch (Exception e) {
            enhancedLogger.severe("Failed to reload configuration. - " + e);
        }
    }

}
