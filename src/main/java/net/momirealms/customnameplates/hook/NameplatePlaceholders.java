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

package net.momirealms.customnameplates.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.objects.BackGround;
import net.momirealms.customnameplates.data.PlayerData;
import net.momirealms.customnameplates.nameplates.NameplateInstance;
import net.momirealms.customnameplates.font.FontUtil;
import net.momirealms.customnameplates.nameplates.NameplateUtil;
import net.momirealms.customnameplates.resource.ResourceManager;
import net.momirealms.customnameplates.nameplates.NameplatesTeam;
import net.momirealms.customnameplates.nameplates.TeamManager;
import net.momirealms.customnameplates.objects.BackGroundText;
import net.momirealms.customnameplates.objects.NameplateText;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class NameplatePlaceholders extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "nameplates";
    }

    @Override
    public @NotNull String getAuthor() {
        return "XiaoMoMi";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equals("equipped")){
            String nameplate = Optional.ofNullable(CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId())).orElse(PlayerData.EMPTY).getEquippedNameplate();
            if (!nameplate.equals("none")) return ResourceManager.NAMEPLATES.get(nameplate).getName();
            else return ConfigManager.Message.noNameplate;
        }
        if (params.equals("prefix")){
            NameplatesTeam nameplatesTeam = CustomNameplates.instance.getTeamManager().getTeams().get(TeamManager.getTeamName(player.getPlayer()));
            if (nameplatesTeam != null) return nameplatesTeam.getPrefixText();
            else return "";
        }
        if (params.equals("suffix")){
            NameplatesTeam nameplatesTeam = CustomNameplates.instance.getTeamManager().getTeams().get(TeamManager.getTeamName(player.getPlayer()));
            if (nameplatesTeam != null) return nameplatesTeam.getSuffixText();
            else return "";
        }
        if (params.startsWith("bg_")){
            String bg = params.substring(3);
            BackGroundText backGroundText = ConfigManager.papiBG.get(bg);
            if (backGroundText == null) return "";
            BackGround backGround = ConfigManager.backgrounds.get(backGroundText.getBackground());
            if (backGround == null) return "";
            String text = backGroundText.getText();
            if (ConfigManager.Main.placeholderAPI) text = PlaceholderAPI.setPlaceholders(player, text);
            return backGround.getBackGround(FontUtil.getTotalWidth(text));
        }
        if (params.startsWith("npp_")){
            String np = params.substring(4);
            NameplateText nameplateText = ConfigManager.papiNP.get(np);
            if (nameplateText == null) return "";
            NameplateInstance nameplateInstance = ResourceManager.NAMEPLATES.get(nameplateText.getNameplate());
            if (nameplateInstance == null) return "";
            String text = nameplateText.getText();
            if (ConfigManager.Main.placeholderAPI) text = PlaceholderAPI.setPlaceholders(player, text);
            return NameplateUtil.makeCustomNameplate("", text,"", nameplateInstance);
        }
        if (params.startsWith("nps_")){
            String np = params.substring(4);
            NameplateText nameplateText = ConfigManager.papiNP.get(np);
            if (nameplateText == null) return "";
            NameplateInstance nameplateInstance = ResourceManager.NAMEPLATES.get(nameplateText.getNameplate());
            if (nameplateInstance == null) return "";
            String text = nameplateText.getText();
            if (ConfigManager.Main.placeholderAPI) text = PlaceholderAPI.setPlaceholders(player, text);
            return NameplateUtil.getSuffixChar(text);
        }
        return null;
    }
}
