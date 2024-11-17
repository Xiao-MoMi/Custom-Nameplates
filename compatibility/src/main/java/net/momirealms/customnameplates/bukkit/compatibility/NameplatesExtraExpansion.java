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
import net.momirealms.customnameplates.common.util.MoonPhase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

public class NameplatesExtraExpansion extends PlaceholderExpansion {

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
        }
        return null;
    }
}
