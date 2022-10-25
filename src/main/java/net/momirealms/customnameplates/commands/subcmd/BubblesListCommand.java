package net.momirealms.customnameplates.commands.subcmd;

import net.momirealms.customnameplates.commands.AbstractSubCommand;
import net.momirealms.customnameplates.commands.SubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BubblesListCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new BubblesListCommand();

    public BubblesListCommand() {
        super("list", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {

        if (!(sender.hasPermission("bubbles.list"))) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.noPerm);
            return true;
        }

        if (!(sender instanceof Player player)) {
            AdventureUtil.consoleMessage(MessageManager.prefix + MessageManager.no_console);
            return true;
        }

        List<String> availableBubbles = new ArrayList<>();
        getAvailableBubbles(player, availableBubbles);
        if (availableBubbles.size() != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < availableBubbles.size(); i++) {
                if (i != availableBubbles.size() - 1) stringBuilder.append(availableBubbles.get(i)).append(",");
                else stringBuilder.append(availableBubbles.get(i));
            }
            AdventureUtil.playerMessage(player, MessageManager.prefix + MessageManager.bb_available.replace("{Bubbles}", stringBuilder.toString()));
        }
        else {
            AdventureUtil.playerMessage(player, MessageManager.prefix + MessageManager.bb_haveNone);
        }
        return true;
    }
}
