package nsk.enhanced.System.Hibernate;

import nsk.enhanced.System.Hibernate.Base.Minecraft.Coordinates;
import nsk.enhanced.System.Hibernate.Base.Minecraft.Event.CreatureInteraction;
import org.bukkit.Location;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * <p>
 * The {@link EventDetails} class is an embeddable entity used to store detailed information
 * about an {@link Event}, including the event value (as a byte array) and
 * the location (coordinates and orientation) where the event occurred.
 * </p>
 *
 * <p>
 * This class is designed to be embedded in other entities, allowing for the reuse of
 * event-related data structures within different contexts.
 * </p>
 */
@Embeddable
public class EventDetails {

    /**
     * The event value stored as a byte array. This field is stored in the database
     * as a TINYBLOB, which can hold up to 255 bytes of data.
     */
    @Column(name = "event_value", columnDefinition = "TINYBLOB")
    private byte[] eventValue;

    @ManyToOne
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(nullable = false)
    private float pitch, yaw;

    /**
     * Default constructor for JPA.
     */
    public EventDetails() { }

    /**
     * Constructs a new {@link EventDetails} with the specified event's value and location.
     *
     * @param eventValue the byte array representing the event's value
     * @param location the {@link Location} object containing the coordinates and orientation of the event
     */
    public EventDetails(byte[] eventValue, Location location) {
        this.eventValue = eventValue;
        this.coordinates    = Coordinates.getCoordinatesByLocation(location);
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }

    /**
     * Returns the byte array representing the event's value.
     *
     * @return the event value as a byte array
     */
    public byte[] getEventValue() { return eventValue; }

    /**
     * Returns the {@link Coordinates} of the event location.
     *
     * @return the {@link Coordinates} of the location
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Returns the pitch (vertical rotation) of the event location.
     *
     * @return the pitch value
     */
    public float getPitch() { return pitch; }

    /**
     * Returns the yaw (horizontal rotation) of the event location.
     *
     * @return the yaw value
     */
    public float getYaw() { return yaw; }

    /**
     * Sets the byte array representing the event's value.
     *
     * @param eventValue the new event value as a byte array
     */
    public void setEventValue(byte[] eventValue) { this.eventValue = eventValue; }

}
