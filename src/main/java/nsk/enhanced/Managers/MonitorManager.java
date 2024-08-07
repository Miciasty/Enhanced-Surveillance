package nsk.enhanced.Managers;

import nsk.enhanced.EnhancedSurveillance;
import nsk.enhanced.System.ES;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
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
                FileWriter writer = new FileWriter(eventFile);

                String eventName = type.split("/")[1];
                eventName = eventName.substring(0, 1).toUpperCase() + eventName.substring(1);

                writer.write("#\n");
                writer.write("# Enhanced Surveillance - " + eventName + " Event File\n");
                writer.write("#\n\n");

                writer.write("uuid: "    + uuid + "\n");
                writer.write("player: "  + name + "\n\n");

                writer.write("events: []\n");

                writer.close();
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
        data.put("player_world",       player.getWorld().getName().toUpperCase() );
        data.put("player_location",    String.format("{x: %d, y: %d, z: %d}", player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()).toUpperCase());
        events.add(data);

        config.set("events", events);
        try {
            config.save(eventFile);
            addNewLineBetweenEvents(eventFile);

        } catch (Exception e) {
            plugin.getEnhancedLogger().severe(e.getMessage());
        }

    }

    public static Map<String, Object> getEvent(Player player, String type) {

        String uuid = player.getUniqueId().toString();
        File eventFile = new File(plugin.getDataFolder(), "Surveillance Data/" + uuid + "/" + type + ".yml");

        if (!eventFile.exists()) {
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(eventFile);
        List<Map<String, Object>> events = (List<Map<String, Object>>) config.getList("events");

        if (events == null || events.isEmpty()) {
            return null;
        }

        return events.get(events.size() - 1);

    }

    // --- --- --- --- //

    private static void addNewLineBetweenEvents(File eventfile) {
        try {
            List<String> lines = Files.readAllLines(eventfile.toPath());
            List<String> modifiedLines = new ArrayList<>();
            boolean previousLineWasEmpty = false;

            for (String line : lines) {
                if (line.startsWith("- ") && !previousLineWasEmpty) {
                    modifiedLines.add("");
                    modifiedLines.add("# -- # -- # -- # -- # -- # -- # -- # -- # -- # -- #");
                    modifiedLines.add("");
                }

                if (line.startsWith("  timestamp: ") && !previousLineWasEmpty) {
                    modifiedLines.add("");
                }

                modifiedLines.add(line);
                previousLineWasEmpty = line.isEmpty();
            }
            Files.write(eventfile.toPath(), modifiedLines);

        } catch (Exception e) {
            plugin.getEnhancedLogger().severe(e.getMessage());
        }
    }

}
