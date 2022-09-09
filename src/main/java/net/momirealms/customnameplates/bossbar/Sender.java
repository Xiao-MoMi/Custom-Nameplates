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

package net.momirealms.customnameplates.bossbar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.objects.TextCache;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.utils.Reflection;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;

public class Sender {

    private final Player player;
    private int timer;
    private final TextCache text;
    private final BukkitTask bukkitTask;
    private final UUID uuid;

    public Sender(Player player, BossBarConfig config){

        this.player = player;
        this.text = new TextCache(player, config.getText());
        this.timer = 0;
        this.uuid = UUID.randomUUID();

        show(config);

        this.bukkitTask = new BukkitRunnable() {
                @Override
                public void run() {
                if (timer < config.getRate()){
                    timer++;
                }
                else {
                    timer = 0;
                    if (text.update()) {
                        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BOSS);
                        packet.getModifier().write(0, uuid);
                        InternalStructure internalStructure = packet.getStructures().read(1);
                        internalStructure.getChatComponents().write(0, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(text.getLatestValue()))));
                        internalStructure.getFloat().write(0,1F);
                        internalStructure.getEnumModifier(BarColor.class, 2).write(0, config.getColor());
                        internalStructure.getEnumModifier(Overlay.class, 3).write(0, config.getOverlay());
                        internalStructure.getModifier().write(4, false);
                        internalStructure.getModifier().write(5, false);
                        internalStructure.getModifier().write(6, false);
                        try{
                            CustomNameplates.protocolManager.sendServerPacket(player, packet);
                        }catch (InvocationTargetException e){
                            AdventureUtil.consoleMessage("<red>[CustomNameplates] Failed to update bossbar for " + player.getName());
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(CustomNameplates.instance,1,1);

    }

    private void show(BossBarConfig config){

        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BOSS);

        packet.getModifier().write(0, uuid);

        InternalStructure internalStructure = packet.getStructures().read(1);
        internalStructure.getChatComponents().write(0, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(text.getLatestValue()))));
        internalStructure.getFloat().write(0,1F);
        internalStructure.getEnumModifier(BarColor.class, 2).write(0, config.getColor());
        internalStructure.getEnumModifier(Overlay.class, 3).write(0, config.getOverlay());
        internalStructure.getModifier().write(4, false);
        internalStructure.getModifier().write(5, false);
        internalStructure.getModifier().write(6, false);
        try{
            CustomNameplates.protocolManager.sendServerPacket(player, packet);
        }catch (InvocationTargetException e){
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Failed to display bossbar for " + player.getName());
        }
    }

    public void hide() {
        remove();
        bukkitTask.cancel();
    }

    private void remove() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BOSS);
        packet.getModifier().write(0, uuid);
        packet.getModifier().write(1, Reflection.removeBar);
        try{
            CustomNameplates.protocolManager.sendServerPacket(player, packet);
        }catch (InvocationTargetException e){
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Failed to remove bossbar for " + player.getName());
        }
    }
}
