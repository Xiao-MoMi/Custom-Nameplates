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

package net.momirealms.customnameplates.paper.mechanic.bossbar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.misc.PacketManager;
import net.momirealms.customnameplates.paper.util.ReflectionUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class BossBar {

    private final Overlay overlay;
    private final BarColor barColor;
    private final UUID uuid;
    private final Player owner;
    private String latestMiniMessage;
    private boolean visible;

    public BossBar(Player owner, Overlay overlay, BarColor barColor) {
        this.owner = owner;
        this.overlay = overlay;
        this.barColor = barColor;
        this.uuid = UUID.randomUUID();
        this.visible = false;
    }

    public void show() {
        PacketManager.getInstance().send(owner, getCreatePacket());
        this.visible = true;
    }

    public void hide() {
        PacketManager.getInstance().send(owner, getRemovePacket());
        this.visible = false;
    }

    public void update() {
        PacketManager.getInstance().send(owner, getUpdatePacket());
    }

    public boolean isVisible() {
        return visible;
    }

    public void setMiniMessageText(String text) {
        latestMiniMessage = text;
    }

    private PacketContainer getCreatePacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BOSS);
        packet.getModifier().write(0, uuid);
        InternalStructure internalStructure = packet.getStructures().read(1);
        internalStructure.getChatComponents().write(0, AdventureManagerImpl.getInstance().getWrappedChatComponentFromMiniMessage(latestMiniMessage));
        internalStructure.getFloat().write(0,1F);
        internalStructure.getEnumModifier(BarColor.class, 2).write(0, barColor);
        internalStructure.getEnumModifier(Overlay.class, 3).write(0, overlay);
        internalStructure.getModifier().write(4, false);
        internalStructure.getModifier().write(5, false);
        internalStructure.getModifier().write(6, false);
        return packet;
    }

    private PacketContainer getUpdatePacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BOSS);
        packet.getModifier().write(0, uuid);
        try {
            Object chatComponent = AdventureManagerImpl.getInstance().getIChatComponentFromMiniMessage(latestMiniMessage);
            Object updatePacket = ReflectionUtils.getUpdateConstructor().newInstance(chatComponent);
            packet.getModifier().write(1, updatePacket);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        return packet;
    }

    private PacketContainer getRemovePacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BOSS);
        packet.getModifier().write(0, uuid);
        packet.getModifier().write(1, ReflectionUtils.getRemoveBossBarPacket());
        return packet;
    }
}
