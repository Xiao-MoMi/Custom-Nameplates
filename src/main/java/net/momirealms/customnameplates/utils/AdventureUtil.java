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

package net.momirealms.customnameplates.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdventureUtil {

    public static void sendMessage(CommandSender sender, String s) {
        if (sender instanceof Player player) playerMessage(player, s);
        else consoleMessage(s);
    }

    public static void consoleMessage(String s) {
        Audience au = CustomNameplates.adventure.sender(Bukkit.getConsoleSender());
        MiniMessage mm = MiniMessage.miniMessage();
        Component parsed = mm.deserialize(replaceLegacy(s));
        au.sendMessage(parsed);
    }

    public static void playerMessage(Player player, String s){
        Audience au = CustomNameplates.adventure.player(player);
        MiniMessage mm = MiniMessage.miniMessage();
        Component parsed = mm.deserialize(replaceLegacy(s));
        au.sendMessage(parsed);
    }

    public static void playerActionbar(Player player, String s) {
        Audience au = CustomNameplates.adventure.player(player);
        MiniMessage mm = MiniMessage.miniMessage();
        au.sendActionBar(mm.deserialize(replaceLegacy(s)));
    }

    public static String replaceLegacy(String s) {
        if (!(s.contains("&") || s.contains("§"))) return s;
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = s.replaceAll("&","§").toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '§') {
                if (i + 1 < chars.length) {
                    switch (chars[i+1]){
                        case '0' -> {
                            i++;
                            stringBuilder.append("<black>");
                        }
                        case '1' -> {
                            i++;
                            stringBuilder.append("<dark_blue>");
                        }
                        case '2' -> {
                            i++;
                            stringBuilder.append("<dark_green>");
                        }
                        case '3' -> {
                            i++;
                            stringBuilder.append("<dark_aqua>");
                        }
                        case '4' -> {
                            i++;
                            stringBuilder.append("<dark_red>");
                        }
                        case '5' -> {
                            i++;
                            stringBuilder.append("<dark_purple>");
                        }
                        case '6' -> {
                            i++;
                            stringBuilder.append("<gold>");
                        }
                        case '7' -> {
                            i++;
                            stringBuilder.append("<gray>");
                        }
                        case '8' -> {
                            i++;
                            stringBuilder.append("<dark_gray>");
                        }
                        case '9' -> {
                            i++;
                            stringBuilder.append("<blue>");
                        }
                        case 'a' -> {
                            i++;
                            stringBuilder.append("<green>");
                        }
                        case 'b' -> {
                            i++;
                            stringBuilder.append("<aqua>");
                        }
                        case 'c' -> {
                            i++;
                            stringBuilder.append("<red>");
                        }
                        case 'd' -> {
                            i++;
                            stringBuilder.append("<light_purple>");
                        }
                        case 'e' -> {
                            i++;
                            stringBuilder.append("<yellow>");
                        }
                        case 'f' -> {
                            i++;
                            stringBuilder.append("<white>");
                        }
                        case 'r' -> {
                            i++;
                            stringBuilder.append("<reset><!italic>");
                        }
                        case 'l' -> {
                            i++;
                            stringBuilder.append("<bold>");
                        }
                        case 'm' -> {
                            i++;
                            stringBuilder.append("<strikethrough>");
                        }
                        case 'o' -> {
                            i++;
                            stringBuilder.append("<italic>");
                        }
                        case 'n' -> {
                            i++;
                            stringBuilder.append("<underlined>");
                        }
                        case 'x' -> {
                            stringBuilder.append("<#").append(chars[i+3]).append(chars[i+5]).append(chars[i+7]).append(chars[i+9]).append(chars[i+11]).append(chars[i+13]).append(">");
                            i += 13;
                        }
                        case 'k' -> {
                            i++;
                            stringBuilder.append("<obfuscated>");
                        }
                    }
                }
            }
            else {
                stringBuilder.append(chars[i]);
            }
        }
        return stringBuilder.toString();
    }
}
