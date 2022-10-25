package net.momirealms.customnameplates.commands.subcmd;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.commands.AbstractSubCommand;
import net.momirealms.customnameplates.commands.SubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.objects.nameplates.NameplatesTeam;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NameplatesEquipCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new NameplatesEquipCommand();

    public NameplatesEquipCommand() {
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

        if (sender.hasPermission("nameplates.equip." + args.get(0))) {

            if (CustomNameplates.plugin.getResourceManager().getNameplateConfig(args.get(0)) == null) {
                AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.np_not_exist);
                return true;
            }
            CustomNameplates.plugin.getDataManager().getPlayerData(player).equipNameplate(args.get(0));
            CustomNameplates.plugin.getDataManager().saveData(player);
            NameplatesTeam nameplatesTeam = CustomNameplates.plugin.getNameplateManager().getTeamManager().getNameplatesTeam(player);
            if (nameplatesTeam != null) nameplatesTeam.updateNameplates();
            CustomNameplates.plugin.getNameplateManager().getTeamManager().sendUpdateToAll(player, true);
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.np_equip.replace("{Nameplate}", CustomNameplates.plugin.getResourceManager().getNameplateConfig(args.get(0)).name()));
        }
        else {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.np_notAvailable);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        List<String> arrayList = new ArrayList<>();
        for (String cmd : availableNameplates(sender)) {
            if (cmd.startsWith(args.get(0)))
                arrayList.add(cmd);
        }
        return arrayList;
    }
}
