package net.momirealms.customnameplates.commands.subcmd;

import net.momirealms.customnameplates.commands.AbstractSubCommand;
import net.momirealms.customnameplates.commands.SubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class NameplatesUnequipCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new NameplatesUnequipCommand();

    public NameplatesUnequipCommand() {
        super("unequip", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {

        if (!(sender.hasPermission("nameplates.unequip"))) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.noPerm);
            return true;
        }

        if (!(sender instanceof Player player)) {
            AdventureUtil.consoleMessage(MessageManager.prefix + MessageManager.no_console);
            return true;
        }

        super.unequipNameplate(player);
        AdventureUtil.playerMessage(player, MessageManager.prefix + MessageManager.np_unEquip);
        return super.onCommand(sender, args);
    }
}
