package nsk.enhanced.System.Hibernate;

import org.bukkit.Location;

import javax.persistence.Column;
import javax.persistence.Embeddable;

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

    @Column(nullable = false)
    private int x, y, z;

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
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
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
     * Returns the X coordinate of the event location.
     *
     * @return the X coordinate
     */
    public int getX() { return x; }

    /**
     * Returns the Y coordinate of the event location.
     *
     * @return the Y coordinate
     */
    public int getY() { return y; }

    /**
     * Returns the Z coordinate of the event location.
     *
     * @return the Z coordinate
     */
    public int getZ() { return z; }

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

    /**
     * Sets the location coordinates and orientation (pitch and yaw) of the event.
     *
     * @param location the new {@link Location} object containing the coordinates and orientation
     */
    public void setLocation(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }

}
