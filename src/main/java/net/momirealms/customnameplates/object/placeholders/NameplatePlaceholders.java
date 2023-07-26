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

package net.momirealms.customnameplates.object.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.ConfigManager;
import net.momirealms.customnameplates.manager.FontManager;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.manager.PlaceholderManager;
import net.momirealms.customnameplates.object.SimpleChar;
import net.momirealms.customnameplates.object.nameplate.NameplateConfig;
import net.momirealms.customnameplates.object.nameplate.NameplatesTeam;
import net.momirealms.customnameplates.utils.AdventureUtils;
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
            case "actionbar" -> {
                return getOtherActionBar(player);
            }
            case "unicode" -> {
                return getUnicodeDescent(mainParam[1], player);
            }
        }
        return null;
    }

    private String getOtherActionBar(Player player) {
        return plugin.getActionBarManager().getOtherPluginActionBarText(player);
    }

    private String getOffset(String s) {
        return ConfigManager.surroundWithFont(plugin.getFontManager().getOffset(Integer.parseInt(s)));
    }

    private String getNameplateText(String param, Player player) {
        NameplateText nameplateText = placeholderManager.getNameplateText(param);
        if (nameplateText == null) return param + " NOT FOUND";
        String parsed = PlaceholderAPI.setPlaceholders(player, nameplateText.text());
        NameplateManager nameplateManager = plugin.getNameplateManager();
        NameplateConfig nameplateConfig = nameplateManager.getNameplateConfig(nameplateText.nameplate());
        if (nameplateConfig == null) return nameplateText.nameplate() + " NOT FOUND";
        String text = AdventureUtils.stripAllTags(parsed);
        return nameplateManager.getNameplatePrefixWithFont(text, nameplateConfig) + parsed + plugin.getFontManager().getSuffixStringWithFont(text);
    }

    private String getEquipped(String param, Player player) {
        if (param.equals("nameplate")) {
            return plugin.getDataManager().getEquippedNameplate(player);
        } else if (param.equals("bubble")) {
            return plugin.getDataManager().getEquippedBubble(player);
        }
        return "null";
    }

    private String getTime(Player player) {
        long time = player.getWorld().getTime();
        String ap = time >= 6000 && time < 18000 ? " PM" : " AM";
        int hours = (int) (time / 1000) ;
        int minutes = (int) ((time - hours * 1000 ) * 0.06);
        hours += 6;
        while (hours >= 12) hours -= 12;
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
        FontManager fontManager = plugin.getFontManager();
        String parsed = PlaceholderAPI.setPlaceholders(player, staticText.text());
        int parsedWidth = fontManager.getTotalWidth(AdventureUtils.stripAllTags(parsed));
        if (staticText.staticState() == StaticText.StaticState.LEFT) {
            return parsed + ConfigManager.surroundWithFont(fontManager.getOffset(staticText.value() - parsedWidth));
        } else if (staticText.staticState() == StaticText.StaticState.RIGHT) {
            return ConfigManager.surroundWithFont(fontManager.getOffset(staticText.value() - parsedWidth)) + parsed;
        } else if (staticText.staticState() == StaticText.StaticState.MIDDLE) {
            int half = (staticText.value() - parsedWidth) / 2;
            String left = ConfigManager.surroundWithFont(fontManager.getOffset(half));
            String right = ConfigManager.surroundWithFont(fontManager.getOffset(staticText.value() - parsedWidth - half));
            return left + parsed + right;
        }
        return "";
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

    private String getUnicodeDescent(String param, Player player) {
        if (!ConfigManager.enable1_20_Unicode && plugin.getVersionHelper().isVersionNewerThan1_20()) {
            return "Not Available on 1.20";
        }
        DescentText descentText = placeholderManager.getDescentUnicode(param);
        if (descentText == null) return param + " NOT FOUND";
        String parsed = PlaceholderAPI.setPlaceholders(player, descentText.text());
        return "<font:" + ConfigManager.namespace + ":" + "unicode_ascent_" + descentText.ascent() + ">" + parsed + "</font>";
    }

    private String getVanilla(String param, Player player) {
        VanillaHud vanillaHud = placeholderManager.getVanillaHud(param);
        if (vanillaHud == null) return param + " NOT FOUND";
        double current;
        double max;
        try {
            current= Double.parseDouble(PlaceholderAPI.setPlaceholders(player, vanillaHud.papi()));
            max = Double.parseDouble(PlaceholderAPI.setPlaceholders(player, vanillaHud.max()));
        } catch (NumberFormatException e) {
            current = 1;
            max = 1;
        }
        int point = (int) ((current / max) * 20);
        int full_amount = point / 2;
        int half_amount = point % 2;
        int empty_amount = 10 - full_amount - half_amount;
        if (vanillaHud.reverse()) {
            return "<#FFFEFD>" + vanillaHud.empty().repeat(empty_amount) + vanillaHud.half().repeat(half_amount) + vanillaHud.full().repeat(full_amount) + "</#FFFEFD>";
        } else {
            return "<#FFFEFD>" + vanillaHud.full().repeat(full_amount) + vanillaHud.half().repeat(half_amount) + vanillaHud.empty().repeat(empty_amount) + "</#FFFEFD>";
        }
    }
}
