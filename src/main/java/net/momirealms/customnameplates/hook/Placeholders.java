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
import net.momirealms.customnameplates.background.BackGround;
import net.momirealms.customnameplates.font.FontCache;
import net.momirealms.customnameplates.font.FontWidth;
import net.momirealms.customnameplates.font.FontWidthThin;
import net.momirealms.customnameplates.nameplates.NameplateUtil;
import net.momirealms.customnameplates.resource.ResourceManager;
import net.momirealms.customnameplates.scoreboard.NameplatesTeam;
import net.momirealms.customnameplates.scoreboard.ScoreBoardManager;
import net.momirealms.customnameplates.utils.BGInfo;
import net.momirealms.customnameplates.utils.NPInfo;
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
        if (params.equals("prefix")){
            if (ConfigManager.MainConfig.tab){
                String teamName = TABHook.getTABTeam(player.getName());
                NameplatesTeam nameplatesTeam = ScoreBoardManager.teams.get(teamName);
                if (nameplatesTeam != null){
                    return nameplatesTeam.getPrefixText();
                }
            }else {
                NameplatesTeam nameplatesTeam = ScoreBoardManager.teams.get(player.getName());
                if (nameplatesTeam != null){
                    return nameplatesTeam.getPrefixText();
                }
            }
        }
        if (params.equals("suffix")){
            if (ConfigManager.MainConfig.tab){
                String teamName = TABHook.getTABTeam(player.getName());
                NameplatesTeam nameplatesTeam = ScoreBoardManager.teams.get(teamName);
                if (nameplatesTeam != null){
                    return nameplatesTeam.getSuffixText();
                }
            }else {
                NameplatesTeam nameplatesTeam = ScoreBoardManager.teams.get(player.getName());
                if (nameplatesTeam != null){
                    return nameplatesTeam.getSuffixText();
                }
            }
        }
        if (params.startsWith("bg_")){
            String bg = params.substring(3);
            BGInfo bgInfo = ConfigManager.papiBG.get(bg);
            if (bgInfo != null){
                BackGround backGround = ConfigManager.backgrounds.get(bgInfo.getBackground());
                if (backGround != null){
                    String text;
                    if (ConfigManager.MainConfig.placeholderAPI){
                        text = PlaceholderAPI.setPlaceholders(player, bgInfo.getText());
                    }else {
                        text = bgInfo.getText();
                    }
                    if (ConfigManager.MainConfig.thin_font){
                        return backGround.getBackGround(FontWidthThin.getTotalWidth(text));
                    }else {
                        return backGround.getBackGround(FontWidth.getTotalWidth(text));
                    }
                }
            }
        }
        if (params.startsWith("npp_")){
            String np = params.substring(4);
            NPInfo npInfo = ConfigManager.papiNP.get(np);
            if (npInfo != null){
                FontCache fontCache = ResourceManager.caches.get(npInfo.getNameplate());
                if (fontCache != null){
                    if (ConfigManager.MainConfig.placeholderAPI){
                        return new NameplateUtil(fontCache).makeCustomNameplate("", PlaceholderAPI.setPlaceholders(player, npInfo.getText()),"");
                    }else {
                        return new NameplateUtil(fontCache).makeCustomNameplate("", npInfo.getText(),"");
                    }
                }
            }
        }
        if (params.startsWith("nps_")){
            String np = params.substring(4);
            NPInfo npInfo = ConfigManager.papiNP.get(np);
            if (npInfo != null){
                FontCache fontCache = ResourceManager.caches.get(npInfo.getNameplate());
                if (fontCache != null){
                    if (ConfigManager.MainConfig.placeholderAPI){
                        return new NameplateUtil(fontCache).getSuffixLength(PlaceholderAPI.setPlaceholders(player, npInfo.getText()));
                    }else {
                        return new NameplateUtil(fontCache).getSuffixLength(npInfo.getText());
                    }
                }
            }
        }
        return null;
    }
}
