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
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdventureUtils {

    public static Component getComponentFromMiniMessage(String text) {
        return MiniMessage.miniMessage().deserialize(replaceLegacy(text));
    }

    public static void sendMessage(CommandSender sender, String s) {
        if (s == null) return;
        if (sender instanceof Player player) playerMessage(player, s);
        else consoleMessage(s);
    }

    public static void consoleMessage(String s) {
        if (s == null) return;
        Audience au = CustomNameplates.getAdventure().sender(Bukkit.getConsoleSender());
        au.sendMessage(getComponentFromMiniMessage(s));
    }

    public static void playerMessage(Player player, String s) {
        if (s == null) return;
        Audience au = CustomNameplates.getAdventure().player(player);
        au.sendMessage(getComponentFromMiniMessage(s));
    }

    public static void playerActionbar(Player player, Component component) {
        Audience au = CustomNameplates.getAdventure().player(player);
        au.sendActionBar(component);
    }

    public static String replaceLegacy(String legacy) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = legacy.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (isColorCode(chars[i])) {
                if (i + 1 < chars.length) {
                    switch (chars[i+1]) {
                        case '0' -> stringBuilder.append("<black>");
                        case '1' -> stringBuilder.append("<dark_blue>");
                        case '2' -> stringBuilder.append("<dark_green>");
                        case '3' -> stringBuilder.append("<dark_aqua>");
                        case '4' -> stringBuilder.append("<dark_red>");
                        case '5' -> stringBuilder.append("<dark_purple>");
                        case '6' -> stringBuilder.append("<gold>");
                        case '7' -> stringBuilder.append("<gray>");
                        case '8' -> stringBuilder.append("<dark_gray>");
                        case '9' -> stringBuilder.append("<blue>");
                        case 'a' -> stringBuilder.append("<green>");
                        case 'b' -> stringBuilder.append("<aqua>");
                        case 'c' -> stringBuilder.append("<red>");
                        case 'd' -> stringBuilder.append("<light_purple>");
                        case 'e' -> stringBuilder.append("<yellow>");
                        case 'f' -> stringBuilder.append("<white>");
                        case 'r' -> stringBuilder.append("<reset><!italic>");
                        case 'l' -> stringBuilder.append("<bold>");
                        case 'm' -> stringBuilder.append("<strikethrough>");
                        case 'o' -> stringBuilder.append("<italic>");
                        case 'n' -> stringBuilder.append("<underlined>");
                        case 'k' -> stringBuilder.append("<obfuscated>");
                        case 'x' -> {
                            if (i + 13 >= chars.length
                                    || !isColorCode(chars[i+2])
                                    || !isColorCode(chars[i+4])
                                    || !isColorCode(chars[i+6])
                                    || !isColorCode(chars[i+8])
                                    || !isColorCode(chars[i+10])
                                    || !isColorCode(chars[i+12])) {
                                stringBuilder.append(chars[i]);
                                continue;
                            }
                            stringBuilder
                                    .append("<#")
                                    .append(chars[i+3])
                                    .append(chars[i+5])
                                    .append(chars[i+7])
                                    .append(chars[i+9])
                                    .append(chars[i+11])
                                    .append(chars[i+13])
                                    .append(">");
                            i += 13;
                        }
                        default -> {
                            stringBuilder.append(chars[i]);
                            continue;
                        }
                    }
                    i++;
                } else {
                    stringBuilder.append(chars[i]);
                }
            } else {
                stringBuilder.append(chars[i]);
            }
        }
        return stringBuilder.toString();
    }

    private static boolean isColorCode(char c) {
        return c == '§' || c == '&';
    }
    
    public static int colorToDecimal(ChatColor color){
        switch (String.valueOf(color.getChar())){
            case "0" -> {return 0;}
            case "c" -> {return 16733525;}
            case "6" -> {return 16755200;}
            case "4" -> {return 11141120;}
            case "e" -> {return 16777045;}
            case "2" -> {return 43520;}
            case "a" -> {return 5635925;}
            case "b" -> {return 5636095;}
            case "3" -> {return 43690;}
            case "1" -> {return 170;}
            case "9" -> {return 5592575;}
            case "d" -> {return 16733695;}
            case "5" -> {return 11141290;}
            case "8" -> {return 5592405;}
            case "7" -> {return 11184810;}
            default -> {return 16777215;}
        }
    }

    public static String stripAllTags(String text) {
        return MiniMessage.miniMessage().stripTags(replaceLegacy(text));
    }

    public static String getMiniMessageFormat(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }
}
