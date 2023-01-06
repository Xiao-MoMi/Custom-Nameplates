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

package net.momirealms.customnameplates.objects.bossbar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.objects.TextCache;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BossBarSender {

    private final Player player;
    private int timer_1;
    private int timer_2;
    private int timer_3;
    private int counter;
    private final int size;
    private final TextCache[] texts;
    private TextCache text;
    private BukkitTask bukkitTask;
    private final UUID uuid;
    private boolean force;
    private final BossBarConfig config;
    private boolean isShown;

    public void setText(int position) {
        this.text = texts[position];
        this.force = true;
    }

    public BossBarSender(Player player, BossBarConfig config){
        String[] str = config.getText();
        this.size = str.length;
        texts = new TextCache[str.length];
        for (int i = 0; i < str.length; i++) {
            texts[i] = new TextCache(player, str[i]);
        }
        text = texts[0];
        this.player = player;
        this.uuid = UUID.randomUUID();
        this.config = config;
        this.isShown = false;
        this.timer_3 = config.getRate();
    }

    public boolean canConditionCheck() {
        timer_3++;
        if (timer_3 > config.getRate()) {
            timer_3 = 0;
            return true;
        }
        return false;
    }

    public void show() {
        this.isShown = true;

        CustomNameplates.protocolManager.sendServerPacket(player, getPacket());

        this.bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (size != 1) {
                    timer_2++;
                    if (timer_2 > config.getInterval()) {
                        timer_2 = 0;
                        counter++;
                        if (counter == size) {
                            counter = 0;
                        }
                        setText(counter);
                    }
                }
                if (timer_1 < config.getRate()) {
                    timer_1++;
                }
                else {
                    timer_1 = 0;
                    if (text.update() || force) {
                        force = false;
                        CustomNameplates.protocolManager.sendServerPacket(player, getUpdatePacket());
                    }
                }
            }
        }.runTaskTimerAsynchronously(CustomNameplates.plugin,0,1);
    }

    private PacketContainer getPacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BOSS);
        packet.getModifier().write(0, uuid);
        InternalStructure internalStructure = packet.getStructures().read(1);
        internalStructure.getChatComponents().write(0, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(AdventureUtil.replaceLegacy(text.getLatestValue())))));
        internalStructure.getFloat().write(0,1F);
        internalStructure.getEnumModifier(BarColor.class, 2).write(0, config.getColor());
        internalStructure.getEnumModifier(Overlay.class, 3).write(0, config.getOverlay());
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
            Object chatComponent = sMethod.invoke(null, GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(AdventureUtil.replaceLegacy(text.getLatestValue()))));
            Class<?> packetBossClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutBoss$e");
            Constructor<?> packetConstructor = packetBossClass.getDeclaredConstructor(MinecraftReflection.getIChatBaseComponentClass());
            packetConstructor.setAccessible(true);
            Object updatePacket = packetConstructor.newInstance(chatComponent);
            packet.getModifier().write(1, updatePacket);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
        return packet;
    }

    public void hide() {
        if (bukkitTask != null) bukkitTask.cancel();
        remove();
        this.isShown = false;
    }

    private void remove() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BOSS);
        packet.getModifier().write(0, uuid);
        try {
            Class<?> bar = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutBoss");
            Field remove = bar.getDeclaredField("f");
            remove.setAccessible(true);
            packet.getModifier().write(1, remove.get(null));
            CustomNameplates.protocolManager.sendServerPacket(player, packet);
        } catch (ClassNotFoundException e){
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Failed to remove bossbar for " + player.getName());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getStatus() {
        return this.isShown;
    }

    public BossBarConfig getConfig() {
        return config;
    }
}
