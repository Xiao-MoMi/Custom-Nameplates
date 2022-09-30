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

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class Sender {

    private final Player player;
    private int timer_1;
    private int timer_2;
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

    public Sender(Player player, BossBarConfig config){
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
    }

    public void show() {
        this.isShown = true;

        try{
            CustomNameplates.protocolManager.sendServerPacket(player, getPacket());
        }catch (InvocationTargetException e){
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Failed to display bossbar for " + player.getName());
        }

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
                if (timer_1 < config.getRate()){
                    timer_1++;
                }
                else {
                    timer_1 = 0;
                    if (text.update() || force) {
                        force = false;
                        try{
                            CustomNameplates.protocolManager.sendServerPacket(player, getPacket());
                        }
                        catch (InvocationTargetException e){
                            AdventureUtil.consoleMessage("<red>[CustomNameplates] Failed to update bossbar for " + player.getName());
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(CustomNameplates.instance,1,1);
    }

    private PacketContainer getPacket() {
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
        return packet;
    }

    public void hide() {
        remove();
        if (bukkitTask != null) bukkitTask.cancel();
        this.isShown = false;
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

    public boolean getStatus() {
        return this.isShown;
    }

    public BossBarConfig getConfig() {
        return config;
    }
}
