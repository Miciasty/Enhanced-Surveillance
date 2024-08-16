package nsk.enhanced.System.Hibernate;

import nsk.enhanced.System.ES;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "surveillance_players")
public class Character {

    private static final List<Character> characters = new ArrayList<Character>();

    // --- --- --- --- --- --- --- --- --- --- --- --- --- //

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



    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    public static List<Character> getCharacters() {
        return characters;
    }

    public static Character getCharacter(int id) {
        for (Character character : characters) {
            if (character.getId() == id) {
                return character;
            }
        }

        return null;
    }

    public static Character getCharacter(String uuid) {
        for (Character character : characters) {
            if (character.getUuid().equals(uuid)) {
                return character;
            }
        }

        return null;
    }

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

    public static void addCharacter(Character character) {
        try {
            characters.add(character);
            ES.getInstance().saveEntity(character);

        } catch (Exception e) {
            characters.remove(character);
            ES.getInstance().getEnhancedLogger().severe(e.getMessage());
        }
    }

    public static void removeCharacter(Character character) {
        try {
            characters.remove(character);
            ES.getInstance().deleteEntity(character);

        } catch (Exception e) {
            characters.add(character);
            ES.getInstance().getEnhancedLogger().severe(e.getMessage());
        }
    }
}
