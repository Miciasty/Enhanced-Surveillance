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
    @Column(name = "event_value", columnDefinition = "TINYBLOB")
    private Map<String, byte[]> eventData = new HashMap<>();

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

        setEventData(eventData);

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

        setEventData(eventData);

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

    public Map<String, byte[]> getEventData() { return eventData; }

    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }

    private void setYaw(float yaw) {
        this.yaw = Math.round(yaw * 1000) / 1000f;
    }

    private void setPitch(float pitch) {
        this.pitch = Math.round(pitch * 1000) / 1000f;
    }

    // --- --- --- --- --- --- Compression --- --- --- --- --- --- //

    private void setEventData(Map<String, String> eventData) {
        this.eventData = new HashMap<>();
        for (Map.Entry<String, String> entry : eventData.entrySet()) {
            try {
                this.eventData.put(entry.getKey(), Compression.compress(entry.getValue()));
            } catch (Exception e) {
                ES.getInstance().getEnhancedLogger().severe(e.getMessage());
            }
        }
    }

    private Map<String, String> getDecompressedEventData() {
        Map<String, String> decompressedData = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : eventData.entrySet()) {
            try {
                decompressedData.put(entry.getKey(), Compression.decompress(entry.getValue()));
            } catch (Exception e) {
                ES.getInstance().getEnhancedLogger().severe(e.getMessage());
            }
        }
        return decompressedData;
    }

}
