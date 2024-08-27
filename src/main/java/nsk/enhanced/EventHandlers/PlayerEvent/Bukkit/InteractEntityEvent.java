package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import com.destroystokyo.paper.entity.villager.ReputationType;
import nsk.enhanced.Managers.MonitorManager;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.Configuration.ServerConfiguration;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.ChatEvent.Command;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class InteractEntityEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

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
        if (level > 0 && level < 4) {

            if (target.getType().getEntityClass() != null) {
                eventData.put("type",   target.getType().getEntityClass().getSimpleName());
                if (ES.debugMode()) EnhancedLogger.log().info("type: <gold>" + target.getType().getEntityClass().getSimpleName());
            }

            if (level > 1) {

                if (target.getCustomName() != null) {
                    eventData.put("customName", target.getCustomName());
                    if (ES.debugMode()) EnhancedLogger.log().info("health: <red>" + target.getCustomName());
                }

                if (target instanceof LivingEntity) {
                    eventData.put("health",     String.valueOf( ((LivingEntity) target).getHealth()) );
                    if (ES.debugMode()) EnhancedLogger.log().info("health: <red>" + ((LivingEntity) target).getHealth());
                }
            }

            if (level > 2) {

                if (target instanceof Villager) {

                    Villager villager = (Villager) target;

                    if ( villager.isAdult() ) {
                        eventData.put("vprof",      villager.getProfession().toString().toUpperCase());
                        eventData.put("vrep",       String.valueOf( villager.getReputation(player.getUniqueId()).getReputation(ReputationType.TRADING)) );
                        eventData.put("vlvl",       String.valueOf( villager.getVillagerLevel() ));
                        eventData.put("vexp",       String.valueOf( villager.getVillagerExperience() ));
                        eventData.put("vrstc",      String.valueOf( villager.getRestocksToday() ));
                        eventData.put("vwork",      String.valueOf( villager.isTrading() ));

                        if (ES.debugMode()) {
                            EnhancedLogger.log().info("vprof: <green>" + villager.getProfession().toString().toUpperCase());
                            EnhancedLogger.log().info("vrep: <aqua>" + villager.getReputation(player.getUniqueId()).getReputation(ReputationType.TRADING));
                            EnhancedLogger.log().info("vlvl: <green>" + villager.getVillagerLevel());
                            EnhancedLogger.log().info("vexp: <green>" + villager.getVillagerExperience());
                            EnhancedLogger.log().info("vrstc: <blue>" + villager.getRestocksToday());
                            EnhancedLogger.log().info("vwork: <blue>" + villager.isTrading());
                        }


                    }

                }

            }

        }


        try {

            Event e = new Event("interactEntity", player, target.getLocation(), eventData);

            MonitorManager.saveEvent(e);
        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/interactEntity - " + ex.getMessage());
        }
    }

}