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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.nameplates.mode.ArmorStandManager;
import net.momirealms.customnameplates.nameplates.mode.EntityTag;
import net.momirealms.customnameplates.nameplates.mode.rd.RidingTag;
import net.momirealms.customnameplates.resource.ResourceManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.NameplateInstance;
import net.momirealms.customnameplates.nameplates.NameplateUtil;
import net.momirealms.customnameplates.nameplates.NameplatesTeam;
import net.momirealms.customnameplates.nameplates.TeamManager;
import net.momirealms.customnameplates.utils.HoloUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

public class Execute implements CommandExecutor {

    private final HashMap<Player, Long> coolDown = new HashMap<>();

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 1){
            if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
            else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
            return true;
        }

        switch (args[0]) {
            //重载
            case "reload" -> {

                if (sender.hasPermission("nameplates.reload") || sender.isOp()) {

                    CustomNameplates.instance.loadConfig();

                    if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.reload);
                    else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.reload);
                }
                else AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.noPerm);
                return true;
            }
            //生成资源包
            case "generate" -> {
                if (sender.hasPermission("nameplates.generate") || sender.isOp()) {
                    ConfigManager.Main.reload();
                    CustomNameplates.instance.getResourceManager().generateResourcePack();
                    if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.generate);
                    else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.generate);
                }
                else AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.noPerm);
                return true;
            }
            //佩戴铭牌
            case "equip" -> {
                if (sender instanceof Player player) {

                    if (args.length < 2) {
                        AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                        return true;
                    }

                    if (sender.hasPermission("nameplates.equip." + args[1]) || sender.isOp()) {

                        if (CustomNameplates.instance.getResourceManager().getNameplateInstance(args[1]) == null) {
                            AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.not_exist);
                            return true;
                        }
                        CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId()).equipNameplate(args[1]);
                        CustomNameplates.instance.getDataManager().savePlayer(player.getUniqueId());
                        CustomNameplates.instance.getTeamManager().getTeams().get(TeamManager.getTeamName(player)).updateNameplates();
                        CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(player);
                        AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.equip.replace("{Nameplate}", CustomNameplates.instance.getResourceManager().getNameplateInstance(args[1]).getConfig().getName()));

                    }
                    else AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.notAvailable);
                }
                else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.no_console);

                return true;
            }
            //强制佩戴铭牌
            case "forceequip" -> {

                if (args.length < 3){
                    if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                    else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                    return true;
                }

                if (sender.hasPermission("nameplates.forceequip") || sender.isOp()){
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player != null){
                        if (CustomNameplates.instance.getResourceManager().getNameplateInstance(args[2]) == null){
                            if(sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.not_exist);
                            else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.not_exist);
                            return true;
                        }
                        CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId()).equipNameplate(args[2]);
                        CustomNameplates.instance.getDataManager().savePlayer(player.getUniqueId());
                        CustomNameplates.instance.getTeamManager().getTeams().get(TeamManager.getTeamName(player)).updateNameplates();
                        CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(player);
                        if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.force_equip.replace("{Nameplate}", CustomNameplates.instance.getResourceManager().getNameplateInstance(args[2]).getConfig().getName()).replace("{Player}", args[1]));
                        else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.force_equip.replace("{Nameplate}", CustomNameplates.instance.getResourceManager().getNameplateInstance(args[2]).getConfig().getName()).replace("{Player}", args[1]));
                    }else {
                        if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.not_online.replace("{Player}",args[1]));
                        else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.not_online.replace("{Player}",args[1]));
                    }
                }
                else AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.noPerm);

                return true;
            }
            //卸下铭牌
            case "unequip" -> {

                if (sender instanceof Player player){
                    CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId()).equipNameplate("none");
                    CustomNameplates.instance.getDataManager().savePlayer(player.getUniqueId());
                    CustomNameplates.instance.getTeamManager().getTeams().get(TeamManager.getTeamName(player)).updateNameplates();
                    CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(player);
                    AdventureUtil.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.unEquip);
                }
                else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.no_console);

                return true;
            }
            //强制卸下铭牌
            case "forceunequip" -> {

                if (args.length < 2){
                    if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                    else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                    return true;
                }

                if (sender.hasPermission("nameplates.forceunequip")){

                    Player player = Bukkit.getPlayer(args[1]);
                    if (player != null){
                        CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId()).equipNameplate("none");
                        CustomNameplates.instance.getDataManager().savePlayer(player.getUniqueId());
                        CustomNameplates.instance.getTeamManager().getTeams().get(TeamManager.getTeamName(player)).updateNameplates();
                        CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(player);
                        if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.force_unEquip.replace("{Player}", args[1]));
                        else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.force_unEquip.replace("{Player}", args[1]));
                    }else {
                        if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.not_online.replace("{Player}",args[1]));
                        else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.not_online.replace("{Player}",args[1]));
                    }
                }

                return true;
            }
            //预览铭牌
            case "preview" -> {

                if (sender instanceof Player player){
                    if (player.hasPermission("nameplates.preview") || player.isOp()){
                        //指令冷却
                        long time = System.currentTimeMillis();
                        //冷却时间判断
                        if (time - (coolDown.getOrDefault(player, time - ConfigManager.Nameplate.preview * 1050)) < ConfigManager.Nameplate.preview * 1050) {
                            AdventureUtil.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.coolDown);
                            return true;
                        }
                        //重置冷却时间
                        coolDown.put(player, time);
                        AdventureUtil.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.preview);
                        if (ConfigManager.Nameplate.mode.equalsIgnoreCase("team")) {
                            NameplatesTeam team = CustomNameplates.instance.getTeamManager().getTeams().get(TeamManager.getTeamName(player));
                            Component full = team.getPrefix().append(Component.text(player.getName()).color(TextColor.color(color2decimal(team.getColor()))).font(Key.key("default")).append(team.getSuffix()));
                            HoloUtil.showHolo(full, player, (int) ConfigManager.Nameplate.preview);
                        }
                        else {
                            EntityTag entityTag = (EntityTag) CustomNameplates.instance.getNameplateManager();
                            ArmorStandManager asm = entityTag.getArmorStandManager(player);
                            asm.spawn(player);
                            for (int i = 0; i < ConfigManager.Nameplate.preview * 20; i++) {
                                Bukkit.getScheduler().runTaskLater(CustomNameplates.instance, ()->{
                                    asm.teleport(player);
                                },i);
                            }
                            Bukkit.getScheduler().runTaskLater(CustomNameplates.instance, ()->{
                                asm.destroy(player);
                            },ConfigManager.Nameplate.preview * 20);
                        }
                    }else {
                        AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.noPerm);
                    }
                }
                else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.no_console);

                return true;
            }
            //强制预览铭牌
            case "forcepreview" -> {

                if (sender.hasPermission("nameplates.forcepreview") || sender.isOp()) {

                    if (args.length < 3){
                        if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                        else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                        return true;
                    }

                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null){
                        if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.not_online);
                        else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.not_online);
                        return true;
                    }
                    NameplateInstance nameplateInstance = CustomNameplates.instance.getResourceManager().getNameplateInstance(args[2]);
                    if (nameplateInstance == null){
                        if (sender instanceof Player) AdventureUtil.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.not_exist);
                        else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.not_exist);
                        return true;
                    }
                    long time = System.currentTimeMillis();
                    if (time - (coolDown.getOrDefault(player, time - ConfigManager.Nameplate.preview * 1050)) < ConfigManager.Nameplate.preview * 1050) {
                        AdventureUtil.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.coolDown);
                        return true;
                    }
                    coolDown.put(player, time);
                    if (ConfigManager.Nameplate.mode.equalsIgnoreCase("team")) {
                        String playerPrefix = ConfigManager.Nameplate.hidePrefix ? "" : ConfigManager.Main.placeholderAPI ? CustomNameplates.instance.getPlaceholderManager().parsePlaceholders(player, ConfigManager.Nameplate.player_prefix) : ConfigManager.Nameplate.player_prefix;
                        String playerSuffix = ConfigManager.Nameplate.hideSuffix ? "" : ConfigManager.Main.placeholderAPI ? CustomNameplates.instance.getPlaceholderManager().parsePlaceholders(player, ConfigManager.Nameplate.player_suffix) : ConfigManager.Nameplate.player_suffix;
                        Component prefix = Component.text(NameplateUtil.makeCustomNameplate(MiniMessage.miniMessage().stripTags(playerPrefix), args[1], MiniMessage.miniMessage().stripTags(playerSuffix), nameplateInstance)).font(ConfigManager.Main.key).append(MiniMessage.miniMessage().deserialize(playerPrefix));
                        Component suffix = MiniMessage.miniMessage().deserialize(playerSuffix).append(Component.text(NameplateUtil.getSuffixChar(MiniMessage.miniMessage().stripTags(playerPrefix) + args[1] + MiniMessage.miniMessage().stripTags(playerSuffix))).font(ConfigManager.Main.key));
                        Component full = prefix.append(Component.text(player.getName()).color(TextColor.color(color2decimal(nameplateInstance.getConfig().getColor()))).font(Key.key("default")).append(suffix));
                        HoloUtil.showHolo(full, player, (int) ConfigManager.Nameplate.preview);
                    }
                    else {
                        EntityTag entityTag = (EntityTag) CustomNameplates.instance.getNameplateManager();
                        ArmorStandManager asm = entityTag.getArmorStandManager(player);
                        asm.spawn(player);
                        for (int i = 0; i < ConfigManager.Nameplate.preview * 20; i++) {
                            Bukkit.getScheduler().runTaskLater(CustomNameplates.instance, ()->{
                                asm.teleport(player);
                            },i);
                        }
                        Bukkit.getScheduler().runTaskLater(CustomNameplates.instance, ()->{
                            asm.destroy(player);
                        },ConfigManager.Nameplate.preview * 20);
                    }
                }

                return true;
            }
            //显示可用铭牌
            case "list" -> {
                if (sender instanceof Player player) {
                    if (player.isOp()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        ResourceManager.NAMEPLATES.keySet().forEach(key -> {
                            if (key.equalsIgnoreCase("none")) return;
                            stringBuilder.append(key).append(" ");
                        });
                        AdventureUtil.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.available.replace("{Nameplates}", stringBuilder.toString()));
                    }
                    else if (player.hasPermission("nameplates.list")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
                            String permission = info.getPermission().toLowerCase();
                            if (permission.startsWith("nameplates.equip.")) {
                                permission = StringUtils.replace(permission, "nameplates.equip.", "");
                                if (ResourceManager.NAMEPLATES.get(permission) != null) {
                                    stringBuilder.append(permission).append(" ");
                                }
                            }
                        }
                        AdventureUtil.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.available.replace("{Nameplates}", stringBuilder.toString()));
                    }
                    else AdventureUtil.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.noPerm);
                }
                else AdventureUtil.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.no_console);
                return true;
            }
            //默认
            default -> {
                if (sender instanceof Player player){
                    if (player.hasPermission("nameplates.help")){
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates help - <color:#7FFFAA>show the command list");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates reload - <color:#7FFFAA>reload the configuration");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates equip <nameplate> - <color:#7FFFAA>equip a specified nameplate");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates forceequip <player> <nameplate> - <color:#7FFFAA>force a player to equip a specified nameplate");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates unequip - <color:#7FFFAA>unequip your nameplate");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates forceunequip - <color:#7FFFAA>force unequip a player's nameplate");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates preview - <color:#7FFFAA>preview your nameplate");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates forcepreview  <player> <nameplate> - <color:#7FFFAA>force a player to preview a nameplate");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates list - <color:#7FFFAA>list your available nameplates");
                        AdventureUtil.playerMessage(player,"<color:#87CEFA>/nameplates generate - <color:#7FFFAA>generate the resource pack");
                    }
                }
                else {
                    AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates help - <color:#7FFFAA>show the command list");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates reload - <color:#7FFFAA>reload the configuration");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates equip <nameplate> - <color:#7FFFAA>equip a specified nameplate");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates forceequip <player> <nameplate> - <color:#7FFFAA>force a player to equip a specified nameplate");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates unequip - <color:#7FFFAA>unequip your nameplate");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates forceunequip - <color:#7FFFAA>force unequip a player's nameplate");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates preview - <color:#7FFFAA>preview your nameplate");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates forcepreview  <player> <nameplate> - <color:#7FFFAA>force a player to preview a nameplate");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates list - <color:#7FFFAA>list your available nameplates");
                    AdventureUtil.consoleMessage("<color:#87CEFA>/nameplates generate - <color:#7FFFAA>generate the resource pack");
                }
                return true;
            }
        }
    }

    private int color2decimal(ChatColor color){
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
}