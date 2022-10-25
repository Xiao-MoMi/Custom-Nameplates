package net.momirealms.customnameplates.commands.subcmd;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.commands.AbstractSubCommand;
import net.momirealms.customnameplates.commands.SubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.objects.nameplates.NameplatesTeam;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NameplatesForceEquipCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new NameplatesForceEquipCommand();

    public NameplatesForceEquipCommand() {
        super("forceequip", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {

        if (!(sender.hasPermission("nameplates.forceequip"))) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.noPerm);
            return true;
        }

        if (args.size() < 2){
            AdventureUtil.sendMessage(sender,MessageManager.prefix + MessageManager.lackArgs);
            return true;
        }

        Player player = Bukkit.getPlayer(args.get(0));
        if (player == null) {
            AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.not_online.replace("{Player}",args.get(0)));
            return true;
        }
        if (CustomNameplates.plugin.getResourceManager().getNameplateConfig(args.get(1)) == null){
            AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.np_not_exist);
            return true;
        }
        CustomNameplates.plugin.getDataManager().getPlayerData(player).equipNameplate(args.get(1));
        CustomNameplates.plugin.getDataManager().saveData(player);
        NameplatesTeam nameplatesTeam = CustomNameplates.plugin.getNameplateManager().getTeamManager().getNameplatesTeam(player);
        if (nameplatesTeam != null) nameplatesTeam.updateNameplates();
        CustomNameplates.plugin.getNameplateManager().getTeamManager().sendUpdateToAll(player, true);
        AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.np_force_equip.replace("{Nameplate}", CustomNameplates.plugin.getResourceManager().getNameplateConfig(args.get(1)).name()).replace("{Player}", args.get(0)));

        return super.onCommand(sender, args);
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
            for (String nameplate : nameplates()) {
                if (nameplate.startsWith(args.get(1)))
                    arrayList.add(nameplate);
            }
            return arrayList;
        }
        return super.onTabComplete(sender, args);
    }
}
