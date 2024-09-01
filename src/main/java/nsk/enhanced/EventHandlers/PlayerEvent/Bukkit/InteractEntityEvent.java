package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import com.destroystokyo.paper.entity.villager.ReputationType;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link InteractEntityEvent} class listens for the {@link PlayerInteractEntityEvent} in Minecraft
 * and handles the event based on the configuration. It captures data about the entity
 * that the player interacted with and logs the event.
 */
public class InteractEntityEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerInteractEntityEvent}. This method processes interactions between a player
     * and an entity by capturing relevant data based on the configured detail level.
     *
     * @param event the {@link PlayerInteractEntityEvent} triggered when a player interacts with an entity
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        if (!config.getBoolean("events.PlayerInteractEntityEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();
        Entity target = event.getRightClicked();

        if (target instanceof Player || target instanceof Monster) {
            return;
        }

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerInteractEntityEvent.level", 0);
        if (Check.inRange(1, 3, level)) {

            if (target.getType().getEntityClass() != null) {
                eventData.put("type",   target.getType().getEntityClass().getSimpleName());
                EnhancedLogger.log().config("type: <gold>" + target.getType().getEntityClass().getSimpleName());
            }

            if (level > 1) {

                if (target.getCustomName() != null) {
                    eventData.put("customName", target.getCustomName());
                    EnhancedLogger.log().config("health: <red>" + target.getCustomName());
                }

                if (target instanceof LivingEntity) {
                    eventData.put("health",     String.valueOf( ((LivingEntity) target).getHealth()) );
                    EnhancedLogger.log().config("health: <red>" + ((LivingEntity) target).getHealth());
                }
            }

            if (level > 2 && target instanceof Villager) {

                Villager villager = (Villager) target;

                if ( villager.isAdult() ) {
                    eventData.put("vprof",      villager.getProfession().toString().toUpperCase());
                    eventData.put("vrep",       String.valueOf( villager.getReputation(player.getUniqueId()).getReputation(ReputationType.TRADING)) );
                    eventData.put("vlvl",       String.valueOf( villager.getVillagerLevel() ));
                    eventData.put("vexp",       String.valueOf( villager.getVillagerExperience() ));
                    eventData.put("vrstc",      String.valueOf( villager.getRestocksToday() ));
                    eventData.put("vwork",      String.valueOf( villager.isTrading() ));

                    EnhancedLogger.log().config("vprof: <green>" + villager.getProfession().toString().toUpperCase());
                    EnhancedLogger.log().config("vrep: <aqua>" + villager.getReputation(player.getUniqueId()).getReputation(ReputationType.TRADING));
                    EnhancedLogger.log().config("vlvl: <green>" + villager.getVillagerLevel());
                    EnhancedLogger.log().config("vexp: <green>" + villager.getVillagerExperience());
                    EnhancedLogger.log().config("vrstc: <blue>" + villager.getRestocksToday());
                    EnhancedLogger.log().config("vwork: <blue>" + villager.isTrading());

                }

            }

        } else if (!Check.inRange(0, 3, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerInteractEntityEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }


        try {

            Event e = new Event("interactEntity", player, target.getLocation(), eventData);

            MemoryService.logEventAsync(() -> {
                DatabaseService.saveEntity(e);
            });

        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/interactEntity - " + ex.getMessage());
        }
    }

}