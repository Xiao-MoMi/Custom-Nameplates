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

package net.momirealms.customnameplates.commands;

import net.momirealms.customnameplates.CustomNameplates;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    private final CustomNameplates plugin;

    public TabComplete(CustomNameplates plugin){
        this.plugin = plugin;
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if(1 == args.length){
            List<String> tab = new ArrayList<>();
            if (sender.hasPermission("customnameplates.reload")) tab.add("reload");
            if (sender.hasPermission("customnameplates.help")) tab.add("help");
            if (sender.hasPermission("customnameplates.equip")) tab.add("equip");
            if (sender.hasPermission("customnameplates.forceequip")) tab.add("forceequip");
            if (sender.hasPermission("customnameplates.unequip")) tab.add("unequip");
            if (sender.hasPermission("customnameplates.forceunequip")) tab.add("forceunequip");
            if (sender.hasPermission("customnameplates.forcepreview")) tab.add("forcepreview");
            if (sender.hasPermission("customnameplates.preview")) tab.add("preview");
            if (sender.hasPermission("customnameplates.list")) tab.add("list");
            if (sender.hasPermission("customnameplates.generate")) tab.add("generate");
            return tab;
        }
        if(2 == args.length){
            if (args[0].equalsIgnoreCase("equip")){
                return availableNameplates(sender);
            }
            if (args[0].equalsIgnoreCase("forceunequip") && sender.hasPermission("customnameplates.forceunequip")){
                return online_players();
            }
            if (args[0].equalsIgnoreCase("forceequip") && sender.hasPermission("customnameplates.forceequip")){
                return online_players();
            }
            if (args[0].equalsIgnoreCase("forcepreview") && sender.hasPermission("customnameplates.forcepreview")){
                return online_players();
            }
        }
        if(3 == args.length){
            if (args[0].equalsIgnoreCase("forceequip") && sender.hasPermission("customnameplates.forceequip")){
                return nameplates();
            }
            if (args[0].equalsIgnoreCase("forcepreview") && sender.hasPermission("customnameplates.forcepreview")){
                return nameplates();
            }
        }
        return null;
    }

    private static List<String> online_players(){
        List<String> online = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach((player -> online.add(player.getName())));
        return online;
    }

    private List<String> availableNameplates(CommandSender sender){
        List<String> availableNameplates = new ArrayList<>();
        if (sender instanceof Player player){
            for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
                String permission = info.getPermission().toLowerCase();
                if (permission.startsWith("customnameplates.equip.")) {
                    permission = StringUtils.replace(permission, "customnameplates.equip.", "");
                    if (this.plugin.getResourceManager().caches.get(permission) != null){
                        availableNameplates.add(permission);
                    }
                }
            }
        }
        return availableNameplates;
    }

    private List<String> nameplates(){
        return new ArrayList<>(this.plugin.getResourceManager().caches.keySet());
    }
}
