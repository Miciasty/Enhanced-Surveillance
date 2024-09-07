package nsk.enhanced.System.Hibernate.Base.Messages.Event;

import nsk.enhanced.System.Hibernate.Base.Messages.Original;
import nsk.enhanced.System.Hibernate.Character;
import nsk.enhanced.System.Hibernate.Event;
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
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Original message;

    /**
     * Default constructor for JPA.
     */
    public Kick() { }

    /**
     * Constructs a new {@link Kick} instance associated with the specified player and message.
     * The kick event is initialized with the current timestamp.
     *
     * @param event the {@link Event} associated with the kick
     * @param message the message that contains the kick details
     */
    public Kick(Event event, String message) {
        this.event      = event;
        this.message    = Original.getMessage(message);
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
     * Returns the {@link Event} associated with this {@link Kick}.
     *
     * @return the {@link Event} involved in the kick
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Returns the {@link Original} message associated with this {@link Kick}.
     *
     * @return the {@link Original} message that contains the kick details
     */
    public Original getMessage() {
        return message;
    }



}