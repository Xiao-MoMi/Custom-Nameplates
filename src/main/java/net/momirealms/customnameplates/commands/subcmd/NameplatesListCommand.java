package net.momirealms.customnameplates.commands.subcmd;

import net.momirealms.customnameplates.commands.AbstractSubCommand;
import net.momirealms.customnameplates.commands.SubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NameplatesListCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new NameplatesListCommand();

    public NameplatesListCommand() {
        super("list", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (!(sender.hasPermission("nameplates.list"))) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.noPerm);
            return true;
        }
        if (!(sender instanceof Player player)) {
            AdventureUtil.consoleMessage(MessageManager.prefix + MessageManager.no_console);
            return true;
        }

        List<String> availableNameplates = new ArrayList<>();
        getAvailableNameplates(player, availableNameplates);
        if (availableNameplates.size() != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < availableNameplates.size(); i++) {
                if (i != availableNameplates.size() - 1) stringBuilder.append(availableNameplates.get(i)).append(",");
                else stringBuilder.append(availableNameplates.get(i));
            }
            AdventureUtil.playerMessage(player, MessageManager.prefix + MessageManager.np_available.replace("{Nameplates}", stringBuilder.toString()));
        }
        else {
            AdventureUtil.playerMessage(player, MessageManager.prefix + MessageManager.np_haveNone);
        }
        return true;
    }
}
