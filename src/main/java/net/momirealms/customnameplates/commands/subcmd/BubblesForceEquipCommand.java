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

public class BubblesForceEquipCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new BubblesForceEquipCommand();

    public BubblesForceEquipCommand() {
        super("forceequip", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 2){
            AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.lackArgs);
            return true;
        }
        if (!sender.hasPermission("bubbles.forceequip")) {
            AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.noPerm);
            return true;
        }

        Player player = Bukkit.getPlayer(args.get(0));
        if (player == null) {
            return true;
        }

        if (CustomNameplates.plugin.getResourceManager().getBubbleConfig(args.get(1)) == null){
            AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.bb_not_exist);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.plugin, () -> {
            CustomNameplates.plugin.getDataManager().getPlayerData(player).setBubbles(args.get(0));
            CustomNameplates.plugin.getDataManager().saveData(player);

            AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.bb_force_equip.replace("{Bubble}", CustomNameplates.plugin.getResourceManager().getBubbleConfig(args.get(1)).name()).replace("{Player}", args.get(0)));
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
        if (args.size() == 2) {
            List<String> arrayList = new ArrayList<>();
            for (String bubble : bubbles()) {
                if (bubble.startsWith(args.get(1)))
                    arrayList.add(bubble);
            }
            return arrayList;
        }
        return super.onTabComplete(sender, args);
    }
}
