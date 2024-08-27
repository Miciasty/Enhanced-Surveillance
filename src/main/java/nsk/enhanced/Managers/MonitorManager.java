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


}
