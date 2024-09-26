package net.momirealms.customnameplates.common.event;

import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;
import org.jetbrains.annotations.NotNull;

public interface NameplatesEvent {

    /**
     * Get the plugin instance this event was dispatched from
     */
    @NotNull
    NameplatesPlugin plugin();

    /**
     * Gets the type of the event.
     *
     * @return the type of the event
     */
    @NotNull
    Class<? extends NameplatesEvent> eventType();
}
