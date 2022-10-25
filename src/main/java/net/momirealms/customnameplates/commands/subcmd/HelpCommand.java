package net.momirealms.customnameplates.commands.subcmd;

import net.momirealms.customnameplates.commands.AbstractSubCommand;
import net.momirealms.customnameplates.commands.SubCommand;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new HelpCommand();

    public HelpCommand() {
        super("help", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (sender instanceof Player player) {
            if (player.hasPermission("nameplates.help")){
                AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates help - <color:#7FFFAA>show the command list");
                AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates reload - <color:#7FFFAA>reload the configuration");
                AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates equip <nameplate> - <color:#7FFFAA>equip a specified nameplate");
                AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates forceequip <player> <nameplate> - <color:#7FFFAA>force a player to equip a specified nameplate");
                AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates unequip - <color:#7FFFAA>unequip your nameplate");
                AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates forceunequip - <color:#7FFFAA>force unequip a player's nameplate");
                AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates preview - <color:#7FFFAA>preview your nameplate");
                AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates forcepreview  <player> <nameplate> - <color:#7FFFAA>force a player to preview a nameplate");
                AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates list - <color:#7FFFAA>list your available nameplates");
            }
        }
        else {
            AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates help - <color:#7FFFAA>show the command list");
            AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates reload - <color:#7FFFAA>reload the configuration");
            AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates equip <nameplate> - <color:#7FFFAA>equip a specified nameplate");
            AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates forceequip <player> <nameplate> - <color:#7FFFAA>force a player to equip a specified nameplate");
            AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates unequip - <color:#7FFFAA>unequip your nameplate");
            AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates forceunequip - <color:#7FFFAA>force unequip a player's nameplate");
            AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates preview - <color:#7FFFAA>preview your nameplate");
            AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates forcepreview  <player> <nameplate> - <color:#7FFFAA>force a player to preview a nameplate");
            AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates list - <color:#7FFFAA>list your available nameplates");
        }
        return true;
    }
}
