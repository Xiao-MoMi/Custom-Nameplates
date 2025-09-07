/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customnameplates.bukkit.compatibility;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.CustomNameplatesAPI;
import net.momirealms.customnameplates.api.feature.OffsetFont;
import net.momirealms.customnameplates.api.feature.background.Background;
import net.momirealms.customnameplates.api.feature.bubble.Bubble;
import net.momirealms.customnameplates.api.feature.nameplate.Nameplate;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.placeholder.internal.StaticPosition;
import net.momirealms.customnameplates.common.util.MoonPhase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class NameplatesExtraExpansion extends PlaceholderExpansion {
    private static final Map<Character, Character> CHAR_CONVERTOR = new HashMap<>();
    static {
        CHAR_CONVERTOR.put('A', 'ᴀ');
        CHAR_CONVERTOR.put('a', 'ᴀ');
        CHAR_CONVERTOR.put('B', 'ʙ');
        CHAR_CONVERTOR.put('b', 'ʙ');
        CHAR_CONVERTOR.put('C', 'ᴄ');
        CHAR_CONVERTOR.put('c', 'ᴄ');
        CHAR_CONVERTOR.put('D', 'ᴅ');
        CHAR_CONVERTOR.put('d', 'ᴅ');
        CHAR_CONVERTOR.put('E', 'ᴇ');
        CHAR_CONVERTOR.put('e', 'ᴇ');
        CHAR_CONVERTOR.put('F', 'ꜰ');
        CHAR_CONVERTOR.put('f', 'ꜰ');
        CHAR_CONVERTOR.put('G', 'ɢ');
        CHAR_CONVERTOR.put('g', 'ɢ');
        CHAR_CONVERTOR.put('H', 'ʜ');
        CHAR_CONVERTOR.put('h', 'ʜ');
        CHAR_CONVERTOR.put('I', 'ɪ');
        CHAR_CONVERTOR.put('i', 'ɪ');
        CHAR_CONVERTOR.put('J', 'ᴊ');
        CHAR_CONVERTOR.put('j', 'ᴊ');
        CHAR_CONVERTOR.put('K', 'ᴋ');
        CHAR_CONVERTOR.put('k', 'ᴋ');
        CHAR_CONVERTOR.put('L', 'ʟ');
        CHAR_CONVERTOR.put('l', 'ʟ');
        CHAR_CONVERTOR.put('M', 'ᴍ');
        CHAR_CONVERTOR.put('m', 'ᴍ');
        CHAR_CONVERTOR.put('N', 'ɴ');
        CHAR_CONVERTOR.put('n', 'ɴ');
        CHAR_CONVERTOR.put('O', 'ᴏ');
        CHAR_CONVERTOR.put('o', 'ᴏ');
        CHAR_CONVERTOR.put('P', 'ᴘ');
        CHAR_CONVERTOR.put('p', 'ᴘ');
        CHAR_CONVERTOR.put('Q', 'ꞯ');
        CHAR_CONVERTOR.put('q', 'ꞯ');
        CHAR_CONVERTOR.put('R', 'ʀ');
        CHAR_CONVERTOR.put('r', 'ʀ');
        CHAR_CONVERTOR.put('S', 'ꜱ');
        CHAR_CONVERTOR.put('s', 'ꜱ');
        CHAR_CONVERTOR.put('T', 'ᴛ');
        CHAR_CONVERTOR.put('t', 'ᴛ');
        CHAR_CONVERTOR.put('U', 'ᴜ');
        CHAR_CONVERTOR.put('u', 'ᴜ');
        CHAR_CONVERTOR.put('V', 'ᴠ');
        CHAR_CONVERTOR.put('v', 'ᴠ');
        CHAR_CONVERTOR.put('W', 'ᴡ');
        CHAR_CONVERTOR.put('w', 'ᴡ');
        CHAR_CONVERTOR.put('X', 'x');
        CHAR_CONVERTOR.put('x', 'x');
        CHAR_CONVERTOR.put('Y', 'ʏ');
        CHAR_CONVERTOR.put('y', 'ʏ');
        CHAR_CONVERTOR.put('Z', 'ᴢ');
        CHAR_CONVERTOR.put('z', 'ᴢ');
        CHAR_CONVERTOR.put('0', '₀');
        CHAR_CONVERTOR.put('1', '₁');
        CHAR_CONVERTOR.put('2', '₂');
        CHAR_CONVERTOR.put('3', '₃');
        CHAR_CONVERTOR.put('4', '₄');
        CHAR_CONVERTOR.put('5', '₅');
        CHAR_CONVERTOR.put('6', '₆');
        CHAR_CONVERTOR.put('7', '₇');
        CHAR_CONVERTOR.put('8', '₈');
        CHAR_CONVERTOR.put('9', '₉');
    }

    private final CustomNameplates plugin;

    public NameplatesExtraExpansion(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "npex";
    }

    @Override
    public @NotNull String getAuthor() {
        return "XiaoMoMi";
    }

    @Override
    public @NotNull String getVersion() {
        return "3.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] split = params.split("_", 2);
        if (split.length == 0) {
            return null;
        }
        switch (split[0]) {
            case "offset" -> {
                if (split.length != 2) {
                    return null;
                }
                return OffsetFont.createOffsets(Float.parseFloat(split[1]));
            }
            // left:0:xxx
            case "static" -> {
                if (split.length != 2) {
                    return null;
                }
                String subParams = split[1];
                String[] subSplit = subParams.split(":", 3);
                if (subSplit.length != 3) {
                    return null;
                }
                return CustomNameplatesAPI.getInstance().createStaticText(subSplit[2], Integer.parseInt(subSplit[1]), StaticPosition.valueOf(subSplit[0].toUpperCase(Locale.ENGLISH)));
            }
            case "background" -> {
                if (split.length != 2) {
                    return null;
                }
                String subParams = split[1];
                String[] subSplit = subParams.split(":", 4);
                // 0      1    2     3
                // config:left:right:advance
                Optional<Background> optional = CustomNameplatesAPI.getInstance().getBackground(subSplit[0]);
                float advance;
                try {
                    advance = Float.parseFloat(subSplit[3]);
                } catch (NumberFormatException e) {
                    String text;
                    if (subSplit[3].startsWith("{") && subSplit[3].endsWith("}")) {
                        String before = "%" + subSplit[3].substring(1, subSplit[3].length() - 1) + "%";
                        text = PlaceholderAPI.setPlaceholders(player, before);
                        advance = Float.parseFloat(text);
                    } else {
                        return null;
                    }
                }
                float finalAdvance = advance;
                return optional.map(background ->
                                AdventureHelper.surroundWithNameplatesFont(background.createImage(finalAdvance, Float.parseFloat(subSplit[1]), Float.parseFloat(subSplit[2]))))
                        .orElse(null);
            }
            case "nameplate" -> {
                if (split.length != 2) {
                    return null;
                }
                String subParams = split[1];
                String[] subSplit = subParams.split(":", 4);
                // 0      1    2     3
                // config:left:right:advance
                Optional<Nameplate> optional = CustomNameplatesAPI.getInstance().getNameplate(subSplit[0]);
                float advance;
                try {
                    advance = Float.parseFloat(subSplit[3]);
                } catch (NumberFormatException e) {
                    String text;
                    if (subSplit[3].startsWith("{") && subSplit[3].endsWith("}")) {
                        String before = "%" + subSplit[3].substring(1, subSplit[3].length() - 1) + "%";
                        text = PlaceholderAPI.setPlaceholders(player, before);
                        advance = Float.parseFloat(text);
                    } else {
                        return null;
                    }
                }
                float finalAdvance = advance;
                return optional.map(nameplate ->
                                AdventureHelper.surroundWithNameplatesFont(nameplate.createImage(finalAdvance, Float.parseFloat(subSplit[1]), Float.parseFloat(subSplit[2]))))
                        .orElse(null);
            }
            case "bubble" -> {
                if (split.length != 2) {
                    return null;
                }
                String subParams = split[1];
                String[] subSplit = subParams.split(":", 4);
                // 0      1    2     3
                // config:left:right:advance
                Optional<Bubble> optional = CustomNameplatesAPI.getInstance().getBubble(subSplit[0]);
                float advance;
                try {
                    advance = Float.parseFloat(subSplit[3]);
                } catch (NumberFormatException e) {
                    String text;
                    if (subSplit[3].startsWith("{") && subSplit[3].endsWith("}")) {
                        String before = "%" + subSplit[3].substring(1, subSplit[3].length() - 1) + "%";
                        text = PlaceholderAPI.setPlaceholders(player, before);
                        advance = Float.parseFloat(text);
                    } else {
                        return null;
                    }
                }
                float finalAdvance = advance;
                return optional.map(bubble ->
                                AdventureHelper.surroundWithNameplatesFont(bubble.createImage(finalAdvance, Float.parseFloat(subSplit[1]), Float.parseFloat(subSplit[2]))))
                        .orElse(null);
            }
            case "lunarphase" -> {
                if (split.length == 1 && player != null && player.isOnline()) {
                    Player online = player.getPlayer();
                    if (online == null) return null;
                    return MoonPhase.getPhase(online.getWorld().getFullTime() / 24_000).name().toLowerCase(Locale.ENGLISH);
                }
                if (split.length == 2) {
                    String world = split[1];
                    World bukkitWorld = Bukkit.getWorld(world);
                    if (bukkitWorld == null) return null;
                    return MoonPhase.getPhase(bukkitWorld.getFullTime() / 24_000).name().toLowerCase(Locale.ENGLISH);
                }
                return null;
            }
            case "weather" -> {
                World world = null;
                if (split.length == 1 && player != null && player.isOnline()) {
                    Player online = player.getPlayer();
                    if (online == null) return null;
                    world = online.getWorld();
                }
                if (split.length == 2) {
                    World bukkitWorld = Bukkit.getWorld(split[1]);
                    if (bukkitWorld == null) return null;
                    world = bukkitWorld;
                }
                if (world == null) return null;
                String currentWeather;
                if (world.isClearWeather()) currentWeather = "clear";
                else if (world.isThundering()) currentWeather = "thunder";
                else currentWeather = "rain";
                return currentWeather;
            }
            case "time12" -> {
                long time;
                if (split.length == 1 && player != null && player.isOnline()) {
                    Player online = player.getPlayer();
                    if (online == null) return null;
                    time = online.getWorld().getTime();
                } else if (split.length == 2) {
                    String world = split[1];
                    World bukkitWorld = Bukkit.getWorld(world);
                    if (bukkitWorld == null) return null;
                    time = bukkitWorld.getTime();
                } else {
                    return null;
                }
                String ap = time >= 6000 && time < 18000 ? " PM" : " AM";
                int hours = (int) (time / 1000) ;
                int minutes = (int) ((time - hours * 1000 ) * 0.06);
                hours += 6;
                while (hours >= 12) hours -= 12;
                if (minutes < 10) return hours + ":0" + minutes + ap;
                else return hours + ":" + minutes + ap;
            }
            case "time24" -> {
                long time;
                if (split.length == 1 && player != null && player.isOnline()) {
                    Player online = player.getPlayer();
                    if (online == null) return null;
                    time = online.getWorld().getTime();
                } else if (split.length == 2) {
                    String world = split[1];
                    World bukkitWorld = Bukkit.getWorld(world);
                    if (bukkitWorld == null) return null;
                    time = bukkitWorld.getTime();
                } else {
                    return null;
                }
                int hours = (int) (time / 1000);
                int minutes = (int) ((time - hours * 1000) * 0.06);
                hours += 6;
                if (hours >= 24) hours -= 24;
                String minuteStr = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);
                return hours + ":" + minuteStr;
            }
            case "newline" -> {
                return "\n";
            }
            case "smallfont" -> {
                if (split.length != 2) {
                    return null;
                }
                String anotherPlaceholder = PlaceholderAPI.setPlaceholders(player, "%" + split[1] + "%");
                StringBuilder builder = new StringBuilder();
                for (char c : anotherPlaceholder.toCharArray()) {
                    builder.append(CHAR_CONVERTOR.getOrDefault(c, c));
                }
                String finalString = builder.toString();
                if (finalString.length() == split[1].length() + 2 && finalString.charAt(0) == '%' && finalString.charAt(finalString.length() - 1) == '%') {
                    return finalString.substring(1, finalString.length() - 1);
                }
                return finalString;
            }
            case "parse-twice" -> {
                String anotherPlaceholder = PlaceholderAPI.setPlaceholders(player, "%" + split[1] + "%");
                return PlaceholderAPI.setPlaceholders(player, anotherPlaceholder);
            }
        }
        return null;
    }
}
