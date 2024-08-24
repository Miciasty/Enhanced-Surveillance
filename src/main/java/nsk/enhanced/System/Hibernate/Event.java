package nsk.enhanced.System.Hibernate;

import nsk.enhanced.System.ES;
import nsk.enhanced.System.MemoryService;
import nsk.enhanced.System.Utils.Compression;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.hibernate.Session;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "surveillance_events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;

    @ManyToOne
    @JoinColumn(name = "character_id", nullable = false)
    private Character character;

    @Column(nullable = true)
    private String type;

    @Column(nullable = true)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "world_id", nullable = false)
    private WorldEntity world;

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

        this.type       = type;
        this.timestamp  = LocalDateTime.now();
        this.character  = Character.getCharacter(player);
        this.world      = WorldEntity.getWorld(player.getWorld());

        setEventData(eventData, new Location(player.getWorld(), 0, 0, 0, 0, 0));

        this.x = player.getLocation().getBlockX();
        this.y = player.getLocation().getBlockY();
        this.z = player.getLocation().getBlockZ();
        setYaw(player.getLocation().getYaw());
        setPitch(player.getLocation().getPitch());

        if (type.equals("join") || type.equals("quit")) {
            character.updateStatistics();
        }

    }

    public Event(

            String  type,
            Player player,
            Location location,

            Map<String, String> eventData

            ) {

        this.type       = type;
        this.timestamp  = LocalDateTime.now();
        this.character  = Character.getCharacter(player);
        this.world      = WorldEntity.getWorld(player.getWorld());

        setEventData(eventData, location);

        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        setYaw(location.getYaw());
        setPitch(location.getPitch());

        if (type.equals("join") || type.equals("quit")) {
            character.updateStatistics();
        }

    }

    // --- --- --- --- --- --- Main --- --- --- --- --- --- //

    public int getID() { return ID; }
    public String getType() { return type; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public Character getCharacter() { return character; }
    public WorldEntity getWorldEntity() { return world; }

    public OfflinePlayer getPlayer() {
        return ES.getInstance().getServer().getOfflinePlayer( character.getUuid() );
    }
    public World getWorld() {
        return ES.getInstance().getServer().getWorld(world.getWorld());
    }

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

    public Map<String, String> getDecompressedEventData() {
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


    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    public static List<Event> getEventsForCharacter(Character character) {
        List<Event> events = new ArrayList<>();

        try (Session session = ES.getInstance().getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Event> query = builder.createQuery(Event.class);
            Root<Event> root = query.from(Event.class);

            query.select(root).where(builder.equal(root.get("character"), character));

            events = session.createQuery(query).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to load events for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + ": " + e.getMessage());
        }

        return events;
    }
    public static long getEventsSizeForCharacter(Character character) {
        long size = 0;

        try (Session session = ES.getInstance().getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<Event> root = query.from(Event.class);

            query.select(builder.count(root))
                            .where(builder.equal(root.get("character"), character));

            size = session.createQuery(query).getSingleResult();

            session.getTransaction().commit();
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to count events for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + ": " + e.getMessage());
        }

        return size;
    }

    public static List<Event> getEventsForCharacter(Character character, String type) {
        List<Event> events = new ArrayList<>();

        try (Session session = ES.getInstance().getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Event> query = builder.createQuery(Event.class);
            Root<Event> root = query.from(Event.class);

            query.select(root)
                    .where(
                            builder.and(
                                    builder.equal(root.get("character"), character),
                                    builder.equal(root.get("type"), type)
                            )
                    );


            events = session.createQuery(query).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to load events for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + " with type " + type + ": " + e.getMessage());
        }

        return events;
    }
    public static long getEventsSizeForCharacter(Character character, String type) {
        long count = 0;

        try (Session session = ES.getInstance().getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<Event> root = query.from(Event.class);

            query.select(builder.count(root))
                    .where(
                            builder.and(
                                    builder.equal(root.get("character"), character),
                                    builder.equal(root.get("type"), type)
                            )
                    );


            count = session.createQuery(query).getSingleResult();

            session.getTransaction().commit();
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to count events for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + " with type " + type + ": " + e.getMessage());
        }

        return count;
    }

    public static List<Event> getEventsForCharacter(Character character, String type, LocalDate localDate) {
        List<Event> events = new ArrayList<>();

        try (Session session = ES.getInstance().getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Event> query = builder.createQuery(Event.class);
            Root<Event> root = query.from(Event.class);

            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

            query.select(root)
                    .where(
                            builder.and(
                                    builder.equal(root.get("character"), character),
                                    builder.equal(root.get("type"), type),
                                    builder.between(root.get("timestamp"), startOfDay, endOfDay)
                            )
                    );


            events = session.createQuery(query).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to load events for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + " with type " + type
                    + " at " + localDate + ": " + e.getMessage());
        }

        return events;
    }
    public static long getEventsSizeForCharacter(Character character, String type, LocalDate localDate) {
        long count = 0;

        try (Session session = ES.getInstance().getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<Event> root = query.from(Event.class);

            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

            query.select(builder.count(root))
                    .where(
                            builder.and(
                                    builder.equal(root.get("character"), character),
                                    builder.equal(root.get("type"), type),
                                    builder.between(root.get("timestamp"), startOfDay, endOfDay)
                            )
                    );


            count = session.createQuery(query).getSingleResult();

            session.getTransaction().commit();
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to count events for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + " with type " + type
                    + " at " + localDate + ": " + e.getMessage());
        }

        return count;
    }

    public static long getTotalSessionTimeForCharacter(Character character) {
        long totalSessionTime = 0;

        try (Session session = ES.getInstance().getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Event> query = builder.createQuery(Event.class);
            Root<Event> root = query.from(Event.class);

            query.select(root)
                    .where(
                            builder.and(
                                    builder.equal(root.get("character"), character),
                                    builder.equal(root.get("type"), "join")
                            )
                    )
                    .orderBy(builder.asc(root.get("timestamp")));

            List<Event> joinEvents = session.createQuery(query).getResultList();

            query.select(root)
                    .where(
                            builder.and(
                                    builder.equal(root.get("character"), character),
                                    builder.equal(root.get("type"), "quit")
                            )
                    )
                    .orderBy(builder.asc(root.get("timestamp")));

            List<Event> quitEvents = session.createQuery(query).getResultList();

            session.getTransaction().commit();

            for (int i=0; i < Math.min(joinEvents.size(), quitEvents.size()); i++) {
                Event joinEvent = joinEvents.get(i);
                Event quitEvent = quitEvents.get(i);

                if (joinEvent.getTimestamp() != null && quitEvent.getTimestamp() != null) {
                    long sessionTime = Duration.between(joinEvent.getTimestamp(), quitEvent.getTimestamp()).getSeconds();
                    totalSessionTime += sessionTime;
                }
            }

        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to calculate total session time for character " + character.getName() + ": " + e.getMessage());
        }

        return totalSessionTime;
    }

    public static Event getLastEventByType(Character character, String type) {

        Event lastEvent = null;

        try (Session session = ES.getInstance().getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Event> query = builder.createQuery(Event.class);
            Root<Event> root = query.from(Event.class);

            query.select(root)
                    .where(
                            builder.and(
                                    builder.equal(root.get("character"), character),
                                    builder.equal(root.get("type"), type)
                            )
                    )
                    .orderBy(builder.asc(root.get("timestamp")));

            List<Event> events = session.createQuery(query)
                    .setMaxResults(1)
                    .getResultList();

            if (!events.isEmpty()) {
                lastEvent = events.get(0);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            ES.getInstance().getEnhancedLogger().severe("Failed to load last event for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + " with type " + type + ": " + e.getMessage());
        }

        return lastEvent;

    }

}
