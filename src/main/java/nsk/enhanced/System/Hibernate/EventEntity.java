package nsk.enhanced.System.Hibernate;

import nsk.enhanced.System.ES;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "surveillance_events")
public class EventEntity {

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
    private Map<String, String> eventData = new HashMap<>();

    // --- --- --- --- --- --- --- --- --- --- --- --- --- //

    public EventEntity() {}

    public EventEntity(

            String  type,
            String  uuid,
            String  world,

            Map<String, String> eventData

            ) {

        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.uuid = uuid;
        this.world = world;

        this.eventData = eventData;

    }

    // --- --- --- --- --- --- Main --- --- --- --- --- --- //

    public int getID() { return ID; }
    public String getType() { return type; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public String getUuid() { return uuid; }
    public String getWorld() { return world; }

    public Map<String, String> getEventData() { return eventData; }


}
