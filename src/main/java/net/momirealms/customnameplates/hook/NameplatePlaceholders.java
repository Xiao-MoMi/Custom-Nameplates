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
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.manager.PlaceholderManager;
import net.momirealms.customnameplates.manager.ResourceManager;
import net.momirealms.customnameplates.objects.StaticText;
import net.momirealms.customnameplates.objects.background.BackGround;
import net.momirealms.customnameplates.objects.background.BackGroundText;
import net.momirealms.customnameplates.objects.font.FontUtil;
import net.momirealms.customnameplates.objects.nameplates.NameplateConfig;
import net.momirealms.customnameplates.objects.nameplates.NameplateText;
import net.momirealms.customnameplates.objects.nameplates.NameplatesTeam;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NameplatePlaceholders extends PlaceholderExpansion {

    private final PlaceholderManager placeholderManager;

    public NameplatePlaceholders(PlaceholderManager placeholderManager) {
        this.placeholderManager = placeholderManager;
    }

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
        return "1.2";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.equals("equipped")){
            String nameplate = CustomNameplates.plugin.getDataManager().getPlayerData(player).getEquippedNameplate();
            if (!nameplate.equals("none")) return ResourceManager.NAMEPLATES.get(nameplate).name();
            return MessageManager.noNameplate;
        }
        if (params.equals("prefix")){
            NameplatesTeam nameplatesTeam = CustomNameplates.plugin.getNameplateManager().getTeamManager().getNameplatesTeam(player);
            if (nameplatesTeam != null) return nameplatesTeam.getPrefixText();
            return "";
        }
        if (params.equals("suffix")){
            NameplatesTeam nameplatesTeam = CustomNameplates.plugin.getNameplateManager().getTeamManager().getNameplatesTeam(player);
            if (nameplatesTeam != null) return nameplatesTeam.getSuffixText();
            return "";
        }
        if (params.startsWith("bg_")){
            String bg = params.substring(3);
            BackGroundText backGroundText = placeholderManager.getPapiBG().get(bg);;
            if (backGroundText == null) return "";
            BackGround backGround = ResourceManager.BACKGROUNDS.get(backGroundText.getBackground());
            if (backGround == null) return "";
            String text = PlaceholderAPI.setPlaceholders(player, backGroundText.getText());
            return backGround.getBackGround(FontUtil.getTotalWidth(text));
        }
        if (params.startsWith("static_")){
            String staticKey = params.substring(7);
            StaticText staticText = placeholderManager.getPapiST().get(staticKey);
            if (staticText == null) return "";
            String text = PlaceholderAPI.setPlaceholders(player, staticText.getText());
            int offset = staticText.getStaticValue() - FontUtil.getTotalWidth(text);
            return FontUtil.getOffset(offset);
        }
        if (params.startsWith("npp_")){
            String np = params.substring(4);
            NameplateText nameplateText = placeholderManager.getPapiNP().get(np);
            if (nameplateText == null) return "";
            NameplateConfig nameplateConfig = ResourceManager.NAMEPLATES.get(nameplateText.getNameplate());
            if (nameplateConfig == null) return "";
            String text = placeholderManager.parsePlaceholders(player, nameplateText.getText());
            return CustomNameplates.plugin.getNameplateManager().makeCustomNameplate("", text,"", nameplateConfig);
        }
        if (params.startsWith("nps_")){
            String np = params.substring(4);
            NameplateText nameplateText = placeholderManager.getPapiNP().get(np);
            if (nameplateText == null) return "";
            NameplateConfig nameplateConfig = ResourceManager.NAMEPLATES.get(nameplateText.getNameplate());
            if (nameplateConfig == null) return "";
            String text = placeholderManager.parsePlaceholders(player, nameplateText.getText());
            return CustomNameplates.plugin.getNameplateManager().getSuffixChar(text);
        }
        return null;
    }
}
