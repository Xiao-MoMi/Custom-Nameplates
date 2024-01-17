package net.momirealms.customnameplates.api.event;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomNameplatesReloadEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final CustomNameplatesPlugin plugin;

    public CustomNameplatesReloadEvent(CustomNameplatesPlugin plugin) {
        this.plugin = plugin;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public CustomNameplatesPlugin getPlugin() {
        return plugin;
    }
}
