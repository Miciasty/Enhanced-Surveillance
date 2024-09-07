package nsk.enhanced.System.Hibernate.Base.Messages.Event;

import nsk.enhanced.System.Hibernate.Base.Messages.Original;
import nsk.enhanced.System.Hibernate.Character;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * The {@link Command} class represents a command issued by a player on the server,
 * stored in the `surveillance_commands` database table. This class links a command to
 * an associated {@link Event}, the player's {@link Character}, and the original message
 * {@link Original} that contains the command details. The class also stores the timestamp
 * when the command was issued.
 *
 * <p>
 * Each {@link Command} instance records a player's command input and ties it to the context
 * of an {@link Event}, which allows for tracking in-game player actions.
 * </p>
 */
@Entity
@Table(name = "surveillance_commands")
public class Command {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Original message;

    /**
     * Default constructor for JPA.
     */
    public Command() { }

    /**
     * Constructs a new {@link Command} instance associated with the specified event and message.
     * The command is linked to a player's action in-game, represented by the {@link Event},
     * and the actual command input stored as an {@link Original} message.
     *
     * @param event The {@link Event} associated with this command
     * @param message The message that contains the command details
     */
    public Command(Event event, String message) {
        this.event      = event;
        this.message    = Original.getMessage(message);
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
     * Returns the {@link Event} associated with this {@link Command}.
     *
     * @return the {@link Event} involved in the command
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Returns the {@link Original} message associated with this Command.
     *
     * @return the {@link Original} message that contains the command details
     */
    public Original getMessage() {
        return message;
    }


}
