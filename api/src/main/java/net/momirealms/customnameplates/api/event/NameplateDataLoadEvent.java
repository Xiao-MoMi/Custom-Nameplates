package net.momirealms.customnameplates.api.event;

import net.momirealms.customnameplates.api.data.OnlineUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NameplateDataLoadEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final UUID uuid;
    private final OnlineUser onlineUser;

    public NameplateDataLoadEvent(UUID uuid, OnlineUser onlineUser) {
        super(true);
        this.uuid = uuid;
        this.onlineUser = onlineUser;
    }

    public UUID getUUID() {
        return uuid;
    }

    public OnlineUser getOnlineUser() {
        return onlineUser;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
