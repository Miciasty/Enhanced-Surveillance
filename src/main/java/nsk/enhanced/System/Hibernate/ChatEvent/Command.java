package nsk.enhanced.System.Hibernate.ChatEvent;

import nsk.enhanced.System.Hibernate.Character;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "surveillance_commands")
public class Command {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "character_id", nullable = false)
    private Character character;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Original message;

    @Column(nullable = true)
    private LocalDateTime timestamp;

    public Command() { }

    public Command(Player player, String message) {

        this.character  = Character.getCharacter(player);
        this.message    = Original.getMessage(message);
        this.timestamp  = LocalDateTime.now();

    }

    // --- --- --- --- --- --- Main --- --- --- --- --- --- //

    public int getId() {
        return id;
    }

    public Character getCharacter() {
        return character;
    }

    public Original getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //



}
