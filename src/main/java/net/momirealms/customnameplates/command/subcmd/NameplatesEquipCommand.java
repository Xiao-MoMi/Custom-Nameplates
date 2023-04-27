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

package net.momirealms.customnameplates.command.subcmd;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.api.CustomNameplatesAPI;
import net.momirealms.customnameplates.command.AbstractSubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.utils.AdventureUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class NameplatesEquipCommand extends AbstractSubCommand {

    public static final AbstractSubCommand INSTANCE = new NameplatesEquipCommand();

    public NameplatesEquipCommand() {
        super("equip");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (noConsoleExecute(sender) || lackArgs(sender, 1, args.size()) || notExist(sender, "nameplate", args.get(0))) return true;
        if (!sender.hasPermission("nameplates.equip." + args.get(0))) {
            AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.np_notAvailable);
            return true;
        }
        Player player = (Player) sender;
        CustomNameplatesAPI.getInstance().equipNameplate(player, args.get(0));
        AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.np_equip.replace("{Nameplate}", CustomNameplates.getInstance().getNameplateManager().getNameplateConfig(args.get(0)).display_name()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1 && sender instanceof Player player) {
            return filterStartingWith(CustomNameplates.getInstance().getNameplateManager().getAvailableNameplates(player), args.get(0));
        }
        return null;
    }
}
