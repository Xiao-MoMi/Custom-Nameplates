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

package net.momirealms.customnameplates.paper.mechanic.actionbar.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.paper.mechanic.actionbar.ActionBarManagerImpl;

public class ActionBarListener extends PacketAdapter {

    private final ActionBarManagerImpl actionBarManager;

    public ActionBarListener(ActionBarManagerImpl actionBarManager) {
        super(CustomNameplatesPlugin.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        this.actionBarManager = actionBarManager;
    }

    public void onPacketSending(PacketEvent event) {
        actionBarManager.onReceiveActionBarPacket(event);
    }
}
