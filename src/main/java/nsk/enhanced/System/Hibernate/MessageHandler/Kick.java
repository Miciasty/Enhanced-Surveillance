package nsk.enhanced.System.Hibernate.MessageHandler;

import nsk.enhanced.System.Hibernate.Character;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * The {@link Kick} entity represents a kick event in the surveillance system.
 * It stores information about the player (character) involved, the message associated with the kick,
 * and the timestamp when the kick occurred.
 */
@Entity
@Table(name = "surveillance_kick")
public class Kick {

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

    /**
     * Default constructor for JPA.
     */
    public Kick() { }

    /**
     * Constructs a new {@link Kick} instance associated with the specified player and message.
     * The kick event is initialized with the current timestamp.
     *
     * @param player the player who was kicked
     * @param message the message that contains the kick details
     */
    public Kick(Player player, String message) {

        this.character  = Character.getCharacter(player);
        this.message    = Original.getMessage(message);
        this.timestamp  = LocalDateTime.now();

    }

    // --- --- --- --- --- --- Main --- --- --- --- --- --- //

    /**
     * Returns the ID of this {@link Kick} event.
     *
     * @return the ID of the kick event
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the {@link Character} associated with this {@link Kick}.
     *
     * @return the {@link Character} who was kicked
     */
    public Character getCharacter() {
        return character;
    }

    /**
     * Returns the {@link Original} message associated with this {@link Kick}.
     *
     * @return the {@link Original} message that contains the kick details
     */
    public Original getMessage() {
        return message;
    }

    /**
     * Returns the timestamp when this {@link Kick} event occurred.
     *
     * @return the timestamp of the kick event
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }


}