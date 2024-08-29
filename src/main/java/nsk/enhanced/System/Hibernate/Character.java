package nsk.enhanced.System.Hibernate;

import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link Character} class represents a player in the server, stored in the `surveillance_players` database table.
 * This class is responsible for managing player data and statistics.
 */
@Entity
@Table(name = "surveillance_players")
public class Character {

    /**
     * A static list that holds all players {@link Character} in memory.
     * This list is used to track all players currently managed by the plugin.
     */
    private static final List<Character> characters = new ArrayList<Character>();

    // --- --- --- --- --- --- --- --- --- --- --- --- --- //

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private Long eventsAmount;

    @Column(nullable = true)
    private Long averageSessionTime;

    // --- --- --- --- --- --- --- --- --- --- --- --- --- //

    /**
     * Default constructor for JPA.
     */
    public Character() {}

    /**
     * Constructs a new {@link Character} based on the given Player object.
     *
     * @param player the Player object from which to create this {@link Character}
     */
    public Character(Player player) {
        this.uuid = player.getUniqueId().toString();
        this.name = player.getName();
    }

    /**
     * Returns the ID of this {@link Character}.
     *
     * @return the ID of the character
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the UUID of this {@link Character}.
     *
     * @return the UUID of the character
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Returns the name of this {@link Character}.
     *
     * @return the name of the character
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the total number of events associated with this {@link Character}.
     *
     * @return the number of events, or null if not set
     */
    public Long getEventsAmount() {
        return eventsAmount;
    }

    /**
     * Returns the average session time of this {@link Character}.
     *
     * @return the average session time, or null if not set
     */
    public Long getAverageSessionTime() {
        return averageSessionTime;
    }

    // --- --- --- --- --- //

    /**
     * Updates the statistics for this {@link Character}, including the total number of events
     * and the average session time, and saves the updated data to the database.
     */
    public void updateStatistics() {
        long totalEvents = Event.getEventsSizeForCharacter(this);
        long totalJoinEvents = Event.getEventsSizeForCharacter(this, "join");
        long totalQuitEvents = Event.getEventsSizeForCharacter(this, "quit");

        if (totalJoinEvents > 0 && totalQuitEvents > 0) {
            long totalSessions = Math.min(totalJoinEvents, totalQuitEvents);
            long totalSessionTime = Event.getTotalSessionTimeForCharacter(this);
            this.averageSessionTime = totalSessionTime / totalSessions;
        } else {
            this.averageSessionTime = 0L;
        }

        this.eventsAmount = totalEvents;

        DatabaseService.saveEntity(this);
    }

    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    /**
     * Returns the list of all players {@link Character} currently in memory.
     *
     * @return a list of all Characters
     */
    public static List<Character> getCharacters() {
        return characters;
    }

    /**
     * Retrieves a {@link Character} by its ID.
     *
     * @param id the ID of the character to retrieve
     * @return the {@link Character} with the specified ID, or null if not found
     */
    public static Character getCharacter(int id) {
        for (Character character : characters) {
            if (character.getId() == id) {
                return character;
            }
        }

        return null;
    }

    /**
     * Retrieves a {@link Character} by its UUID.
     *
     * @param uuid the UUID of the character to retrieve
     * @return the {@link Character} with the specified UUID, or null if not found
     */
    public static Character getCharacter(String uuid) {
        for (Character character : characters) {
            if (character.getUuid().equals(uuid)) {
                return character;
            }
        }

        return null;
    }

    /**
     * Retrieves a {@link Character} based on a Player object.
     * If the character does not exist in memory, a new {@link Character} is created, added to the list, and saved to the database.
     *
     * @param player the Player object to retrieve or create a {@link Character} from
     * @return the corresponding Character
     */
    public static Character getCharacter(Player player) {
        for (Character character : characters) {
            if (character.getUuid().equals(player.getUniqueId().toString())) {
                return character;
            }
        }

        Character character = new Character(player);
        addCharacter(character);

        return character;
    }

    /**
     * Adds a new {@link Character} to the list and persists it in the database.
     *
     * @param character the {@link Character} to add
     */
    public static void addCharacter(Character character) {
        try {
            characters.add(character);
            DatabaseService.saveEntity(character);

        } catch (Exception e) {
            characters.remove(character);
            EnhancedLogger.log().severe(e.getMessage());
        }
    }

    /**
     * Removes a {@link Character} from the list and deletes it from the database.
     *
     * @param character the {@link Character} to remove
     */
    public static void removeCharacter(Character character) {
        try {
            characters.remove(character);
            DatabaseService.deleteEntity(character);

        } catch (Exception e) {
            characters.add(character);
            EnhancedLogger.log().severe(e.getMessage());
        }
    }

}
