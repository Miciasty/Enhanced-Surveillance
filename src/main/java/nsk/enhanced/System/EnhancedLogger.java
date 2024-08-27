package nsk.enhanced.System;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nsk.enhanced.EnhancedSurveillance;
import org.bukkit.Bukkit;

import java.util.logging.*;

public class EnhancedLogger extends Logger {

    private static EnhancedLogger logger;

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

        EnhancedLogger.addLogger(this);
    }

    private class EnhancedHandler extends Handler {
        @Override
        public void publish(LogRecord record) {
            if (!isLoggable(record)) return;

            String prefix = "[Enhanced Surveillance]";

            String message = getFormatter().formatMessage(record);
            Level level = record.getLevel();

            Component casual = MiniMessage.miniMessage().deserialize("<gradient:#1f8eb2:#2dccff>" + prefix +"</gradient> " + message);

            /*if (level == Level.SEVERE) {
                Component severe = MiniMessage.miniMessage().deserialize("<gradient:#b24242:#ff5f5f>" + prefix +"</gradient> <#ffafaf>" + message);
                Bukkit.getConsoleSender().sendMessage(severe);
            } else if (level == Level.WARNING) {
                Component warning = MiniMessage.miniMessage().deserialize("<gradient:#b28724:#ffc234>" + prefix +"</gradient> <#ffe099>" + message);
                Bukkit.getConsoleSender().sendMessage(warning);
            } else if (level == Level.FINE) {
                Component fine = MiniMessage.miniMessage().deserialize("<gradient:#3ca800:#56f000>" + prefix +"</gradient> <#aaf77f>" + message);
                Bukkit.getConsoleSender().sendMessage(fine);
            } else if (level == Level.CONFIG) {
                Component dev = MiniMessage.miniMessage().deserialize("<gradient:#b28724:#ffc234>[NSK]</gradient><gradient:#1f8eb2:#2dccff> [Devmode] </gradient> <#ffe099>" + message);
                Bukkit.getConsoleSender().sendMessage(dev);
            }
            else {
                Bukkit.getConsoleSender().sendMessage(casual);
            }*/

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

                case "CONFIG":
                    Component dev = MiniMessage.miniMessage().deserialize("<gradient:#b28724:#ffc234>[NSK]</gradient><gradient:#1f8eb2:#2dccff> [Devmode] </gradient> <#ffe099>" + message);
                    Bukkit.getConsoleSender().sendMessage(dev);
                    break;

                default:
                    Bukkit.getConsoleSender().sendMessage(casual);
            }

        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}

    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private static void addLogger(EnhancedLogger e) {
        logger = e;
    }

    public static EnhancedLogger log() {
        return logger;
    }
}
