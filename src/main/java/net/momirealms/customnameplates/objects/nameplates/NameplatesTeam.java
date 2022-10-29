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

package net.momirealms.customnameplates.objects.nameplates;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.ConfigManager;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.manager.TeamManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class NameplatesTeam {

    private final Player player;
    private Component prefix;
    private Component suffix;
    private String prefixText;
    private String suffixText;
    private ChatColor color;
    private String dynamic;

    public NameplatesTeam(Player player, TeamManager teamManager) {

        this.color = ChatColor.WHITE;
        this.player = player;

        updateNameplates();

        if (!ConfigManager.tab_hook && !ConfigManager.tab_BC_hook) {
            if (NameplateManager.fakeTeam) {
                teamManager.getTeamManagePacketUtil().createTeamToAll(player);
            }
            else {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                Team teamTemp = scoreboard.getTeam(player.getName());
                if (teamTemp == null) {
                    teamTemp = scoreboard.registerNewTeam(player.getName());
                }
                teamTemp.addEntry(player.getName());
            }
        }
    }

    public void updateNameplates() {

        String nameplate = CustomNameplates.plugin.getDataManager().getPlayerData(this.player).getEquippedNameplate();

        if (nameplate.equals("none")) {
            this.prefix = MiniMessage.miniMessage().deserialize(AdventureUtil.replaceLegacy(CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(this.player, NameplateManager.player_prefix)));
            this.suffix = MiniMessage.miniMessage().deserialize(AdventureUtil.replaceLegacy(CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(this.player, NameplateManager.player_suffix)));

            this.prefixText = CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(this.player, NameplateManager.player_prefix);
            this.suffixText = CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(this.player, NameplateManager.player_suffix);

            this.color = ChatColor.WHITE;
            return;
        }

        NameplateConfig nameplateConfig = CustomNameplates.plugin.getResourceManager().getNameplateConfig(nameplate);

        if (nameplateConfig == null){
            this.prefix = Component.text("");
            this.suffix = Component.text("");
            this.prefixText = "";
            this.suffixText = "";
            this.color = ChatColor.WHITE;
            CustomNameplates.plugin.getDataManager().getPlayerData(player).equipNameplate("none");
            return;
        }

        String playerPrefix;
        String playerSuffix;

        if (!NameplateManager.hidePrefix) playerPrefix = AdventureUtil.replaceLegacy(CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(this.player, NameplateManager.player_prefix));
        else playerPrefix = "";
        if (!NameplateManager.hideSuffix) playerSuffix = AdventureUtil.replaceLegacy(CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(this.player, NameplateManager.player_suffix));
        else playerSuffix = "";

        this.dynamic = playerPrefix + playerSuffix;

        String name;
        if (NameplateManager.player_name.equals("%player_name%")) {
            name = this.player.getName();
        }
        else {
            name = CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(player, NameplateManager.player_name);
        }

        this.prefixText = CustomNameplates.plugin.getNameplateManager().makeCustomNameplate(
                MiniMessage.miniMessage().stripTags(playerPrefix),
                name,
                MiniMessage.miniMessage().stripTags(playerSuffix),
                nameplateConfig
        );

        this.suffixText = CustomNameplates.plugin.getNameplateManager().getSuffixChar(
                MiniMessage.miniMessage().stripTags(playerPrefix) +
                        name +
                        MiniMessage.miniMessage().stripTags(playerSuffix)
        );

        this.prefix = Component.text(this.prefixText)
                .font(ConfigManager.key)
                .append(MiniMessage.miniMessage().deserialize(playerPrefix));

        this.suffix = MiniMessage.miniMessage().deserialize(playerSuffix)
                .append(Component.text(this.suffixText)
                .font(ConfigManager.key));

        this.color = nameplateConfig.color();
    }

    public Component getPrefix() {
        return this.prefix;
    }

    public Component getSuffix() {
        return this.suffix;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public String getPrefixText() {
        return prefixText;
    }

    public String getSuffixText() {
        return suffixText;
    }

    public String getDynamic() {
        return dynamic;
    }
}