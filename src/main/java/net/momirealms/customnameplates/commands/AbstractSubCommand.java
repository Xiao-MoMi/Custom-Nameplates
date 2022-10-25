/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.commands;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.manager.ResourceManager;
import net.momirealms.customnameplates.objects.nameplates.ArmorStandManager;
import net.momirealms.customnameplates.objects.nameplates.NameplatesTeam;
import net.momirealms.customnameplates.objects.nameplates.mode.EntityTag;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSubCommand implements SubCommand {

    private final String command;
    private Map<String, SubCommand> subCommandMap;
    protected HashMap<Player, Long> coolDown = new HashMap<>();

    public AbstractSubCommand(String command, Map<String, SubCommand> subCommandMap) {
        this.command = command;
        this.subCommandMap = subCommandMap;
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (subCommandMap == null || args.size() < 1) {
            return true;
        }
        SubCommand subCommand = subCommandMap.get(args.get(0));
        if (subCommand == null) {
            AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.unavailableArgs);
        } else {
            subCommand.onCommand(sender, args.subList(1, args.size()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (subCommandMap == null)
            return Collections.singletonList("");
        if (args.size() <= 1) {
            List<String> returnList = new ArrayList<>(subCommandMap.keySet());
            returnList.removeIf(str -> !str.startsWith(args.get(0)));
            return returnList;
        }
        SubCommand subCmd = subCommandMap.get(args.get(0));
        if (subCmd != null)
            return subCommandMap.get(args.get(0)).onTabComplete(sender, args.subList(1, args.size()));
        return Collections.singletonList("");
    }

    @Override
    public String getSubCommand() {
        return command;
    }

    @Override
    public Map<String, SubCommand> getSubCommands() {
        return Collections.unmodifiableMap(subCommandMap);
    }

    @Override
    public void regSubCommand(SubCommand command) {
        if (subCommandMap == null) {
            subCommandMap = new ConcurrentHashMap<>();
        }
        subCommandMap.put(command.getSubCommand(), command);
    }

    protected List<String> availableNameplates(CommandSender sender){
        List<String> availableNameplates = new ArrayList<>();
        if (sender instanceof Player player){
            getAvailableNameplates(player, availableNameplates);
        }
        return availableNameplates;
    }

    protected List<String> availableBubbles(CommandSender sender){
        List<String> availableBubbles = new ArrayList<>();
        if (sender instanceof Player player){
            getAvailableBubbles(player, availableBubbles);
        }
        return availableBubbles;
    }

    protected List<String> online_players(){
        List<String> online = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach((player -> online.add(player.getName())));
        return online;
    }

    protected List<String> nameplates(){
        return new ArrayList<>(ResourceManager.NAMEPLATES.keySet());
    }

    protected List<String> bubbles(){
        return new ArrayList<>(ResourceManager.BUBBLES.keySet());
    }

    protected void unequipNameplate(Player player) {
        CustomNameplates.plugin.getDataManager().getPlayerData(player).equipNameplate("none");
        CustomNameplates.plugin.getDataManager().saveData(player);
        NameplatesTeam nameplatesTeam = CustomNameplates.plugin.getNameplateManager().getTeamManager().getNameplatesTeam(player);
        if (nameplatesTeam != null) nameplatesTeam.updateNameplates();
        CustomNameplates.plugin.getNameplateManager().getTeamManager().sendUpdateToAll(player, true);
    }

    protected void showPlayerArmorStandTags(Player player) {
        EntityTag entityTag = (EntityTag) CustomNameplates.plugin.getNameplateManager().getNameplateMode();
        ArmorStandManager asm = entityTag.getArmorStandManager(player);
        asm.spawn(player);
        for (int i = 0; i < NameplateManager.preview * 20; i++) {
            Bukkit.getScheduler().runTaskLater(CustomNameplates.plugin, ()-> {
                asm.teleport(player);
            },i);
        }
        Bukkit.getScheduler().runTaskLater(CustomNameplates.plugin, ()-> {
            asm.destroy(player);
        },NameplateManager.preview * 20);
    }

    protected void getAvailableNameplates(Player player, List<String> availableNameplates) {
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String permission = info.getPermission().toLowerCase();
            if (permission.startsWith("nameplates.equip.")) {
                permission = permission.substring(17);
                if (ResourceManager.NAMEPLATES.get(permission) != null) {
                    availableNameplates.add(permission);
                }
            }
        }
    }

    protected void getAvailableBubbles(Player player, List<String> availableBubbles) {
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String permission = info.getPermission().toLowerCase();
            if (permission.startsWith("bubbles.equip.")) {
                permission = permission.substring(14);
                if (ResourceManager.BUBBLES.get(permission) != null) {
                    availableBubbles.add(permission);
                }
            }
        }
    }

    protected int color2decimal(ChatColor color){
        switch (String.valueOf(color.getChar())){
            case "0" -> {
                return 0;
            }
            case "c" -> {
                return 16733525;
            }
            case "6" -> {
                return 16755200;
            }
            case "4" -> {
                return 11141120;
            }
            case "e" -> {
                return 16777045;
            }
            case "2" -> {
                return 43520;
            }
            case "a" -> {
                return 5635925;
            }
            case "b" -> {
                return 5636095;
            }
            case "3" -> {
                return 43690;
            }
            case "1" -> {
                return 170;
            }
            case "9" -> {
                return 5592575;
            }
            case "d" -> {
                return 16733695;
            }
            case "5" -> {
                return 11141290;
            }
            case "8" -> {
                return 5592405;
            }
            case "7" -> {
                return 11184810;
            }
            default -> {
                return 16777215;
            }
        }
    }

    public void setSubCommandMap(Map<String, SubCommand> subCommandMap) {
        this.subCommandMap = subCommandMap;
    }
}
