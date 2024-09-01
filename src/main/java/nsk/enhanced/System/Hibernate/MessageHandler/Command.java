package nsk.enhanced.System.Hibernate.MessageHandler;

import nsk.enhanced.System.Hibernate.Character;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * The {@link Command} class represents a command issued by a player on the server,
 * stored in the `surveillance_commands` database table.
 * This class links a command to the player's {@link Character} who issued it and the original message {@link Original}
 * that contains the command details. This class also stores the timestamp when the command was issued.
 */
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

    /**
     * Default constructor for JPA.
     */
    public Command() { }

    /**
     * Constructs a new {@link Command} instance associated with the specified player and message.
     * The command is initialized with the current timestamp.
     *
     * @param player the player who issued the command
     * @param message the message that contains the command details
     */
    public Command(Player player, String message) {

        this.character  = Character.getCharacter(player);
        this.message    = Original.getMessage(message);
        this.timestamp  = LocalDateTime.now();

    }

    // --- --- --- --- --- --- Main --- --- --- --- --- --- //

    /**
     * Returns the ID of this Command.
     *
     * @return the ID of the command
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the {@link Character} associated with this Command.
     *
     * @return the {@link Character} who issued the command
     */
    public Character getCharacter() {
        return character;
    }

    /**
     * Returns the {@link Original} message associated with this Command.
     *
     * @return the {@link Original} message that contains the command details
     */
    public Original getMessage() {
        return message;
    }

    /**
     * Returns the timestamp when this Command was issued.
     *
     * @return the timestamp of the command
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }


}
