package nsk.enhanced.System.Hibernate.MessageHandler;

import nsk.enhanced.System.Hibernate.Character;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * The {@link Message} class represents a message sent by a player on the server,
 * stored in the `surveillance_messages` database table.
 * This class links a message to the player's {@link Character} who sent it and the original message {@link Original}
 * that contains the message details. It also stores the number of recipients and the timestamp
 * when the message was sent.
 */
@Entity
@Table(name = "surveillance_messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "character_id", nullable = false)
    private Character character;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Original message;

    @Column(name = "recipients_size", nullable = true)
    private int size;

    @Column(nullable = true)
    private LocalDateTime timestamp;

    /**
     * Default constructor for JPA.
     */
    public Message() { }

    /**
     * Constructs a new {@link Message} instance associated with the specified player, message, and recipients size.
     * The message is initialized with the current timestamp.
     *
     * @param player the player who sent the message
     * @param message the message content
     * @param recipients the number of recipients the message was sent to
     */
    public Message(Player player, String message, int recipients) {

        this.character  = Character.getCharacter(player);
        this.message    = Original.getMessage(message);
        this.size       = recipients;
        this.timestamp  = LocalDateTime.now();

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
     * Returns the {@link Character} associated with this Message.
     *
     * @return the {@link Character} who sent the message
     */
    public Character getCharacter() {
        return character;
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
     * Returns the timestamp when this Message was sent.
     *
     * @return the timestamp of the message
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
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
