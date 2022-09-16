package net.momirealms.customnameplates.commands.bb;

import net.momirealms.customnameplates.resource.ResourceManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteB implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if(1 == args.length){

            List<String> tab = new ArrayList<>();
            if (sender.hasPermission("bubbles.help")) tab.add("help");
            if (sender.hasPermission("bubbles.equip")) tab.add("equip");
            if (sender.hasPermission("bubbles.forceequip")) tab.add("forceequip");
            if (sender.hasPermission("bubbles.unequip")) tab.add("unequip");
            if (sender.hasPermission("bubbles.forceunequip")) tab.add("forceunequip");
            if (sender.hasPermission("bubbles.list")) tab.add("list");

            List<String> arrayList = new ArrayList<>();
            for (String cmd : tab) {
                if (cmd.startsWith(args[0]))
                    arrayList.add(cmd);
            }
            return arrayList;
        }
        if(2 == args.length){
            if (args[0].equalsIgnoreCase("equip")){
                List<String> arrayList = new ArrayList<>();
                for (String cmd : availableBubbles(sender)) {
                    if (cmd.startsWith(args[1]))
                        arrayList.add(cmd);
                }
                return arrayList;
            }
            if (args[0].equalsIgnoreCase("forceunequip") && sender.hasPermission("bubbles.forceunequip")){
                List<String> arrayList = new ArrayList<>();
                for (String cmd : online_players()) {
                    if (cmd.startsWith(args[1]))
                        arrayList.add(cmd);
                }
                return arrayList;
            }
            if (args[0].equalsIgnoreCase("forceequip") && sender.hasPermission("bubbles.forceequip")){
                List<String> arrayList = new ArrayList<>();
                for (String cmd : online_players()) {
                    if (cmd.startsWith(args[1]))
                        arrayList.add(cmd);
                }
                return arrayList;
            }
        }
        if(3 == args.length){
            if (args[0].equalsIgnoreCase("forceequip") && sender.hasPermission("bubbles.forceequip")){
                List<String> arrayList = new ArrayList<>();
                for (String cmd : bubbles()) {
                    if (cmd.startsWith(args[2]))
                        arrayList.add(cmd);
                }
                return arrayList;
            }
        }
        return null;
    }

    private static List<String> online_players(){
        List<String> online = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach((player -> online.add(player.getName())));
        return online;
    }

    private List<String> availableBubbles(CommandSender sender){
        List<String> availableBubbles = new ArrayList<>();
        if (sender instanceof Player player){
            for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
                String permission = info.getPermission().toLowerCase();
                if (permission.startsWith("bubbles.equip.")) {
                    permission = StringUtils.replace(permission, "bubbles.equip.", "");
                    if (ResourceManager.BUBBLES.get(permission) != null){
                        availableBubbles.add(permission);
                    }
                }
            }
        }
        return availableBubbles;
    }

    private List<String> bubbles(){
        return new ArrayList<>(ResourceManager.BUBBLES.keySet());
    }
}
