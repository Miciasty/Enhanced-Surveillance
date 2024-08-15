package nsk.enhanced.System.Hibernate;

import org.bukkit.World;

import javax.persistence.*;

@Entity
@Table(name = "surveillance_worlds")
public class WorldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
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

}
