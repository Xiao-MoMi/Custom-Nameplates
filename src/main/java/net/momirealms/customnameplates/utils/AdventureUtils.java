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
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
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

    public static String replaceLegacy(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = str.replace("&","§").toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '§') {
                if (i + 1 < chars.length) {
                    switch (chars[i+1]) {
                        case '0' -> {i++;stringBuilder.append("<black>");}
                        case '1' -> {i++;stringBuilder.append("<dark_blue>");}
                        case '2' -> {i++;stringBuilder.append("<dark_green>");}
                        case '3' -> {i++;stringBuilder.append("<dark_aqua>");}
                        case '4' -> {i++;stringBuilder.append("<dark_red>");}
                        case '5' -> {i++;stringBuilder.append("<dark_purple>");}
                        case '6' -> {i++;stringBuilder.append("<gold>");}
                        case '7' -> {i++;stringBuilder.append("<gray>");}
                        case '8' -> {i++;stringBuilder.append("<dark_gray>");}
                        case '9' -> {i++;stringBuilder.append("<blue>");}
                        case 'a' -> {i++;stringBuilder.append("<green>");}
                        case 'b' -> {i++;stringBuilder.append("<aqua>");}
                        case 'c' -> {i++;stringBuilder.append("<red>");}
                        case 'd' -> {i++;stringBuilder.append("<light_purple>");}
                        case 'e' -> {i++;stringBuilder.append("<yellow>");}
                        case 'f' -> {i++;stringBuilder.append("<white>");}
                        case 'r' -> {i++;stringBuilder.append("<reset><!italic>");}
                        case 'l' -> {i++;stringBuilder.append("<bold>");}
                        case 'm' -> {i++;stringBuilder.append("<strikethrough>");}
                        case 'o' -> {i++;stringBuilder.append("<italic>");}
                        case 'n' -> {i++;stringBuilder.append("<underlined>");}
                        case 'k' -> {i++;stringBuilder.append("<obfuscated>");}
                        case 'x' -> {stringBuilder.append("<#").append(chars[i+3]).append(chars[i+5]).append(chars[i+7]).append(chars[i+9]).append(chars[i+11]).append(chars[i+13]).append(">");i += 13;}
                    }
                }
            }
            else {
                stringBuilder.append(chars[i]);
            }
        }
        return stringBuilder.toString();
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
