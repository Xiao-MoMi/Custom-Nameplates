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

package net.momirealms.customnameplates.paper.mechanic.nameplate.tag.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.paper.mechanic.nameplate.tag.team.TeamTagManagerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerInfoListener extends PacketAdapter {

    private final TeamTagManagerImpl manager;

    public PlayerInfoListener(TeamTagManagerImpl manager) {
        super(CustomNameplatesPlugin.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Server.PLAYER_INFO);
        this.manager = manager;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Set<EnumWrappers.PlayerInfoAction> actions = packet.getPlayerInfoActions().read(0);
        if (!actions.contains(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME))
            return;
        List<?> list = (List<?>) packet.getModifier().read(1);
        List<Object> newList = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Object dataHandle = list.get(i);
            if (dataHandle == null) {
                continue;
            }
            PlayerInfoData data = PlayerInfoData.getConverter().getSpecific(dataHandle);
            WrappedGameProfile profile = data.getProfile();
            PlayerInfoData newData = new PlayerInfoData(
                    profile,
                    data.getLatency(),
                    data.getGameMode(),
                    WrappedChatComponent.fromJson(String.format("{\"text\":\"%s\"}", profile.getName()))
            );
            newList.add(PlayerInfoData.getConverter().getGeneric(newData));
        }
        packet.getModifier().write(1, newList);
    }
}
