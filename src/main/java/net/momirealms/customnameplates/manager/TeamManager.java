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

package net.momirealms.customnameplates.manager;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.listener.JoinQuitListener;
import net.momirealms.customnameplates.object.DisplayMode;
import net.momirealms.customnameplates.object.Function;
import net.momirealms.customnameplates.object.nameplate.NameplatesTeam;
import net.momirealms.customnameplates.object.team.TeamNameInterface;
import net.momirealms.customnameplates.object.team.TeamPacketInterface;
import net.momirealms.customnameplates.object.team.name.PlayerNameTeamImpl;
import net.momirealms.customnameplates.object.team.name.TABBungeeCordImpl;
import net.momirealms.customnameplates.object.team.name.TABImpl;
import net.momirealms.customnameplates.object.team.packet.TeamInfoImpl;
import net.momirealms.customnameplates.object.team.packet.TeamVisibilityImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamManager extends Function {

    private final ConcurrentHashMap<UUID, NameplatesTeam> teams ;
    private TeamPacketInterface teamPacketInterface;
    private TeamNameInterface teamNameInterface;
    private final JoinQuitListener joinQuitListener;
    private final CustomNameplates plugin;
    private final ConcurrentHashMap<UUID, Integer> triedTimes;

    public TeamManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.teams = new ConcurrentHashMap<>();
        this.joinQuitListener = new JoinQuitListener(this);
        this.triedTimes = new ConcurrentHashMap<>();
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(joinQuitListener, plugin);
        if (ConfigManager.tab_BC_hook) {
            teamNameInterface = new TABBungeeCordImpl();
        } else if (ConfigManager.tab_hook) {
            teamNameInterface = new TABImpl();
        } else {
            teamNameInterface = new PlayerNameTeamImpl(this);
        }
        teamNameInterface.load();
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(joinQuitListener);
        if (teamNameInterface != null) {
            teamNameInterface.unload();
        }
        teamPacketInterface = null;
    }

    @Override
    public void onQuit(Player player) {
        this.removePlayerFromTeamCache(player);
        teamNameInterface.onQuit(player);
        triedTimes.remove(player.getUniqueId());
    }

    public void setTeamPacketInterface() {
        if (plugin.getNameplateManager().getMode() == DisplayMode.TEAM) {
            teamPacketInterface = new TeamInfoImpl(this);
        } else if (plugin.getNameplateManager().getMode() == DisplayMode.ARMOR_STAND || plugin.getNameplateManager().getMode() == DisplayMode.TEXT_DISPLAY) {
            teamPacketInterface = new TeamVisibilityImpl(this);
        }
    }

    public void sendUpdateToOne(Player player) {
        if (teamPacketInterface != null) teamPacketInterface.sendUpdateToOne(player);
    }

    public void sendUpdateToAll(Player player, boolean force) {
        if (teamPacketInterface != null) teamPacketInterface.sendUpdateToAll(player, force);
    }

    public void createTeam(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline() || !checkTriedTimes(player.getUniqueId())) return;
        String teamName = getCurrentTeamName(player);
        if (teamName != null) {
            if (!teams.containsKey(uuid)) teams.put(uuid, new NameplatesTeam(plugin.getNameplateManager(), player, teamName));
            teamPacketInterface.sendUpdateToAll(player, true);
            teamPacketInterface.sendUpdateToOne(player);
        }
        // wait for bc
        else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(CustomNameplates.getInstance(), () -> createTeam(uuid),10);
        }
    }

    @Nullable
    public NameplatesTeam getNameplateTeam(UUID uuid) {
        return teams.get(uuid);
    }

    public String getCurrentTeamName(Player player) {
        return teamNameInterface.getTeamName(player);
    }

    public void removePlayerFromTeamCache(Player player) {
        teams.remove(player.getUniqueId());
    }

    public boolean isFakeTeam() {
        return plugin.getNameplateManager().isFakeTeam();
    }

    protected boolean checkTriedTimes(UUID uuid) {
        Integer previous = triedTimes.get(uuid);
        if (previous == null) {
            triedTimes.put(uuid, 1);
            return true;
        }
        else if (previous > 4) {
            triedTimes.remove(uuid);
            return false;
        }
        else {
            triedTimes.put(uuid, previous + 1);
            return true;
        }
    }

    public TeamNameInterface getTeamNameInterface() {
        return teamNameInterface;
    }
}
