package net.momirealms.customnameplates.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class BubblesEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled;
    private String bubble;
    private String text;
    private static final HandlerList handlerList = new HandlerList();

    public BubblesEvent(@NotNull Player who, String bubble, String text) {
        super(who);
        this.cancelled = false;
        this.bubble = bubble;
        this.text = text;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public String getBubble() {
        return bubble;
    }

    public void setBubble(String bubble) {
        this.bubble = bubble;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
