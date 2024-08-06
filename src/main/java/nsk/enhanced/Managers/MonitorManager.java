package nsk.enhanced.Managers;

import nsk.enhanced.EnhancedSurveillance;
import nsk.enhanced.System.ES;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MonitorManager {

    private static final EnhancedSurveillance plugin = ES.getInstance();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd - HH:mm:ss");

    public static void saveEvent(Player player, String type, Map<String, Object> data) {

        String uuid = player.getUniqueId().toString();
        String name = player.getName();

        File eventFile = new File(plugin.getDataFolder(), "Surveillance Data/" + uuid + "/" + type + ".yml");

        if (!eventFile.getParentFile().exists()) {
            eventFile.getParentFile().mkdirs();
        }

        if (!eventFile.exists()) {
            try {
                eventFile.createNewFile();
            } catch (IOException e) {
                plugin.getEnhancedLogger().severe(e.getMessage());
                return;
            }
        }

        try {

            if (eventFile.length() < 1) {
                plugin.getEnhancedLogger().info("Event file is empty");
                FileWriter writer = new FileWriter(eventFile);

                String eventName = type.split("/")[1];
                eventName = eventName.substring(0, 1).toUpperCase() + eventName.substring(1);

                plugin.getEnhancedLogger().info("Starting to write basic data.");

                writer.write("#\n");
                writer.write("# Enhanced Surveillance - " + eventName + " Event File\n");
                writer.write("#\n\n");

                writer.write("uuid: "    + uuid + "\n");
                writer.write("player: "  + name + "\n\n");

                writer.write("events: []\n");

                writer.close();
                plugin.getEnhancedLogger().info("Finished writing event file.");
            }

        } catch (Exception e) {
            plugin.getEnhancedLogger().severe(e.getMessage());
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(eventFile);

        List<Map<String, Object>> events = (List<Map<String, Object>>) config.getList("events");
        if (events == null) {
            events = new ArrayList<>();
        }

        String formattedTimestamp = LocalDateTime.now().format(formatter);
        data.put("timestamp", formattedTimestamp);
        events.add(data);

        config.set("events", events);
        try {
            config.save(eventFile);
        } catch (Exception e) {
            plugin.getEnhancedLogger().severe(e.getMessage());
        }

    }

}
