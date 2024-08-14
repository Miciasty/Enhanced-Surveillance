package nsk.enhanced.System.Hibernate;

import nsk.enhanced.System.ES;
import nsk.enhanced.System.Utils.Compression;

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

    @ElementCollection
    @CollectionTable(name = "surveillance_details", joinColumns = @JoinColumn(name = "event_id"))
    @MapKeyColumn(name = "event_key")
    @Column(name = "event_value", columnDefinition = "LONGBLOB")
    private Map<String, byte[]> eventData = new HashMap<>();

    // --- --- --- --- --- --- --- --- --- --- --- --- --- //

    public Event() {}

    public Event(

            String  type,
            String  uuid,
            String  world,

            Map<String, String> eventData

            ) {

        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.uuid = uuid;
        this.world = world;

        setEventData(eventData);

    }

    // --- --- --- --- --- --- Main --- --- --- --- --- --- //

    public int getID() { return ID; }
    public String getType() { return type; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public String getUuid() { return uuid; }
    public String getWorld() { return world; }

    public Map<String, byte[]> getEventData() { return eventData; }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- //

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
