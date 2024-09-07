package nsk.enhanced.System.Hibernate.Base.Messages.Event;

import nsk.enhanced.System.Hibernate.Base.Messages.Original;
import nsk.enhanced.System.Hibernate.Event;

import javax.persistence.*;

/**
 * <p>
 * The {@link Death} entity represents a death event in the surveillance system.
 * It stores information about the event that caused the death, the related {@link Event},
 * and additional metadata, such as an associated message detailing the death.
 * </p>
 *
 * <p>
 * This entity is stored in the database in the `surveillance_death` table and includes references to other entities:
 * <ul>
 *     <li>{@link Event} - The specific event in which the death occurred.</li>
 *     <li>{@link Original} - The message associated with the death event, providing additional details.</li>
 * </ul>
 * </p>
 *
 * <p>
 * The {@link Death} entity is used to track deaths in the game and is linked to both
 * the {@link Event} that triggered the death and any additional messages recorded at the time of the death.
 * </p>
 */
@Entity
@Table(name = "surveillance_death")
public class Death {

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
    public Death() { }

    /**
     * Constructs a new {@link Death} instance associated with the specified player and message.
     * The death is initialized with the current timestamp.
     *
     * @param event the {@link Event} associated with the death
     * @param message the {@link Original} that contains the death details
     */
    public Death(Event event, String message) {
        this.event      = event;
        this.message    = Original.getMessage(message);
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
     * Returns the {@link Event} associated with this {@link Death}.
     *
     * @return the {@link Event} involved in the death
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Returns the {@link Original} message associated with this {@link Death}.
     *
     * @return the {@link Original} message that contains the death details
     */
    public Original getMessage() {
        return message;
    }

}