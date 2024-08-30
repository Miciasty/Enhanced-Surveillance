package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit.Extended;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link ExtMove} class extends the {@link PlayerMoveEvent} to include additional information
 * such as the timestamp of when the move event was created. This helps in tracking
 * player movements over time.
 */
public class ExtMove extends PlayerMoveEvent {

    private final long timestamp = System.currentTimeMillis();

    /**
     * Constructs a new {@link ExtMove} event with the specified player and locations.
     *
     * @param player the player involved in the movement
     * @param from the location the player moved from
     * @param to the location the player moved to
     */
    public ExtMove(@NotNull Player player, @NotNull Location from, @Nullable Location to) {
        super(player, from, to);
    }

    /**
     * Returns the timestamp of when this move event was created.
     *
     * @return the timestamp in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }

}
