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
import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.paper.mechanic.nameplate.NameplateManagerImpl;

public class EntityAddEffectListener extends PacketAdapter {

    private final NameplateManagerImpl manager;

    public EntityAddEffectListener(NameplateManagerImpl manager) {
        super(CustomNameplatesPlugin.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EFFECT);
        this.manager = manager;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
//        PacketContainer packet = event.getPacket();
//        System.out.println("添加包");
//        if (packet.getEffectTypes().read(0) == PotionEffectType.INVISIBILITY) {
//            System.out.println("是隐身");
//            manager.onEntityAddEffect(event.getPlayer(), packet.getIntegers().read(0));
//        }
    }
}
