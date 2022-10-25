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

import net.momirealms.customnameplates.commands.subcmd.*;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NameplateCommand implements TabExecutor {

    private final Map<String, SubCommand> subCommandMap;

    public NameplateCommand() {
        subCommandMap = new ConcurrentHashMap<>();
        regDefaultSubCommands();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> argList = Arrays.asList(args);
        if (argList.size() < 1) {
            AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.nonArgs);
            return true;
        }
        SubCommand subCommand = subCommandMap.get(argList.get(0));
        if (subCommand != null)
            return subCommand.onCommand(sender, argList.subList(1, argList.size()));
        else {
            AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.unavailableArgs);
            return true;
        }
    }

    private void regDefaultSubCommands() {
        regSubCommand(ReloadCommand.INSTANCE);
        regSubCommand(NameplatesEquipCommand.INSTANCE);
        regSubCommand(NameplatesForceEquipCommand.INSTANCE);
        regSubCommand(NameplatesUnequipCommand.INSTANCE);
        regSubCommand(NameplatesForceUnequipCommand.INSTANCE);
        regSubCommand(PreviewCommand.INSTANCE);
        regSubCommand(ForcePreviewCommand.INSTANCE);
        regSubCommand(NameplatesListCommand.INSTANCE);
        regSubCommand(HelpCommand.INSTANCE);
    }

    public void regSubCommand(SubCommand executor) {
        subCommandMap.put(executor.getSubCommand(), executor);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> argList = Arrays.asList(args);
        if (argList.size() <= 1) {
            List<String> returnList = new ArrayList<>(subCommandMap.keySet());
            returnList.removeIf(str -> !str.startsWith(args[0]) || !sender.hasPermission("nameplates." + str));
            return returnList;
        }
        SubCommand subCommand = subCommandMap.get(argList.get(0));
        if (subCommand != null)
            return subCommand.onTabComplete(sender, argList.subList(1, argList.size()));
        else
            return Collections.singletonList("");
    }

    public Map<String, SubCommand> getSubCommandMap() {
        return subCommandMap;
    }
}
