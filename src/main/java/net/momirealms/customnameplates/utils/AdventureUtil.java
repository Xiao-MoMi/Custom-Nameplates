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
        Component parsed = mm.deserialize(s);
        au.sendMessage(parsed);
    }

    public static void playerMessage(Player player, String s){
        Audience au = CustomNameplates.adventure.player(player);
        MiniMessage mm = MiniMessage.miniMessage();
        Component parsed = mm.deserialize(s);
        au.sendMessage(parsed);
    }

    public static void playerActionbar(Player player, String s) {
        Audience au = CustomNameplates.adventure.player(player);
        MiniMessage mm = MiniMessage.miniMessage();
        au.sendActionBar(mm.deserialize(s));
    }
}
