package nsk.enhanced.System.Hibernate;

import org.bukkit.Location;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EventDetails {

    @Column(name = "event_value", columnDefinition = "TINYBLOB")
    private byte[] eventValue;

    @Column(nullable = false)
    private int x, y, z;

    @Column(nullable = false)
    private float pitch, yaw;

    public EventDetails() { }

    public EventDetails(byte[] eventValue, Location location) {
        this.eventValue = eventValue;
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }

    public byte[] getEventValue() { return eventValue; }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }

    public float getPitch() { return pitch; }
    public float getYaw() { return yaw; }

    public void setEventValue(byte[] eventValue) { this.eventValue = eventValue; }
    public void setLocation(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }

}
