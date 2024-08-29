package nsk.enhanced.System.Hibernate;

import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import nsk.enhanced.System.Utils.Compression;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.hibernate.Hibernate;
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

/**
 * <p>
 * The {@link Event} class represents an event occurring in the server, stored in the `surveillance_events` database table.
 * This class is responsible for capturing and managing various types of events related to players, including their locations,
 * worlds, and additional data.
 * </p>
 *
 * <p>The Event class links together several other entities, including {@link Character}, {@link WorldEntity}, and {@link EventDetails},
 * to provide a comprehensive record of player actions and state changes in the game.</p>
 */
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

    /**
     * Default constructor for JPA.
     */
    public Event() {}

    /**
     * Constructs a new {@link Event} with the specified event type, player, and event data.
     * The event is initialized with the player's current location.
     *
     * @param type the type of event (e.g., "join", "interact")
     * @param player the player associated with the event
     * @param eventData a map of event data to be associated with this event
     */
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

    /**
     * Constructs a new {@link Event} with the specified type, player, location, and event data.
     *
     * @param type the type of event (e.g., "join", "move")
     * @param player the player associated with the event
     * @param location the location where the event occurred
     * @param eventData a map of event data to be associated with this event
     */
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

    /**
     * Returns the ID of this {@link Event}.
     *
     * @return the ID of the event
     */
    public int getID() { return ID; }

    /**
     * Returns the type of this {@link Event}.
     *
     * @return the type of event (e.g., "move", "quit")
     */
    public String getType() { return type; }

    /**
     * Returns the timestamp when this {@link Event} occurred.
     *
     * @return the timestamp of the event
     */
    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Returns the {@link Character} associated with this {@link Event}.
     *
     * @return the {@link Character} associated with this event
     */
    public Character getCharacter() { return character; }

    /**
     * Returns the {@link WorldEntity} where this {@link Event} occurred.
     *
     * @return the {@link WorldEntity} associated with this event
     */
    public WorldEntity getWorldEntity() { return world; }

    /**
     * Returns the OfflinePlayer associated with this {@link Event}, based on the Character's UUID.
     *
     * @return the OfflinePlayer associated with this event
     */
    public OfflinePlayer getPlayer() {
        return ES.getInstance().getServer().getOfflinePlayer( character.getUuid() );
    }

    /**
     * Returns the World where this {@link Event} occurred, based on the WorldEntity's name.
     *
     * @return the World associated with this event
     */
    public World getWorld() {
        return ES.getInstance().getServer().getWorld(world.getWorld());
    }

    /**
     * Returns the map of event data associated with this {@link Event}.
     *
     * @return a map of event data keys and their associated {@link EventDetails}
     */
    public Map<String, EventDetails> getEventData() { return eventData; }

    /**
     * Returns the yaw (horizontal rotation) value of this {@link Event}.
     *
     * @return the yaw value
     */
    public float getYaw() { return yaw; }

    /**
     * Returns the pitch (vertical rotation) value of this {@link Event}.
     *
     * @return the pitch value
     */
    public float getPitch() { return pitch; }

    private void setYaw(float yaw) {
        this.yaw = Math.round(yaw * 1000) / 1000f;
    }

    private void setPitch(float pitch) {
        this.pitch = Math.round(pitch * 1000) / 1000f;
    }

    // --- --- --- --- --- --- Compression --- --- --- --- --- --- //

    /**
     * Compresses and sets the event data for this {@link Event}, associating each key-value pair
     * in the provided map with the corresponding {@link EventDetails}.
     *
     * @param eventData a map of event data keys and their associated values
     * @param location the location where the event occurred
     *
     * @see Compression#compress(String)
     */
    private void setEventData(Map<String, String> eventData, Location location) {
        this.eventData = new HashMap<>();
        for (Map.Entry<String, String> entry : eventData.entrySet()) {
            try {
                EventDetails details = new EventDetails( Compression.compress(entry.getValue()) , location);

                this.eventData.put(entry.getKey(), details);
            } catch (Exception e) {
                EnhancedLogger.log().severe(e.getMessage());
            }
        }
    }

    /**
     * Returns a map of decompressed event data for this {@link Event}.
     *
     * @return a map of event data keys and their associated decompressed values
     *
     * @see Compression#decompress(byte[])
     */
    public Map<String, String> getDecompressedEventData() {
        Map<String, String> decompressedData = new HashMap<>();
        for (Map.Entry<String, EventDetails> entry : eventData.entrySet()) {
            try {
                decompressedData.put(entry.getKey(), Compression.decompress(entry.getValue().getEventValue()));
            } catch (Exception e) {
                EnhancedLogger.log().severe(e.getMessage());
            }
        }
        return decompressedData;
    }


    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    // TO DO: Add rest of docs

    public static List<Event> getEventsForCharacter(Character character) {
        List<Event> events = new ArrayList<>();

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Event> query = builder.createQuery(Event.class);
            Root<Event> root = query.from(Event.class);

            query.select(root).where(builder.equal(root.get("character"), character));

            events = session.createQuery(query).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to load events for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + ": " + e.getMessage());
        }

        return events;
    }
    public static long getEventsSizeForCharacter(Character character) {
        long size = 0;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<Event> root = query.from(Event.class);

            query.select(builder.count(root))
                            .where(builder.equal(root.get("character"), character));

            size = session.createQuery(query).getSingleResult();

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to count events for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + ": " + e.getMessage());
        }

        return size;
    }

    public static List<Event> getEventsForCharacter(Character character, String type) {
        List<Event> events = new ArrayList<>();

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
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
            EnhancedLogger.log().severe("Failed to load events for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + " with type " + type + ": " + e.getMessage());
        }

        return events;
    }
    public static long getEventsSizeForCharacter(Character character, String type) {
        long count = 0;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
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
            EnhancedLogger.log().severe("Failed to count events for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + " with type " + type + ": " + e.getMessage());
        }

        return count;
    }

    public static List<Event> getEventsForCharacter(Character character, String type, LocalDate localDate) {
        List<Event> events = new ArrayList<>();

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
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
            EnhancedLogger.log().severe("Failed to load events for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + " with type " + type
                    + " at " + localDate + ": " + e.getMessage());
        }

        return events;
    }
    public static long getEventsSizeForCharacter(Character character, String type, LocalDate localDate) {
        long count = 0;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
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
            EnhancedLogger.log().severe("Failed to count events for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + " with type " + type
                    + " at " + localDate + ": " + e.getMessage());
        }

        return count;
    }

    public static long getTotalSessionTimeForCharacter(Character character) {
        long totalSessionTime = 0;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
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
            EnhancedLogger.log().severe("Failed to calculate total session time for character " + character.getName() + ": " + e.getMessage());
        }

        return totalSessionTime;
    }

    public static Event getLastEventByType(Character character, String type) {

        Event lastEvent = null;

        try (Session session = DatabaseService.getSessionFactory().openSession()) {
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

                Hibernate.initialize(lastEvent.getEventData());
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            EnhancedLogger.log().severe("Failed to load last event for "
                    + ((character != null) ? character.getUuid() : "unknown")
                    + " with type " + type + ": " + e.getMessage());
        }

        return lastEvent;

    }

}
