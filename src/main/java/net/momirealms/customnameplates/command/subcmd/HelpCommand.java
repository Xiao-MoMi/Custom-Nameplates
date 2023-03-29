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

import net.momirealms.customnameplates.command.AbstractSubCommand;
import net.momirealms.customnameplates.utils.AdventureUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand extends AbstractSubCommand {

    public static final AbstractSubCommand INSTANCE = new HelpCommand();

    public HelpCommand() {
        super("help");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        AdventureUtils.sendMessage(sender, "<#3CB371>Command usage:");
        AdventureUtils.sendMessage(sender, "  <gray>├─<#FFFACD><Required Augument> ");
        AdventureUtils.sendMessage(sender, "  <gray>└─<#FFFACD><#E1FFFF>[Optional Augument]");
        AdventureUtils.sendMessage(sender, "<#3CB371>/nameplates");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>help");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>about");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>reload <#98FB98>Reload the plugin");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>list <#98FB98>Show a list of available nameplates");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>equip <#FFFACD><nameplate> <#98FB98>Equip a nameplate");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>forceequip <#FFFACD><player> <nameplate> <#98FB98>Force a player to equip a nameplate");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>unequip <#98FB98>Unequip the current nameplate");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>forceunequip <#FFFACD><player> <#98FB98>Force a player to unequip his nameplate");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>preview <#98FB98>Preview your current nameplate");
        AdventureUtils.sendMessage(sender, "  <gray>└─<white>forcepreview <player> <#E1FFFF>[nameplate] <#98FB98>Force a player to preview the nameplate");
        AdventureUtils.sendMessage(sender, "<#3CB371>/bubbles");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>list <#98FB98>Show a list of available bubbles");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>equip <#FFFACD><nameplate><#98FB98>Equip a bubble");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>forceequip <#FFFACD><player> <nameplate><#98FB98>Force a player to equip a bubble");
        AdventureUtils.sendMessage(sender, "  <gray>├─<white>unequip <#98FB98>Unequip the current bubble");
        AdventureUtils.sendMessage(sender, "  <gray>└─<white>forceunequip <#FFFACD><player> <#98FB98>Force a player to unequip his bubble");
        return true;
    }
}
