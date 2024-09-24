package net.momirealms.customnameplates.api.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PacketEvent {

    private boolean cancelled;
    private List<Runnable> delayedTasks = null;
    private final Object packet;

    public PacketEvent(Object packet) {
        this.packet = packet;
    }

    public Object getPacket() {
        return packet;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void addDelayedTask(Runnable task) {
        if (delayedTasks == null) {
            delayedTasks = new ArrayList<>();
        }
        delayedTasks.add(task);
    }

    public List<Runnable> getDelayedTasks() {
        return Optional.ofNullable(delayedTasks).orElse(Collections.emptyList());
    }
}
