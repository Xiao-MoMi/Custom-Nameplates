package net.momirealms.customnameplates.commands.subcmd;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.commands.AbstractSubCommand;
import net.momirealms.customnameplates.commands.SubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BubblesUnequipCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new BubblesUnequipCommand();

    public BubblesUnequipCommand() {
        super("unequip", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {

        if (!(sender instanceof Player player)) {
            AdventureUtil.consoleMessage(MessageManager.prefix + MessageManager.no_console);
            return true;
        }
        if (!(sender.hasPermission("bubbles.unequip"))) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.noPerm);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.plugin, () -> {
            CustomNameplates.plugin.getDataManager().getPlayerData(player).setBubbles("none");
            CustomNameplates.plugin.getDataManager().saveData(player);
            AdventureUtil.playerMessage(player, MessageManager.prefix + MessageManager.bb_unEquip);
        });

        return true;
    }
}
