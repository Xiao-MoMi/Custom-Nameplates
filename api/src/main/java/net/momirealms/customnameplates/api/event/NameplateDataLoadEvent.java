/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
