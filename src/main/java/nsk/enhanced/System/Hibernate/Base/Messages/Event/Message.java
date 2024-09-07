package nsk.enhanced.System.Hibernate.Base.Messages.Event;

import nsk.enhanced.System.Hibernate.Base.Messages.Original;
import nsk.enhanced.System.Hibernate.Character;
import nsk.enhanced.System.Hibernate.Event;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <p>
 * The {@link Message} class represents a message sent by a player on the server,
 * stored in the `surveillance_messages` database table. It associates the message
 * with a specific {@link Event}, the original content of the message stored in {@link Original},
 * and keeps track of how many recipients received the message.
 * </p>
 *
 * <p>
 * Each {@link Message} is linked to an {@link Event}, which provides context on when and how the message was sent,
 * and an {@link Original} that holds the actual message content.
 * </p>
 */
@Entity
@Table(name = "surveillance_messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Original message;

    @Column(name = "recipients_size", nullable = true)
    private int size;

    /**
     * Default constructor for JPA.
     */
    public Message() { }

    /**
     * Constructs a new {@link Message} instance associated with the specified event, original message,
     * and the number of recipients. The message content is linked to the {@link Original} class,
     * and the event that triggered this message is stored in the {@link Event} class.
     *
     * @param event The {@link Event} associated with this message
     * @param message The message content to store in the {@link Original}
     * @param recipients The number of recipients the message was sent to
     */
    public Message(Event event, String message, int recipients) {

        this.event      = event;
        this.message    = Original.getMessage(message);
        this.size       = recipients;

    }

    // --- --- --- --- --- --- Main --- --- --- --- --- --- //

    /**
     * Returns the ID of this Message.
     *
     * @return the ID of the message
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the {@link Event} associated with this {@link Message}.
     *
     * @return the {@link Event} involved in the message
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Returns the {@link Original} message associated with this Message.
     *
     * @return the {@link Original} message that contains the message details
     */
    public Original getMessage() {
        return message;
    }

    /**
     * Returns the number of recipients of this Message.
     *
     * @return the number of recipients the message was sent to
     */
    public int getSize() {
        return size;
    }


}
