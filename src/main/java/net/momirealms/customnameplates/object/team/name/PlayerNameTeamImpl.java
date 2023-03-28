package net.momirealms.customnameplates.object.team.name;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.TeamManager;
import net.momirealms.customnameplates.object.team.TeamNameInterface;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.Optional;

public class PlayerNameTeamImpl implements TeamNameInterface {

    private final TeamManager teamManager;

    public PlayerNameTeamImpl(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public String getTeamName(Player player) {
        return player.getName();
    }

    @Override
    public void onJoin(Player player) {
        if (teamManager.isFakeTeam()) {
            createFakeTeamToAll(player);
        }
        else {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team team = scoreboard.getTeam(player.getName());
            if (team == null) {
                team = scoreboard.registerNewTeam(player.getName());
            }
            team.addEntry(player.getName());
        }
    }

    @Override
    public void onQuit(Player player) {
        if (teamManager.isFakeTeam()) {
            destroyTeamToAll(player);
        }
        else {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team team = scoreboard.getTeam(player.getName());
            if (team != null) team.unregister();
        }
    }

    @Override
    public void unload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            destroyTeamToAll(player);
        }
    }

    @Override
    public void load() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            createFakeTeamToAll(player);
        }
    }

    // Send fake team packets
    public void createFakeTeamToAll(Player joinPlayer) {
        PacketContainer packetToAll = getPlayerTeamCreatePacket(joinPlayer);
        for (Player all : Bukkit.getOnlinePlayers()) {
            CustomNameplates.getProtocolManager().sendServerPacket(joinPlayer, getPlayerTeamCreatePacket(all));
            if (joinPlayer != all) CustomNameplates.getProtocolManager().sendServerPacket(all, packetToAll);
        }
    }

    // Get team create packet
    private PacketContainer getPlayerTeamCreatePacket(Player joinPlayer) {
        PacketContainer packetToAll = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        packetToAll.getIntegers().write(0,0);
        packetToAll.getStrings().write(0, joinPlayer.getName());
        packetToAll.getModifier().write(2, Collections.singletonList(joinPlayer.getName()));
        Optional<InternalStructure> optionalInternalStructure = packetToAll.getOptionalStructures().read(0);
        if (optionalInternalStructure.isPresent()) {
            InternalStructure internalStructure = optionalInternalStructure.get();
            internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,ChatColor.WHITE);
        }
        return packetToAll;
    }

    // Get team destroy packet
    public void destroyTeamToAll(Player quitPlayer) {
        PacketContainer packetToAll = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        packetToAll.getIntegers().write(0,1);
        packetToAll.getStrings().write(0, quitPlayer.getName());
        for (Player all : Bukkit.getOnlinePlayers()) {
            CustomNameplates.getProtocolManager().sendServerPacket(all, packetToAll);
        }
    }
}
