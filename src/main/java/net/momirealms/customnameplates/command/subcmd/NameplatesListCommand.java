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
import net.momirealms.customnameplates.command.AbstractSubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.utils.AdventureUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.StringJoiner;

public class NameplatesListCommand extends AbstractSubCommand {

    public static final AbstractSubCommand INSTANCE = new NameplatesListCommand();

    public NameplatesListCommand() {
        super("list");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (noConsoleExecute(sender)) return true;
        Player player = (Player) sender;
        List<String> availableNameplates = CustomNameplates.getInstance().getNameplateManager().getAvailableNameplates(player);
        if (availableNameplates.size() != 0) {
            StringJoiner stringJoiner = new StringJoiner(", ");
            for (String availableNameplate : availableNameplates) {
                stringJoiner.add(availableNameplate);
            }
            AdventureUtils.playerMessage(player, MessageManager.prefix + MessageManager.np_available.replace("{Nameplates}", stringJoiner.toString()));
        } else {
            AdventureUtils.playerMessage(player, MessageManager.prefix + MessageManager.np_haveNone);
        }
        return true;
    }
}
