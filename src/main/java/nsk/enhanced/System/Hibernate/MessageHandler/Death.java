package nsk.enhanced.System.Hibernate.MessageHandler;

import nsk.enhanced.System.Hibernate.Character;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * The {@link Death} entity represents a death event in the surveillance system.
 * It stores information about the player (character) involved, the message associated with the death,
 * and the timestamp when the death occurred.
 */
@Entity
@Table(name = "surveillance_death")
public class Death {

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
    public Death() { }

    /**
     * Constructs a new {@link Death} instance associated with the specified player and message.
     * The death is initialized with the current timestamp.
     *
     * @param player the player involved in the death
     * @param message the message that contains the death details
     */
    public Death(Player player, String message) {

        this.character  = Character.getCharacter(player);
        this.message    = Original.getMessage(message);
        this.timestamp  = LocalDateTime.now();

    }

    // --- --- --- --- --- --- Main --- --- --- --- --- --- //

    /**
     * Returns the ID of this {@link Death} event.
     *
     * @return the ID of the death event
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the {@link Character} associated with this {@link Death}.
     *
     * @return the {@link Character} involved in the death
     */
    public Character getCharacter() {
        return character;
    }

    /**
     * Returns the {@link Original} message associated with this {@link Death}.
     *
     * @return the {@link Original} message that contains the death details
     */
    public Original getMessage() {
        return message;
    }

    /**
     * Returns the timestamp when this {@link Death} event occurred.
     *
     * @return the timestamp of the death event
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }


}