package nsk.enhanced.System;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nsk.enhanced.EnhancedSurveillance;
import nsk.enhanced.System.Utils.DevTools;
import org.bukkit.Bukkit;

import java.util.logging.*;

/**
 * The {@link EnhancedLogger} class extends the standard Java {@link Logger} to provide customized logging
 * with colored output and a formatted message prefix. It is designed for use with a {@link Bukkit} plugin
 * and integrates with the server's console logging.
 */
public class EnhancedLogger extends Logger {

    private static EnhancedLogger logger;

    /**
     * Constructs a new {@link EnhancedLogger} instance for the {@link EnhancedSurveillance} plugin.
     * This logger is set to log all levels and customizes the output format with colors and prefixes.
     *
     * @param plugin the plugin instance for which this logger is created
     */
    public EnhancedLogger(EnhancedSurveillance plugin) {
        super(plugin.getName(), null);
        setParent(Bukkit.getLogger());
        setLevel(Level.ALL);

        setUseParentHandlers(false);

        for (Handler handler : getHandlers()) {
            removeHandler(handler);
        }
        EnhancedHandler enhancedHandler = new EnhancedHandler();
        enhancedHandler.setFormatter(new SimpleFormatter());
        addHandler(enhancedHandler);

        addLogger(this);
    }

    /**
     * The {@link EnhancedLogger} class extends the {@link Handler} class to customize how log messages are published.
     * It formats the log messages with colors and gradients based on the log level.
     */
    private class EnhancedHandler extends Handler {
        @Override
        public void publish(LogRecord record) {
            if (!isLoggable(record)) return;

            String prefix = "[Enhanced Surveillance]";

            String message = getFormatter().formatMessage(record);
            Level level = record.getLevel();

            Component casual = MiniMessage.miniMessage().deserialize("<gradient:#1f8eb2:#2dccff>" + prefix +"</gradient> " + message);

            switch (level.toString().toUpperCase()) {
                case "SEVERE":
                    Component severe = MiniMessage.miniMessage().deserialize("<gradient:#b24242:#ff5f5f>" + prefix +"</gradient> <#ffafaf>" + message);
                    Bukkit.getConsoleSender().sendMessage(severe);
                    break;

                case "WARNING":
                    Component warning = MiniMessage.miniMessage().deserialize("<gradient:#b28724:#ffc234>" + prefix +"</gradient> <#ffe099>" + message);
                    Bukkit.getConsoleSender().sendMessage(warning);
                    break;

                case "FINE":
                    Component fine = MiniMessage.miniMessage().deserialize("<gradient:#3ca800:#56f000>" + prefix +"</gradient> <#aaf77f>" + message);
                    Bukkit.getConsoleSender().sendMessage(fine);
                    break;

                case "INFO":
                    Component info = MiniMessage.miniMessage().deserialize("<gradient:#1f8eb2:#2dccff>" + prefix +"</gradient> " + message);
                    Bukkit.getConsoleSender().sendMessage(info);
                    break;

                case "CONFIG":
                    if (!DevTools.isActive()) return;

                    Component dev = MiniMessage.miniMessage().deserialize("<gradient:#b28724:#ffc234>[NSK]</gradient><gradient:#1f8eb2:#2dccff> [Devmode] </gradient> <#ffe099>" + message);
                    Bukkit.getConsoleSender().sendMessage(dev);
                    break;

                default:
                    Bukkit.getConsoleSender().sendMessage(casual);
                    break;
            }

        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}

    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    /**
     * Adds the logger instance to the static field for later retrieval.
     *
     * @param e the {@link EnhancedLogger} instance to be stored
     */
    private static void addLogger(EnhancedLogger e) {
        logger = e;
    }

    /**
     * Returns the singleton instance of {@link EnhancedLogger}. This method is used to access
     * the logger from other parts of the plugin.
     *
     * @return the singleton instance of {@link EnhancedLogger}
     */
    public static EnhancedLogger log() {
        return logger;
    }
}
