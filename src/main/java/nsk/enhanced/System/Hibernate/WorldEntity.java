package nsk.enhanced.System.Hibernate;

import nsk.enhanced.System.ES;
import nsk.enhanced.System.EnhancedLogger;
import org.bukkit.World;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "surveillance_worlds")
public class WorldEntity {

    private static final List<WorldEntity> worlds = new ArrayList<WorldEntity>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String world;

    // --- --- --- --- --- --- --- --- --- --- --- --- --- //

    public WorldEntity() {}

    public WorldEntity(World world) {
        this.world = world.getName();
    }

    public int getId() {
        return id;
    }

    public String getWorld() {
        return world;
    }


    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    public static List<WorldEntity> getWorlds() {
        return worlds;
    }

    public static WorldEntity getWorld(int id) {
        for (WorldEntity world : worlds) {
            if (world.getId() == id) {
                return world;
            }
        }

        return null;
    }

    public static WorldEntity getWorld(String name) {
        for (WorldEntity world : worlds) {
            if (name.equals(world.getWorld())) {
                return world;
            }
        }

        return null;
    }

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

    public static void addWorld(World world) {

        WorldEntity worldEntity = new WorldEntity(world);

        try {
            worlds.add(worldEntity);
            ES.getInstance().saveEntity(worldEntity);

        } catch (Exception e) {
            worlds.remove(worldEntity);
            EnhancedLogger.log().severe(e.getMessage());
        }
    }

    public static void addWorld(WorldEntity world) {
        try {
            worlds.add(world);
            ES.getInstance().saveEntity(world);

        } catch (Exception e) {
            worlds.remove(world);
            EnhancedLogger.log().severe(e.getMessage());
        }
    }

    public static void removeWorld(World world) {

        WorldEntity worldEntity = getWorld(world);

        try {
            worlds.remove(worldEntity);
            ES.getInstance().deleteEntity(worldEntity);

        } catch (Exception e) {
            worlds.add(worldEntity);
            EnhancedLogger.log().severe(e.getMessage());
        }    }

    public static void removeWorld(WorldEntity world) {
        try {
            worlds.remove(world);
            ES.getInstance().deleteEntity(world);

        } catch (Exception e) {
            worlds.add(world);
            EnhancedLogger.log().severe(e.getMessage());
        }
    }

}
