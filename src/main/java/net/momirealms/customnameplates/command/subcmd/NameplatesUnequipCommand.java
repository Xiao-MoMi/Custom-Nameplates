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

import net.momirealms.customnameplates.api.CustomNameplatesAPI;
import net.momirealms.customnameplates.command.AbstractSubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.utils.AdventureUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class NameplatesUnequipCommand extends AbstractSubCommand {

    public static final AbstractSubCommand INSTANCE = new NameplatesUnequipCommand();

    public NameplatesUnequipCommand() {
        super("unequip");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (noConsoleExecute(sender)) return true;
        Player player = (Player) sender;
        CustomNameplatesAPI.getInstance().unEquipNameplate(player);
        AdventureUtils.playerMessage(player, MessageManager.prefix + MessageManager.np_unEquip);
        return true;
    }
}
