package net.momirealms.customnameplates.commands.subcmd;

import net.momirealms.customnameplates.commands.AbstractSubCommand;
import net.momirealms.customnameplates.commands.SubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super("reload", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {

        if (!(sender.hasPermission("nameplates.reload"))) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.noPerm);
            return true;
        }

        if (args.size() < 1) {
            long time1 = System.currentTimeMillis();
            ConfigUtil.reloadConfigs();
            AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.reload.replace("{time}", String.valueOf(System.currentTimeMillis() - time1)));
            return true;
        }
        return super.onCommand(sender, args);
    }
}
