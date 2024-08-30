package nsk.enhanced.System.Configuration;

import nsk.enhanced.EnhancedSurveillance;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * The {@link ServerConfiguration} class handles the loading, accessing, and reloading of the server's configuration
 * and translation files. It manages the <strong>`config.yml`</strong> and <strong>`translations.yml`</strong> files used by the plugin.
 */
public class ServerConfiguration {

    private static final EnhancedSurveillance plugin = ES.getInstance();
    private static final EnhancedLogger enhancedLogger = EnhancedLogger.log();

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private static FileConfiguration config;
    private static FileConfiguration translations;

    /**
     * Loads the server configuration files. This method is responsible for initializing and loading
     * the <strong>`config.yml`</strong> file and the <strong>`translations.yml`</strong> file.
     */
    public static void load() {
        loadConfiguration();
        //loadTranslations();
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    /**
     * Loads the <strong>`config.yml`</strong> file from the plugin's data folder.
     * If the file does not exist, it is created from the plugin's resources.
     */
    private static void loadConfiguration() {
        enhancedLogger.warning("Loading configuration...");
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Returns the loaded <strong>config.yml</strong> configuration.
     *
     * @return the <strong>config.yml</strong> FileConfiguration object
     */
    public static FileConfiguration getConfig() {
        return config;
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    /**
     * Loads the <strong>translations.yml</strong> file from the plugin's data folder.
     * If the file does not exist, it is created from the plugin's resources.
     */
    private static void loadTranslations() {
        enhancedLogger.warning("Loading translations...");
        File translationsFile = new File(plugin.getDataFolder(), "translations.yml");
        if (!translationsFile.exists()) {
            translationsFile.getParentFile().mkdirs();
            plugin.saveResource("translations.yml", false);
        }

        translations = YamlConfiguration.loadConfiguration(translationsFile);
    }

    /**
     * Returns the loaded <strong>translations.yml</strong> configuration.
     *
     * @return the <strong>translations.yml</strong> FileConfiguration object
     */
    public static FileConfiguration getTranslations() {
        return translations;
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    /**
     * Reloads the server configuration files. This method reloads the <strong>config.yml</strong>
     * and <strong>translations.yml</strong> files and logs the success or failure of the operation.
     */
    private void reloadConfiguration() {
        try {
            load();

            enhancedLogger.fine("Reloaded configuration");

        } catch (Exception e) {
            enhancedLogger.severe("Failed to reload configuration. - " + e);
        }
    }

}
