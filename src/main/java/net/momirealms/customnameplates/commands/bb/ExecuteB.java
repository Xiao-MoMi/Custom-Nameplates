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

package net.momirealms.customnameplates.commands.bb;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.TeamManager;
import net.momirealms.customnameplates.resource.ResourceManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExecuteB implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 1){
            if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
            else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
            return true;
        }

        switch (args[0]) {
            case "equip" -> {
                if (sender instanceof Player player) {
                    if (args.length < 2) {
                        AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                        return true;
                    }
                    if (sender.hasPermission("bubbles.equip." + args[1]) || sender.isOp()) {
                        if (CustomNameplates.instance.getResourceManager().getBubbleConfig(args[1]) == null) {
                            AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.bb_not_exist);
                            return true;
                        }
                        CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId()).setBubbles(args[1]);
                        CustomNameplates.instance.getDataManager().savePlayer(player.getUniqueId());
                        AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.bb_equip.replace("{Bubble}", CustomNameplates.instance.getResourceManager().getBubbleConfig(args[1]).name()));
                    }
                    else AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.bb_notAvailable);
                }
                else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.no_console);
                return true;
            }
            case "forceequip" -> {
                if (args.length < 3){
                    if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                    else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                    return true;
                }
                if (sender.hasPermission("bubbles.forceequip") || sender.isOp()) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player != null){
                        if (CustomNameplates.instance.getResourceManager().getBubbleConfig(args[2]) == null){
                            if(sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.bb_not_exist);
                            else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.bb_not_exist);
                            return true;
                        }
                        CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId()).setBubbles(args[2]);
                        CustomNameplates.instance.getDataManager().savePlayer(player.getUniqueId());
                        if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.bb_force_equip.replace("{Bubble}", CustomNameplates.instance.getResourceManager().getBubbleConfig(args[2]).name()).replace("{Player}", args[1]));
                        else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.bb_force_equip.replace("{Bubble}", CustomNameplates.instance.getResourceManager().getBubbleConfig(args[2]).name()).replace("{Player}", args[1]));
                    }
                    else {
                        if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.not_online.replace("{Player}",args[1]));
                        else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.not_online.replace("{Player}",args[1]));
                    }
                }
                else AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.noPerm);
                return true;
            }
            case "unequip" -> {
                if (sender instanceof Player player){
                    CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId()).setBubbles("none");
                    CustomNameplates.instance.getDataManager().savePlayer(player.getUniqueId());
                    AdventureUtil.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.bb_unEquip);
                }
                else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.no_console);
                return true;
            }
            case "forceunequip" -> {
                if (args.length < 2){
                    if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                    else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                    return true;
                }
                if (sender.hasPermission("bubbles.forceunequip")){
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player != null){
                        CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId()).setBubbles("none");
                        CustomNameplates.instance.getDataManager().savePlayer(player.getUniqueId());
                        if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.bb_force_unEquip.replace("{Player}", args[1]));
                        else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.bb_force_unEquip.replace("{Player}", args[1]));
                    }else {
                        if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.not_online.replace("{Player}",args[1]));
                        else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.not_online.replace("{Player}",args[1]));
                    }
                }
                return true;
            }
            case "list" -> {
                if (sender instanceof Player player) {
                    if (player.isOp()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        ResourceManager.BUBBLES.keySet().forEach(key -> {
                            if (key.equalsIgnoreCase("none")) return;
                            stringBuilder.append(key).append(" ");
                        });
                        AdventureUtil.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.bb_available.replace("{Bubbles}", stringBuilder.toString()));
                    }
                    else if (player.hasPermission("bubbles.list")) {
                        List<String> availableBubbles = new ArrayList<>();
                        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
                            String permission = info.getPermission().toLowerCase();
                            if (permission.startsWith("bubbles.equip.")) {
                                permission = StringUtils.replace(permission, "bubbles.equip.", "");
                                if (ResourceManager.BUBBLES.get(permission) != null) {
                                    availableBubbles.add(permission);
                                }
                            }
                        }
                        if (availableBubbles.size() != 0) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (String str : availableBubbles) {
                                stringBuilder.append(str).append(" ");
                            }
                            AdventureUtil.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.bb_available.replace("{Bubbles}", stringBuilder.toString()));
                        }
                        else {
                            AdventureUtil.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.bb_haveNone);
                        }
                    }
                    else AdventureUtil.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.noPerm);
                }
                else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.no_console);
                return true;
            }
            default -> {
                if (sender instanceof Player player){
                    if (player.hasPermission("bubbles.help")){
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/bubbles help - <color:#7FFFAA>show the command list");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/bubbles equip <nameplate> - <color:#7FFFAA>equip a specified bubble");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/bubbles forceequip <player> <nameplate> - <color:#7FFFAA>force a player to equip a specified bubble");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/bubbles unequip - <color:#7FFFAA>unequip your bubble");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/bubbles forceunequip - <color:#7FFFAA>force unequip a player's bubble");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/bubbles list - <color:#7FFFAA>list your available bubbles");
                    }
                }
                else {
                    AdventureUtil.consoleMessage("<color:#87CEFA>/bubbles help - <color:#7FFFAA>show the command list");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/bubbles equip <nameplate> - <color:#7FFFAA>equip a specified bubble");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/bubbles forceequip <player> <nameplate> - <color:#7FFFAA>force a player to equip a specified bubble");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/bubbles unequip - <color:#7FFFAA>unequip your bubble");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/bubbles forceunequip - <color:#7FFFAA>force unequip a player's bubble");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/bubbles list - <color:#7FFFAA>list your available bubbles");
                }
                return true;
            }
        }
    }
}
