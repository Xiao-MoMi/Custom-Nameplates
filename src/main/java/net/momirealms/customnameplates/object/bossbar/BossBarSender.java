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

package net.momirealms.customnameplates.object.bossbar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.DynamicText;
import net.momirealms.customnameplates.object.requirements.Requirement;
import net.momirealms.customnameplates.utils.AdventureUtils;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BossBarSender {

    private final Player player;
    private final int switch_interval;
    private int timer;
    private int current_text_id;
    private final DynamicText[] dynamicTexts;
    private final Requirement[] requirements;
    private final UUID uuid;
    private boolean force;
    private boolean isShown;
    private final Overlay overlay;
    private final BarColor barColor;

    public BossBarSender(int switch_interval, String[] texts, Requirement[] requirements, Overlay overlay, BarColor barColor,Player player) {
        this.player = player;
        this.switch_interval = switch_interval;
        this.overlay = overlay;
        this.barColor = barColor;
        this.uuid = UUID.randomUUID();
        this.requirements = requirements;
        this.dynamicTexts = new DynamicText[texts.length];
        for (int i = 0; i < texts.length; i++) {
            dynamicTexts[i] = new DynamicText(player, texts[i]);
        }
        this.current_text_id = 0;
    }

    public boolean isShown() {
        return isShown;
    }

    public boolean canSend() {
        if (requirements.length == 0) return true;
        for (Requirement requirement : requirements) {
            if (!requirement.isConditionMet(player)) {
                return false;
            }
        }
        return true;
    }

    public void hide() {
        this.sendRemovePacket();
        this.isShown = false;
    }

    public void show() {
        CustomNameplates.getProtocolManager().sendServerPacket(player, getCreatePacket());
        this.isShown = true;
    }

    public void update() {
        timer++;
        if (timer > switch_interval) {
            timer = 0;
            current_text_id++;
            if (current_text_id >= dynamicTexts.length) {
                current_text_id = 0;
            }
            force = true;
        }
        if (dynamicTexts[current_text_id].update() || force) {
            force = false;
            CustomNameplates.getProtocolManager().sendServerPacket(player, getUpdatePacket());
        }
    }

    private PacketContainer getCreatePacket() {
        dynamicTexts[current_text_id].update();
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BOSS);
        packet.getModifier().write(0, uuid);
        InternalStructure internalStructure = packet.getStructures().read(1);
        internalStructure.getChatComponents().write(0, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(AdventureUtils.replaceLegacy(dynamicTexts[current_text_id].getLatestValue())))));
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
            Method sMethod = MinecraftReflection.getChatSerializerClass().getMethod("a", String.class);
            sMethod.setAccessible(true);
            Object chatComponent = sMethod.invoke(null, GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(AdventureUtils.replaceLegacy(dynamicTexts[current_text_id].getLatestValue()))));
            Class<?> packetBossClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutBoss$e");
            Constructor<?> packetConstructor = packetBossClass.getDeclaredConstructor(MinecraftReflection.getIChatBaseComponentClass());
            packetConstructor.setAccessible(true);
            Object updatePacket = packetConstructor.newInstance(chatComponent);
            packet.getModifier().write(1, updatePacket);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        return packet;
    }

    private void sendRemovePacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BOSS);
        packet.getModifier().write(0, uuid);
        try {
            Class<?> bar = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutBoss");
            Field remove = bar.getDeclaredField("f");
            remove.setAccessible(true);
            packet.getModifier().write(1, remove.get(null));
            CustomNameplates.getProtocolManager().sendServerPacket(player, packet);
        } catch (ClassNotFoundException e){
            AdventureUtils.consoleMessage("<red>[CustomNameplates] Failed to remove boss bar for " + player.getName());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
