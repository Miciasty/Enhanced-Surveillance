package nsk.enhanced.System.Hibernate;

import nsk.enhanced.System.DatabaseService;
import nsk.enhanced.System.EnhancedLogger;
import org.bukkit.World;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link WorldEntity} class represents a world in the server, stored in the `surveillance_worlds` database table.
 * This class is used to manage and persist world data.
 */
@Entity
@Table(name = "surveillance_worlds")
public class WorldEntity {

    /**
     * A static list that holds all {@link WorldEntity} in memory.
     * This list is used to track all worlds currently managed by the plugin and database.
     */
    private static final List<WorldEntity> worlds = new ArrayList<WorldEntity>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String world;

    // --- --- --- --- --- --- --- --- --- --- --- --- --- //

    /**
     * Default constructor for JPA.
     */
    public WorldEntity() {}

    /**
     * Constructs a new {@link WorldEntity} based on the given World object.
     *
     * @param world the World object from which to create new {@link WorldEntity}
     */
    public WorldEntity(World world) {
        this.world = world.getName();
    }

    /**
     * Returns the ID of this {@link WorldEntity}.
     *
     * @return the ID of the world
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the world associated with this {@link WorldEntity}.
     *
     * @return the name of the world
     */
    public String getWorld() {
        return world;
    }


    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    /**
     * Returns the list of all {@link WorldEntity} currently in memory.
     *
     * @return a list of all {@link WorldEntity} in memory
     */
    public static List<WorldEntity> getWorlds() {
        return worlds;
    }

    /**
     * Retrieves a {@link WorldEntity} by its ID.
     *
     * @param id the ID of the world to retrieve
     * @return the {@link WorldEntity} with the specified ID, or null if not found
     */
    public static WorldEntity getWorld(int id) {
        for (WorldEntity world : worlds) {
            if (world.getId() == id) {
                return world;
            }
        }

        return null;
    }

    /**
     * Retrieves a {@link WorldEntity} by its name.
     *
     * @param name the name of the world to retrieve
     * @return the {@link WorldEntity} with the specified name, or null if not found
     */
    public static WorldEntity getWorld(String name) {
        for (WorldEntity world : worlds) {
            if (name.equals(world.getWorld())) {
                return world;
            }
        }

        return null;
    }

    /**
     * Retrieves a {@link WorldEntity} based on a World object.
     * If the world does not exist in memory, a new {@link WorldEntity} is created, added to the list, and saved to the database.
     *
     * @param world the World object to retrieve or create a {@link WorldEntity} from
     * @return the corresponding {@link WorldEntity} instance
     */
    public static WorldEntity getWorld(World world) {
        for (WorldEntity worldEntity : worlds) {
            if (worldEntity.getWorld().equalsIgnoreCase(world.getName())) {
                return worldEntity;
            }
        }

        WorldEntity worldEntity = new WorldEntity(world);
        addWorld(worldEntity);

        return worldEntity;
    }

    /**
     * Adds a new world to the list and persists it in the database.
     *
     * @param world the World object to add
     */
    public static void addWorld(World world) {

        WorldEntity worldEntity = new WorldEntity(world);

        try {
            worlds.add(worldEntity);
            DatabaseService.saveEntity(worldEntity);

        } catch (Exception e) {
            worlds.remove(worldEntity);
            EnhancedLogger.log().severe(e.getMessage());
        }
    }

    /**
     * Adds a new {@link WorldEntity} to the list and persists it in the database.
     *
     * @param world the {@link WorldEntity} instance to add
     */
    public static void addWorld(WorldEntity world) {
        try {
            worlds.add(world);
            DatabaseService.saveEntity(world);

        } catch (Exception e) {
            worlds.remove(world);
            EnhancedLogger.log().severe(e.getMessage());
        }
    }

    /**
     * Removes a world from the list and deletes it from the database.
     *
     * @param world the World object to remove
     */
    public static void removeWorld(World world) {

        WorldEntity worldEntity = getWorld(world);

        try {
            worlds.remove(worldEntity);
            DatabaseService.deleteEntity(worldEntity);

        } catch (Exception e) {
            worlds.add(worldEntity);
            EnhancedLogger.log().severe(e.getMessage());
        }    }

    /**
     * Removes a {@link WorldEntity} from the list and deletes it from the database.
     *
     * @param world the {@link WorldEntity} instance to remove
     */
    public static void removeWorld(WorldEntity world) {
        try {
            worlds.remove(world);
            DatabaseService.deleteEntity(world);

        } catch (Exception e) {
            worlds.add(world);
            EnhancedLogger.log().severe(e.getMessage());
        }
    }

}
