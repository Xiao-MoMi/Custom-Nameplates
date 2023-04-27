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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BubblesForceEquipCommand extends AbstractSubCommand {

    public static final AbstractSubCommand INSTANCE = new BubblesForceEquipCommand();

    public BubblesForceEquipCommand() {
        super("forceequip");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (lackArgs(sender, 2, args.size()) || playerNotOnline(sender, args.get(0)) || notExist(sender, "bubble", args.get(1)))
            return true;

        Player player = Bukkit.getPlayer(args.get(0));
        CustomNameplatesAPI.getInstance().equipBubble(player, args.get(1));
        AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.bb_force_equip.replace("{Bubble}", CustomNameplates.getInstance().getChatBubblesManager().getBubbleConfig(args.get(1)).display_name()).replace("{Player}", args.get(0)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            return filterStartingWith(online_players(), args.get(0));
        }
        if (args.size() == 2) {
            return filterStartingWith(allBubbles(), args.get(1));
        }
        return null;
    }
}
