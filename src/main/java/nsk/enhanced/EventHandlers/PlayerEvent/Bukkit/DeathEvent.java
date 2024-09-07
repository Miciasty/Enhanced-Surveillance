package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit;

import nsk.enhanced.EventHandlers.EventData;
import nsk.enhanced.System.Configuration.EventsConfiguration;
import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Hibernate.Event;
import nsk.enhanced.System.Hibernate.Base.Messages.Event.Death;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Check;
import nsk.enhanced.System.Utils.Tools;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@link DeathEvent} class listens for the {@link PlayerDeathEvent} in Minecraft and handles the event
 * based on the configuration. It captures data when a player dies, such as the type of damage, the experience
 * dropped, and other relevant details depending on the configuration level.
 */
public class DeathEvent implements Listener {

    private static final FileConfiguration config = EventsConfiguration.getBukkitEventsFile();

    /**
     * Handles the {@link PlayerDeathEvent}. This method processes the event when a player dies,
     * capturing relevant data based on the configured detail level.
     *
     * @param event the {@link PlayerDeathEvent} triggered when a player dies
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        if (!config.getBoolean("events.PlayerDeathEvent.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        Map<String, String> eventData = new LinkedHashMap<>();

        int level = config.getInt("events.PlayerDeathEvent.level", 0);
        if (Check.inRange(1, 3, level)) {

            DamageSource damageSource = event.getDamageSource();

            eventData.put(EventData.TYPE.name(),            damageSource.getDamageType().toString());
            eventData.put(EventData.EXP.name(),             String.valueOf( event.getDroppedExp() ));

            EnhancedLogger.log().config(EventData.TYPE.name() + ": <red>" + damageSource.getDamageType() + "</red>");
            EnhancedLogger.log().config(EventData.EXP.name() + ": <red>" + event.getDroppedExp() + "</red>");

            if (level > 1) {
                if (damageSource.getCausingEntity() != null) {
                    eventData.put(EventData.ENTITY.name(),         damageSource.getCausingEntity().getClass().getSimpleName().toUpperCase());

                    EnhancedLogger.log().config(EventData.ENTITY.name() + ": <green>" + damageSource.getCausingEntity().getClass().getSimpleName().toUpperCase() + "</green>");
                }

                /*...*/
            }

            if (level > 2) {
                eventData.put(EventData.KEEP_INVENTORY.name(),      String.valueOf( event.getKeepInventory() ).toUpperCase());
                eventData.put(EventData.KEEP_LEVEL.name(),          String.valueOf( event.getKeepLevel() ).toUpperCase());
                eventData.put(EventData.REVIVE_HEALTH.name(),       String.valueOf( Tools.roundTo(event.getReviveHealth(), 2) ));

                EnhancedLogger.log().config(EventData.KEEP_INVENTORY.name() + ": <red>" + event.getKeepInventory() + "</red>");
                EnhancedLogger.log().config(EventData.KEEP_LEVEL.name() + ": <red>" + event.getKeepLevel() + "</red>");
                EnhancedLogger.log().config(EventData.REVIVE_HEALTH.name() + ": <green>" + Tools.roundTo(event.getReviveHealth(), 2) + "</green>");
            }

        } else if (!Check.inRange(0, 3, level)) {
            EnhancedLogger.log().warning("<green>'events.PlayerDeathEvent.level'</green> - Due to the provided invalid level value <red>[" + level + "]</red>, the event has defaulted to level <green>[0]</green>.");
        }

        Location location = event.getDamageSource().getDamageLocation();


        Event e = new Event("death", player, location, eventData);

        try {

            if (event.deathMessage() != null) {
                Death d = new Death(e, event.deathMessage().toString());

                MemoryService.logEventAsync(() -> {
                    DatabaseService.saveEntity(e);
                    DatabaseService.saveEntity(d);
                });
            } else {
                MemoryService.logEventAsync(() -> {
                    DatabaseService.saveEntity(e);
                });
            }


        } catch (Exception ex) {
            EnhancedLogger.log().severe("Failed to save PlayerEvents/death - " + ex.getMessage());
        }
    }

}
