package nsk.enhanced.System.Hibernate;

import org.bukkit.entity.Player;

import javax.persistence.*;

@Entity
@Table(name = "surveillance_players")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String name;

    // --- --- --- --- --- --- --- --- --- --- --- --- --- //

    public Character() {}

    public Character(Player player) {
        this.uuid = player.getUniqueId().toString();
        this.name = player.getName();
    }

    public int getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
