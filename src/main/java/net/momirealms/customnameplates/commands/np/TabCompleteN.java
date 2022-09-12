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

package net.momirealms.customnameplates.commands.np;

import net.momirealms.customnameplates.resource.ResourceManager;
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

public class TabCompleteN implements TabCompleter {

    @Override
    @ParametersAreNonnullByDefault
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if(1 == args.length){

            List<String> tab = new ArrayList<>();
            if (sender.hasPermission("nameplates.reload")) tab.add("reload");
            if (sender.hasPermission("nameplates.help")) tab.add("help");
            if (sender.hasPermission("nameplates.equip")) tab.add("equip");
            if (sender.hasPermission("nameplates.forceequip")) tab.add("forceequip");
            if (sender.hasPermission("nameplates.unequip")) tab.add("unequip");
            if (sender.hasPermission("nameplates.forceunequip")) tab.add("forceunequip");
            if (sender.hasPermission("nameplates.forcepreview")) tab.add("forcepreview");
            if (sender.hasPermission("nameplates.preview")) tab.add("preview");
            if (sender.hasPermission("nameplates.list")) tab.add("list");

            List<String> arrayList = new ArrayList<>();
            for (String cmd : tab) {
                if (cmd.startsWith(args[0]))
                    arrayList.add(cmd);
            }
            return arrayList;
        }
        if(2 == args.length){
            if (args[0].equalsIgnoreCase("equip")){
                List<String> arrayList = new ArrayList<>();
                for (String cmd : availableNameplates(sender)) {
                    if (cmd.startsWith(args[1]))
                        arrayList.add(cmd);
                }
                return arrayList;
            }
            if (args[0].equalsIgnoreCase("forceunequip") && sender.hasPermission("nameplates.forceunequip")){
                List<String> arrayList = new ArrayList<>();
                for (String cmd : online_players()) {
                    if (cmd.startsWith(args[1]))
                        arrayList.add(cmd);
                }
                return arrayList;
            }
            if (args[0].equalsIgnoreCase("forceequip") && sender.hasPermission("nameplates.forceequip")){
                List<String> arrayList = new ArrayList<>();
                for (String cmd : online_players()) {
                    if (cmd.startsWith(args[1]))
                        arrayList.add(cmd);
                }
                return arrayList;
            }
            if (args[0].equalsIgnoreCase("forcepreview") && sender.hasPermission("nameplates.forcepreview")){
                List<String> arrayList = new ArrayList<>();
                for (String cmd : online_players()) {
                    if (cmd.startsWith(args[1]))
                        arrayList.add(cmd);
                }
                return arrayList;
            }
        }
        if(3 == args.length){
            if (args[0].equalsIgnoreCase("forceequip") && sender.hasPermission("nameplates.forceequip")){
                List<String> arrayList = new ArrayList<>();
                for (String cmd : nameplates()) {
                    if (cmd.startsWith(args[2]))
                        arrayList.add(cmd);
                }
                return arrayList;
            }
            if (args[0].equalsIgnoreCase("forcepreview") && sender.hasPermission("nameplates.forcepreview")){
                List<String> arrayList = new ArrayList<>();
                for (String cmd : nameplates()) {
                    if (cmd.startsWith(args[2]))
                        arrayList.add(cmd);
                }
                return arrayList;
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
                if (permission.startsWith("nameplates.equip.")) {
                    permission = StringUtils.replace(permission, "nameplates.equip.", "");
                    if (ResourceManager.NAMEPLATES.get(permission) != null){
                        availableNameplates.add(permission);
                    }
                }
            }
        }
        return availableNameplates;
    }

    private List<String> nameplates(){
        return new ArrayList<>(ResourceManager.NAMEPLATES.keySet());
    }
}
