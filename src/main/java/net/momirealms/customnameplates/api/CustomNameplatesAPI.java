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

package net.momirealms.customnameplates.api;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.nameplate.NameplatesTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CustomNameplatesAPI {

    private static CustomNameplatesAPI api;
    private final CustomNameplates plugin;

    public CustomNameplatesAPI(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    public void init() {
        api = this;
    }

    public static CustomNameplatesAPI getInstance() {
        return api;
    }

    @Deprecated
    public static CustomNameplatesAPI getAPI() {
        return api;
    }

    public boolean doesNameplateExist(String nameplate) {
        return plugin.getNameplateManager().existNameplate(nameplate);
    }

    public boolean doesBubbleExist(String bubble) {
        return plugin.getChatBubblesManager().existBubble(bubble);
    }

    public void equipNameplate(Player player, String nameplate) {
        plugin.getDataManager().equipNameplate(player, nameplate);
        updateAndSave(player);
    }

    private void updateAndSave(Player player) {
        updateNameplateTeam(player);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getDataManager().saveData(player);
        });
    }

    public void equipBubble(Player player, String bubble) {
        plugin.getDataManager().equipBubble(player, bubble);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getDataManager().saveData(player);
        });
    }

    public void unEquipNameplate(Player player) {
        plugin.getDataManager().equipNameplate(player, "none");
        updateAndSave(player);
    }

    public void unEquipBubble(Player player) {
        plugin.getDataManager().equipBubble(player, "none");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getDataManager().saveData(player);
        });
    }

    public void updateNameplateTeam(Player player) {
        NameplatesTeam nameplatesTeam = plugin.getTeamManager().getNameplateTeam(player.getUniqueId());
        if (nameplatesTeam != null) {
            nameplatesTeam.update(true);
            plugin.getTeamManager().sendUpdateToAll(player, true);
        }
    }
}
