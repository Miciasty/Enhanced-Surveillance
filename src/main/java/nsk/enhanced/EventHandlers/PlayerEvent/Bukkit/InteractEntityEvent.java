package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import com.destroystokyo.paper.entity.villager.ReputationType;
import nsk.enhanced.EventHandlers.EventData;
import nsk.enhanced.EventHandlers.PlayerEvent.Bukkit.Enum.VillagerData;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Base.Minecraft.Creature;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

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

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        Entity target = event.getRightClicked();

        if (target instanceof Player) {
            return;
        }

        Map<String, String> eventData = new LinkedHashMap<>();

        Creature entity = null;

        int level = config.getInt("events.PlayerInteractEntityEvent.level", 0);
        if (Check.inRange(1, 3, level)) {

            if (target.getType().getEntityClass() != null) {

                entity = new Creature( target.getType().getEntityClass().getSimpleName() );

                //eventData.put(EventData.TYPE.name(),   target.getType().getEntityClass().getSimpleName());
                EnhancedLogger.log().config(EventData.TYPE.name() + ": <gold>" + target.getType().getEntityClass().getSimpleName());
            }

            if (level > 1) {

                if (target.getCustomName() != null) {

                    entity = new Creature( target.getCustomName() );
                    //eventData.put(EventData.CNAME.name(), target.getCustomName());
                    EnhancedLogger.log().config(EventData.CNAME.name() + ": <red>" + target.getCustomName());
                }

                if (target instanceof LivingEntity) {
                    eventData.put(EventData.HEALTH.name(),     String.valueOf( ((LivingEntity) target).getHealth()) );
                    EnhancedLogger.log().config(EventData.HEALTH.name() + ": <green>" + ((LivingEntity) target).getHealth() + " / " + ((LivingEntity) target).getMaxHealth());
                }

            }

            if (level > 2 && target instanceof Villager) {

                Villager villager = (Villager) target;

                if ( villager.isAdult() ) {
                    eventData.put(VillagerData.V_PROF.name(),      villager.getProfession().name().toUpperCase());
                    eventData.put(VillagerData.V_REP.name(),       String.valueOf( villager.getReputation(player.getUniqueId()).getReputation(ReputationType.TRADING)) );
                    eventData.put(VillagerData.V_LVL.name(),       String.valueOf( villager.getVillagerLevel() ));
                    eventData.put(VillagerData.V_EXP.name(),       String.valueOf( villager.getVillagerExperience() ));
                    eventData.put(VillagerData.V_RSTC.name(),      String.valueOf( villager.getRestocksToday() ));
                    eventData.put(VillagerData.V_WORK.name(),      String.valueOf( villager.isTrading() ));

                    EnhancedLogger.log().config(VillagerData.V_PROF.name() + ": <green>" + villager.getProfession().toString().toUpperCase());
                    EnhancedLogger.log().config(VillagerData.V_REP.name()  + ": <aqua>" + villager.getReputation(player.getUniqueId()).getReputation(ReputationType.TRADING));
                    EnhancedLogger.log().config(VillagerData.V_LVL.name()  + ": <green>" + villager.getVillagerLevel());
                    EnhancedLogger.log().config(VillagerData.V_EXP.name()  + ": <green>" + villager.getVillagerExperience());
                    EnhancedLogger.log().config(VillagerData.V_RSTC.name() + ": <blue>" + villager.getRestocksToday());
                    EnhancedLogger.log().config(VillagerData.V_WORK.name() + ": <blue>" + villager.isTrading());

                }

            }

        } else {
            if (!Check.inRange(0, 3, level)) {
                EnhancedLogger.log().warning("<green>'events.PlayerInteractEntityEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
            }
        }


        try {

            Event e = new Event("interactEntity", player, target.getLocation(), eventData);

            if (entity == null) {
                MemoryService.logEventAsync(() -> {
                    DatabaseService.saveEntity(e);
                });
            } else {
                Creature ent = entity;      // No idea why but without IntelliJ IDEA tells me something is wrong here...

                MemoryService.logEventAsync(() -> {
                    DatabaseService.saveEntity(e);
                    DatabaseService.saveEntity(ent);
                });
            }



        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/interactEntity - " + ex.getMessage());
        }
    }

}