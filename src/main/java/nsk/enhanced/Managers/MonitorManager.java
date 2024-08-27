package nsk.enhanced.Managers;

import nsk.enhanced.EnhancedSurveillance;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MonitorManager {

    private static final EnhancedSurveillance plugin = ES.getInstance();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd - HH:mm:ss");

    public static void saveEvent(Event event) {

        try {

            MemoryService.logEventAsync(() -> {

                ES.getInstance().saveEntity(event);

            });

        } catch (Exception e) {
            EnhancedLogger.log().severe(e.getMessage());
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

    private static String sanitizeString(String input) {
        if (input != null) {
            return input.replaceAll("[\r\n]", "").replaceAll("[^\\x20-\\x7E]", "");
        }
        return "";
    }

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
            EnhancedLogger.log().severe(e.getMessage());
        }
    }

}
