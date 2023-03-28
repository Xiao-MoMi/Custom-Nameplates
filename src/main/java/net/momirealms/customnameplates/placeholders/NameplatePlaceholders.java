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

package net.momirealms.customnameplates.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.ConfigManager;
import net.momirealms.customnameplates.manager.PlaceholderManager;
import net.momirealms.customnameplates.object.SimpleChar;
import net.momirealms.customnameplates.object.StaticText;
import net.momirealms.customnameplates.object.nameplate.NameplatesTeam;
import net.momirealms.customnameplates.utils.AdventureUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NameplatePlaceholders extends PlaceholderExpansion {

    private final PlaceholderManager placeholderManager;
    private final CustomNameplates plugin;

    public NameplatePlaceholders(CustomNameplates plugin, PlaceholderManager placeholderManager) {
        this.placeholderManager = placeholderManager;
        this.plugin = plugin;
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
        return "2.2";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        String[] mainParam = params.split("_", 2);
        switch (mainParam[0]) {
            case "prefix" -> {
                return getPrefix(player);
            }
            case "suffix" -> {
                return getSuffix(player);
            }
            case "equipped" -> {
                return getEquipped(mainParam[1], player);
            }
            case "static" -> {
                return getStatic(mainParam[1], player);
            }
            case "background" -> {
                return getBackground(mainParam[1], player);
            }
            case "nameplate" -> {
                return getNameplateText(mainParam[1], player);
            }
            case "conditional" -> {
                return getConditional(mainParam[1], player);
            }
            case "descent" -> {
                return getDescent(mainParam[1], player);
            }
            case "vanilla" -> {
                return getVanilla(mainParam[1], player);
            }
            case "image" -> {
                return getImage(mainParam[1]);
            }
            case "offset" -> {
                return getOffset(mainParam[1]);
            }
            case "time" -> {
                return getTime(player);
            }
            case "checkupdate" -> {
                return String.valueOf(!plugin.getVersionHelper().isLatest());
            }
        }
        return null;
    }

    private String getOffset(String s) {
        return ConfigManager.surroundWithFont(plugin.getFontManager().getOffset(Integer.parseInt(s)));
    }

    private String getNameplateText(String param, Player player) {
        NameplateText nameplateText = placeholderManager.getNameplateText(param);
        if (nameplateText == null) return param + " NOT FOUND";
        String parsed = PlaceholderAPI.setPlaceholders(player, nameplateText.text());
        return plugin.getNameplateManager().getNameplatePrefix(parsed, nameplateText.nameplate());
    }

    private String getEquipped(String param, Player player) {
        if (param.equals("nameplate")) {
            return plugin.getDataManager().getEquippedNameplate(player);
        }
        else if (param.equals("bubble")) {
            return plugin.getDataManager().getEquippedBubble(player);
        }
        return "null";
    }

    private String getTime(Player player) {
        World world = player.getWorld();
        long time = world.getTime();
        String ap;
        if (time >= 6000 && time < 18000) ap = " PM";
        else ap = " AM";
        int hours = (int) (time / 1000);
        int minutes = (int) ((time - hours * 1000 ) * 0.06);
        hours += 6;
        if (hours >= 24) hours -= 24;
        if (hours >= 12) hours -= 12;
        if (minutes < 10) return hours + ":0" + minutes + ap;
        else return hours + ":" + minutes + ap;
    }

    private String getImage(String param) {
        SimpleChar simpleChar = plugin.getImageManager().getImage(param);
        if (simpleChar == null) return param + " NOT FOUND";
        return ConfigManager.surroundWithFont(String.valueOf(simpleChar.getChars()));
    }

    private String getPrefix(Player player) {
        NameplatesTeam nameplatesTeam = plugin.getTeamManager().getNameplateTeam(player.getUniqueId());
        if (nameplatesTeam != null) return nameplatesTeam.getNameplatePrefixText();
        return "";
    }

    private String getSuffix(Player player) {
        NameplatesTeam nameplatesTeam = plugin.getTeamManager().getNameplateTeam(player.getUniqueId());
        if (nameplatesTeam != null) return nameplatesTeam.getNameplateSuffixText();
        return "";
    }

    private String getStatic(String param, Player player) {
        StaticText staticText = placeholderManager.getStaticText(param);
        if (staticText == null) return param + " NOT FOUND";
        String parsed = PlaceholderAPI.setPlaceholders(player, staticText.text());
        int parsedWidth = plugin.getFontManager().getTotalWidth(AdventureUtils.stripAllTags(parsed));
        if (staticText.left()) {
            return parsed + ConfigManager.surroundWithFont(plugin.getFontManager().getOffset(staticText.value() - parsedWidth));
        }
        else {
            return ConfigManager.surroundWithFont(plugin.getFontManager().getOffset(staticText.value() - parsedWidth)) + parsed;

        }
    }

    private String getBackground(String param, Player player) {
        BackGroundText backGroundText = placeholderManager.getBackgroundText(param);
        if (backGroundText == null) return param + " NOT FOUND";
        String parsed = PlaceholderAPI.setPlaceholders(player, backGroundText.text());
        String background = plugin.getBackgroundManager().getBackGroundImage(backGroundText, AdventureUtils.stripAllTags(parsed));
        if (backGroundText.remove_shadow()) background = "<#FFFEFD>" + background + "</#FFFEFD>";
        return ConfigManager.surroundWithFont(background) + parsed;
    }

    private String getConditional(String param, Player player) {
        ConditionalTexts conditionalTexts = placeholderManager.getConditionalTexts(param);
        if (conditionalTexts == null) return param + " NOT FOUND";
        String value = conditionalTexts.getValue(player);
        return PlaceholderAPI.setPlaceholders(player, value);
    }

    private String getDescent(String param, Player player) {
        DescentText descentText = placeholderManager.getDescentText(param);
        if (descentText == null) return param + " NOT FOUND";
        String parsed = PlaceholderAPI.setPlaceholders(player, descentText.text());
        return "<font:" + ConfigManager.namespace + ":" + "ascent_" + descentText.ascent() + ">" + parsed + "</font>";
    }

    private String getVanilla(String param, Player player) {
        VanillaHud vanillaHud = placeholderManager.getVanillaHud(param);
        if (vanillaHud == null) return param + " NOT FOUND";
        double current = Double.parseDouble(PlaceholderAPI.setPlaceholders(player, vanillaHud.papi()));
        double max = Double.parseDouble(PlaceholderAPI.setPlaceholders(player, vanillaHud.max()));
        int point = (int) ((current / max) * 20);
        int full_amount = point / 2;
        int half_amount = point % 2;
        int empty_amount = 10 - full_amount - half_amount;
        return "<#FFFEFD>" + vanillaHud.empty().repeat(empty_amount) + vanillaHud.half().repeat(half_amount) + vanillaHud.full().repeat(full_amount) + "</#FFFEFD>";
    }
}
