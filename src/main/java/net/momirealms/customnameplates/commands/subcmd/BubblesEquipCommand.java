package net.momirealms.customnameplates.commands.subcmd;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.commands.AbstractSubCommand;
import net.momirealms.customnameplates.commands.SubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BubblesEquipCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new BubblesEquipCommand();

    public BubblesEquipCommand() {
        super("equip", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player player)) {
            AdventureUtil.consoleMessage(MessageManager.prefix + MessageManager.no_console);
            return true;
        }
        if (args.size() < 1) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.lackArgs);
            return true;
        }

        if (CustomNameplates.plugin.getResourceManager().getBubbleConfig(args.get(0)) == null) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.bb_not_exist);
            return true;
        }

        if (!sender.hasPermission("bubbles.equip." + args.get(0))) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.bb_notAvailable);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.plugin, () -> {
            CustomNameplates.plugin.getDataManager().getPlayerData(player).setBubbles(args.get(0));
            CustomNameplates.plugin.getDataManager().saveData(player);
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.bb_equip.replace("{Bubble}", CustomNameplates.plugin.getResourceManager().getBubbleConfig(args.get(0)).name()));
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        List<String> arrayList = new ArrayList<>();
        for (String bubble : availableBubbles(sender)) {
            if (bubble.startsWith(args.get(0)))
                arrayList.add(bubble);
        }
        return arrayList;
    }
}
