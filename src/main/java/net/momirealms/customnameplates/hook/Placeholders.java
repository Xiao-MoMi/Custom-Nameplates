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
import net.momirealms.customnameplates.objects.BackGround;
import net.momirealms.customnameplates.data.DataManager;
import net.momirealms.customnameplates.data.PlayerData;
import net.momirealms.customnameplates.nameplates.NameplateInstance;
import net.momirealms.customnameplates.font.FontUtil;
import net.momirealms.customnameplates.nameplates.NameplateUtil;
import net.momirealms.customnameplates.resource.ResourceManager;
import net.momirealms.customnameplates.scoreboard.NameplatesTeam;
import net.momirealms.customnameplates.scoreboard.ScoreBoardManager;
import net.momirealms.customnameplates.objects.BGInfo;
import net.momirealms.customnameplates.objects.NPInfo;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {

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
            PlayerData playerData = DataManager.cache.get(player.getUniqueId());
            String nameplate = playerData.getEquippedNameplate();
            if (!nameplate.equals("none")) return ResourceManager.NAMEPLATES.get(nameplate).getName();
            else return ConfigManager.Message.noNameplate;
        }
        if (params.equals("prefix")){
            String teamName = player.getName();
            if (ConfigManager.MainConfig.tab) teamName = TABHook.getTABTeam(teamName);
            NameplatesTeam nameplatesTeam = ScoreBoardManager.teams.get(teamName);
            if (nameplatesTeam != null) return nameplatesTeam.getPrefixText();
            else return "";
        }
        if (params.equals("suffix")){
            String teamName = player.getName();
            if (ConfigManager.MainConfig.tab) teamName = TABHook.getTABTeam(teamName);
            NameplatesTeam nameplatesTeam = ScoreBoardManager.teams.get(teamName);
            if (nameplatesTeam != null) return nameplatesTeam.getSuffixText();
            else return "";
        }
        if (params.startsWith("bg_")){
            String bg = params.substring(3);
            BGInfo bgInfo = ConfigManager.papiBG.get(bg);
            if (bgInfo != null){
                BackGround backGround = ConfigManager.backgrounds.get(bgInfo.getBackground());
                if (backGround != null){
                    String text = bgInfo.getText();
                    if (ConfigManager.MainConfig.placeholderAPI) text = PlaceholderAPI.setPlaceholders(player, text);
                    return backGround.getBackGround(FontUtil.getTotalWidth(text));
                }
            }
        }
        if (params.startsWith("npp_")){
            String np = params.substring(4);
            NPInfo npInfo = ConfigManager.papiNP.get(np);
            if (npInfo != null){
                NameplateInstance nameplateInstance = ResourceManager.NAMEPLATES.get(npInfo.getNameplate());
                if (nameplateInstance != null){
                    String text = npInfo.getText();
                    if (ConfigManager.MainConfig.placeholderAPI) text = PlaceholderAPI.setPlaceholders(player, text);
                    return NameplateUtil.makeCustomNameplate("", text,"", nameplateInstance);
                }
            }
        }
        if (params.startsWith("nps_")){
            String np = params.substring(4);
            NPInfo npInfo = ConfigManager.papiNP.get(np);
            if (npInfo != null){
                NameplateInstance nameplateInstance = ResourceManager.NAMEPLATES.get(npInfo.getNameplate());
                if (nameplateInstance != null){
                    String text = npInfo.getText();
                    if (ConfigManager.MainConfig.placeholderAPI) text = PlaceholderAPI.setPlaceholders(player, text);
                    return NameplateUtil.getSuffixChar(text);
                }
            }
        }
        return null;
    }
}
