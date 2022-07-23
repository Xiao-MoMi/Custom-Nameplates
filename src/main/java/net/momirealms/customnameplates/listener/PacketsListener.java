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
        //create team && update team info
        if (n != 0 && n != 2) {
            return;
        }
        Optional<InternalStructure> optional = event.getPacket().getOptionalStructures().read(0);
        if (optional.isEmpty()) {
            return;
        }
        InternalStructure internalStructure = optional.get();
        String teamName = event.getPacket().getStrings().read(0);
        NameplatesTeam team = this.plugin.getScoreBoardManager().getTeam(teamName);
        if (!this.plugin.getScoreBoardManager().doesTeamExist(teamName)){
            return;
        }
        if (ConfigManager.MainConfig.show_after && DataManager.cache.get(event.getPlayer().getUniqueId()).getAccepted() == 0) {
            internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson("{\"text\":\"\"}"));
            internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson("{\"text\":\"\"}"));
            internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,ChatColor.WHITE);
            return;
        }
        //在新建队伍名字的时候其实就是以玩家名命名,所以获得的teamName=playerName
        if (team.getPrefix() != null){
            internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(team.getPrefix())));
        }
        if (team.getSuffix() != null){
            internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(team.getSuffix())));
        }
        internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,team.getColor());
    }
}
