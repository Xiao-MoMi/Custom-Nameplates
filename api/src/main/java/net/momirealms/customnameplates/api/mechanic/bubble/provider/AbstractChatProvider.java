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

package net.momirealms.customnameplates.api.mechanic.bubble.provider;

import net.momirealms.customnameplates.api.manager.BubbleManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class AbstractChatProvider implements Listener {

    protected BubbleManager chatBubblesManager;

    public AbstractChatProvider(BubbleManager chatBubblesManager) {
        this.chatBubblesManager = chatBubblesManager;
    }

    public abstract void register();

    public abstract void unregister();

    public abstract boolean hasJoinedChannel(Player player, String channelID);

    public abstract boolean canJoinChannel(Player player, String channelID);

    public abstract boolean isIgnoring(Player sender, Player receiver);
}
