package nsk.enhanced.EventHandlers.PlayerEvent.Bukkit.Extended;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExtMoveEvent extends PlayerMoveEvent {

    private final long timestamp = System.currentTimeMillis();

    public ExtMoveEvent(@NotNull Player player, @NotNull Location from, @Nullable Location to) {
        super(player, from, to);
    }

    public long getTimestamp() {
        return timestamp;
    }

}
