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

package net.momirealms.customnameplates.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.data.DataManager;
import net.momirealms.customnameplates.scoreboard.NameplatesTeam;
import org.bukkit.ChatColor;

import java.util.Optional;

public class PacketsListener extends PacketAdapter {

    private final CustomNameplates plugin;

    public PacketsListener(CustomNameplates plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.SCOREBOARD_TEAM);
        this.plugin = plugin;
    }

    public void onPacketSending(PacketEvent event) {
        Integer n = event.getPacket().getIntegers().read(0);
        if (n != 2) {
            return;
        }
        //if (n == 0) System.out.println("对玩家" + event.getPlayer().getName() + "发送team创建包");
        //if (n == 2) System.out.println("对玩家"+ event.getPlayer().getName() + "发送team更新包");
        Optional<InternalStructure> optional = event.getPacket().getOptionalStructures().read(0);
        if (optional.isEmpty()) {
            return;
        }
        InternalStructure internalStructure = optional.get();
        String teamName = event.getPacket().getStrings().read(0);
        //System.out.println("本次创建/更新的队伍名是" + teamName);
        NameplatesTeam team = this.plugin.getScoreBoardManager().getTeam(teamName);
        if (team == null) {
            //System.out.println("但是这个队伍不存在于缓存中哦，说明那个玩家还没上线");
            return;
        }
        //System.out.println("这个队伍确实存在于缓存中呢!");
        if (ConfigManager.MainConfig.show_after && (DataManager.cache.get(event.getPlayer().getUniqueId()) == null || DataManager.cache.get(event.getPlayer().getUniqueId()).getAccepted() == 0)) {
            //System.out.println("玩家" +event.getPlayer().getName() +"因为没有接受资源包所以没有被显示铭牌");
            internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson("{\"text\":\"\"}"));
            internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson("{\"text\":\"\"}"));
            internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,ChatColor.WHITE);
            return;
        }
        //System.out.println("玩家" +event.getPlayer().getName() +"可以看见队伍" + teamName + "的铭牌");
        if (team.getPrefix() != null) {
            internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(team.getPrefix())));
        }
        if (team.getSuffix() != null) {
            internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(team.getSuffix())));
        }
        internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,team.getColor());
    }
}
