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

package net.momirealms.customnameplates.api.mechanic.bubble.listener;

import net.momirealms.customnameplates.api.manager.BubbleManager;
import org.bukkit.event.Listener;

public abstract class AbstractChatListener implements Listener {

    protected BubbleManager chatBubblesManager;

    public AbstractChatListener(BubbleManager chatBubblesManager) {
        this.chatBubblesManager = chatBubblesManager;
    }

    public abstract void register();

    public abstract void unregister();
}
