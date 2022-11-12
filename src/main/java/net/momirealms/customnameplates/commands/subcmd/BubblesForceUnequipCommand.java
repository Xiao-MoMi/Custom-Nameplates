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

public class BubblesForceUnequipCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new BubblesForceUnequipCommand();

    public BubblesForceUnequipCommand() {
        super("forceunequip", null);
    }


    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {

        if (!sender.hasPermission("bubbles.forceunequip")) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.noPerm);
            return true;
        }

        if (args.size() < 1){
            AdventureUtil.sendMessage(sender,MessageManager.prefix + MessageManager.lackArgs);
            return true;
        }

        Player player = Bukkit.getPlayer(args.get(0));
        if (player == null) {
            AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.not_online.replace("{Player}",args.get(0)));
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.plugin, () -> {
            CustomNameplates.plugin.getDataManager().getPlayerData(player).setBubbles("none");
            CustomNameplates.plugin.getDataManager().saveData(player);
            AdventureUtil.sendMessage(sender,MessageManager.prefix + MessageManager.bb_force_unEquip.replace("{Player}", args.get(0)));
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            List<String> arrayList = new ArrayList<>();
            for (String player : online_players()) {
                if (player.startsWith(args.get(0)))
                    arrayList.add(player);
            }
            return arrayList;
        }
        return super.onTabComplete(sender, args);
    }
}
