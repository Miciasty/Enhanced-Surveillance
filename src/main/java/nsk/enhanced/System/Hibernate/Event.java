package nsk.enhanced.System.Hibernate;

import nsk.enhanced.System.ES;
import nsk.enhanced.System.Utils.Compression;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "surveillance_events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;

    @Column(nullable = true)
    private String type;

    @Column(nullable = true)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String world;

    @Column(nullable = false)
    private int x, y, z;

    @Column(nullable = false)
    private float yaw, pitch;

    @ElementCollection
    @CollectionTable(name = "surveillance_details", joinColumns = @JoinColumn(name = "event_id"))
    @MapKeyColumn(name = "event_key")
    @AttributeOverrides({
            @AttributeOverride(name = "eventValue", column = @Column(name = "event_value")),
            @AttributeOverride(name = "pitch",      column = @Column(name = "pitch")),
            @AttributeOverride(name = "x",          column = @Column(name = "x")),
            @AttributeOverride(name = "y",          column = @Column(name = "y")),
            @AttributeOverride(name = "yaw",        column = @Column(name = "yaw")),
            @AttributeOverride(name = "z",          column = @Column(name = "z"))
    })
    private Map<String, EventDetails> eventData = new HashMap<>();

    // --- --- --- --- --- --- --- --- --- --- --- --- --- //

    public Event() {}

    public Event(

            String  type,
            Player player,

            Map<String, String> eventData

            ) {

        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.uuid = player.getUniqueId().toString();
        this.world = player.getWorld().getName();

        setEventData(eventData, new Location(player.getWorld(), 0, 0, 0, 0, 0));

        this.x = player.getLocation().getBlockX();
        this.y = player.getLocation().getBlockY();
        this.z = player.getLocation().getBlockZ();
        setYaw(player.getLocation().getYaw());
        setPitch(player.getLocation().getPitch());

    }

    public Event(

            String  type,
            Player player,
            Location location,

            Map<String, String> eventData

            ) {

        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.uuid = player.getUniqueId().toString();
        this.world = player.getWorld().getName();

        setEventData(eventData, location);

        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        setYaw(location.getYaw());
        setPitch(location.getPitch());

    }

    // --- --- --- --- --- --- Main --- --- --- --- --- --- //

    public int getID() { return ID; }
    public String getType() { return type; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public String getUuid() { return uuid; }
    public String getWorld() { return world; }

    public Map<String, EventDetails> getEventData() { return eventData; }

    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }

    private void setYaw(float yaw) {
        this.yaw = Math.round(yaw * 1000) / 1000f;
    }

    private void setPitch(float pitch) {
        this.pitch = Math.round(pitch * 1000) / 1000f;
    }

    // --- --- --- --- --- --- Compression --- --- --- --- --- --- //

    private void setEventData(Map<String, String> eventData, Location location) {
        this.eventData = new HashMap<>();
        for (Map.Entry<String, String> entry : eventData.entrySet()) {
            try {
                EventDetails details = new EventDetails( Compression.compress(entry.getValue()) , location);

                this.eventData.put(entry.getKey(), details);
            } catch (Exception e) {
                ES.getInstance().getEnhancedLogger().severe(e.getMessage());
            }
        }
    }

    private Map<String, String> getDecompressedEventData() {
        Map<String, String> decompressedData = new HashMap<>();
        for (Map.Entry<String, EventDetails> entry : eventData.entrySet()) {
            try {
                decompressedData.put(entry.getKey(), Compression.decompress(entry.getValue().getEventValue()));
            } catch (Exception e) {
                ES.getInstance().getEnhancedLogger().severe(e.getMessage());
            }
        }
        return decompressedData;
    }

}
