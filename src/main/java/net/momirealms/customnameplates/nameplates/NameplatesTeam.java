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

package net.momirealms.customnameplates.nameplates;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Optional;

public class NameplatesTeam {

    private final Player player;
    private Component prefix;
    private Component suffix;
    private String prefixText;
    private String suffixText;
    private ChatColor color;

    public Component getPrefix() {return this.prefix;}
    public Component getSuffix() {return this.suffix;}
    public ChatColor getColor() {return this.color;}
    public String getPrefixText() {return prefixText;}
    public String getSuffixText() {return suffixText;}

    public NameplatesTeam(Player player) {

        this.color = ChatColor.WHITE;
        this.player = player;

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = player.getName();

        if (!ConfigManager.Main.tab) {
            Team teamTemp = scoreboard.getTeam(teamName);
            if (teamTemp == null) {
                teamTemp = scoreboard.registerNewTeam(teamName);
            }
            teamTemp.addEntry(player.getName());
        }

        updateNameplates();
    }

    public void updateNameplates() {

        Optional<PlayerData> playerData = Optional.ofNullable(CustomNameplates.instance.getDataManager().getOrCreate(this.player.getUniqueId()));
        String nameplate;

        if (playerData.isPresent()) nameplate = playerData.get().getEquippedNameplate();
        else nameplate = "none";

        if (nameplate.equals("none")) {
            if (ConfigManager.Main.placeholderAPI) {
                this.prefix = MiniMessage.miniMessage().deserialize(CustomNameplates.instance.getPlaceholderManager().parsePlaceholders(this.player, ConfigManager.Nameplate.player_prefix));
                this.suffix = MiniMessage.miniMessage().deserialize(CustomNameplates.instance.getPlaceholderManager().parsePlaceholders(this.player, ConfigManager.Nameplate.player_suffix));
            }
            else {
                this.prefix = MiniMessage.miniMessage().deserialize(ConfigManager.Nameplate.player_prefix);
                this.suffix = MiniMessage.miniMessage().deserialize(ConfigManager.Nameplate.player_suffix);
            }
            if (ConfigManager.Main.placeholderAPI) {
                this.prefixText = CustomNameplates.instance.getPlaceholderManager().parsePlaceholders(this.player, ConfigManager.Nameplate.player_prefix);
                this.suffixText = CustomNameplates.instance.getPlaceholderManager().parsePlaceholders(this.player, ConfigManager.Nameplate.player_suffix);
            }
            else {
                this.prefixText = ConfigManager.Nameplate.player_prefix;
                this.suffixText = ConfigManager.Nameplate.player_suffix;
            }

            this.color = ChatColor.WHITE;
            return;
        }

        NameplateInstance nameplateInstance = CustomNameplates.instance.getResourceManager().getNameplateInstance(nameplate);

        if (nameplateInstance == null){
            this.prefix = Component.text("");
            this.suffix = Component.text("");
            this.prefixText = "";
            this.suffixText = "";
            this.color = ChatColor.WHITE;
            CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId()).equipNameplate("none");
            return;
        }

        String playerPrefix;
        String playerSuffix;

        if (ConfigManager.Main.placeholderAPI) {
            if (!ConfigManager.Nameplate.hidePrefix) playerPrefix = CustomNameplates.instance.getPlaceholderManager().parsePlaceholders(this.player, ConfigManager.Nameplate.player_prefix);
            else playerPrefix = "";
            if (!ConfigManager.Nameplate.hideSuffix) playerSuffix = CustomNameplates.instance.getPlaceholderManager().parsePlaceholders(this.player, ConfigManager.Nameplate.player_suffix);
            else playerSuffix = "";
        }
        else {
            if (!ConfigManager.Nameplate.hidePrefix) playerPrefix = ConfigManager.Nameplate.player_prefix;
            else playerPrefix = "";
            if (!ConfigManager.Nameplate.hideSuffix) playerSuffix = ConfigManager.Nameplate.player_suffix;
            else playerSuffix = "";
        }

        String name = this.player.getName();

        this.prefixText = NameplateUtil.makeCustomNameplate(
                MiniMessage.miniMessage().stripTags(playerPrefix),
                name,
                MiniMessage.miniMessage().stripTags(playerSuffix),
                nameplateInstance
        ) + playerPrefix;

        this.suffixText = playerSuffix + NameplateUtil.getSuffixChar(
                MiniMessage.miniMessage().stripTags(playerPrefix) +
                        name +
                        MiniMessage.miniMessage().stripTags(playerSuffix)
        );

        this.prefix = Component.text(
                NameplateUtil.makeCustomNameplate(
                        MiniMessage.miniMessage().stripTags(playerPrefix),
                        name,
                        MiniMessage.miniMessage().stripTags(playerSuffix),
                        nameplateInstance
                )
        )
                .font(ConfigManager.Main.key)
                .append(MiniMessage.miniMessage().deserialize(playerPrefix));

        this.suffix = MiniMessage.miniMessage().deserialize(playerSuffix)
                .append(Component.text(
                        NameplateUtil.getSuffixChar(
                                MiniMessage.miniMessage().stripTags(playerPrefix) +
                                        name +
                                        MiniMessage.miniMessage().stripTags(playerSuffix))
                )
                .font(ConfigManager.Main.key));

        this.color = nameplateInstance.getConfig().getColor();
    }
}