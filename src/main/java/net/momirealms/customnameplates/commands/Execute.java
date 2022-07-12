package net.momirealms.customnameplates.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.sun.source.tree.BreakTree;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.AdventureManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.data.DataManager;
import net.momirealms.customnameplates.font.FontCache;
import net.momirealms.customnameplates.nameplates.NameplateUtil;
import net.momirealms.customnameplates.scoreboard.NameplatesTeam;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitScheduler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Execute implements CommandExecutor {

    private final CustomNameplates plugin;
    private final HashMap<Player, Long> coolDown = new HashMap<>();
    public static List<Entity> pCache = new ArrayList<>();

    public Execute(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //参数不足
        if (args.length < 1){
            if (sender instanceof Player){
                AdventureManager.playerMessage((Player) sender,ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
            }else {
                AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
            }
            return true;
        }
        switch (args[0]) {
            case "reload" -> {
                if (sender.hasPermission("customnameplates.reload") || sender.isOp()) {
                    ConfigManager.MainConfig.ReloadConfig();
                    ConfigManager.Message.ReloadConfig();
                    if (sender instanceof Player) {
                        AdventureManager.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.reload);
                    } else {
                        AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.reload);
                    }
                } else {
                    AdventureManager.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.noPerm);
                }
                return true;
            }
            case "equip" -> {
                if (sender instanceof Player player) {
                    if (args.length < 2) {
                        AdventureManager.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                        return true;
                    }
                    if (sender.hasPermission("customnameplates.equip." + args[1]) || sender.isOp()) {
                        if (plugin.getResourceManager().getNameplateInfo(args[1]) == null) {
                            AdventureManager.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.not_exist);
                            return true;
                        }
                        DataManager.cache.get(player.getUniqueId()).equipNameplate(args[1]);
                        this.plugin.getScoreBoardManager().getTeam(player.getName()).updateNameplates();
                        this.plugin.getDataManager().savePlayer(player.getUniqueId());
                        AdventureManager.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.equip.replace("{Nameplate}", plugin.getResourceManager().getNameplateInfo(args[1]).getConfig().getName()));
                    } else {
                        AdventureManager.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.notAvailable);
                    }
                } else {
                    AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.no_console);
                }
                return true;
            }
            case "forceequip" -> {
                if (args.length < 3){
                    if(sender instanceof Player){
                        AdventureManager.playerMessage((Player) sender,ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                    }else {
                        AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                    }
                    return true;
                }
                if (sender.hasPermission("customnameplates.forceequip") || sender.isOp()){
                    if (Bukkit.getPlayer(args[1]) != null){
                        Player player = Bukkit.getPlayer(args[1]);
                        //铭牌是否存在
                        if (plugin.getResourceManager().getNameplateInfo(args[2]) == null){
                            if(sender instanceof Player){
                                AdventureManager.playerMessage((Player) sender,ConfigManager.Message.prefix + ConfigManager.Message.not_exist);
                            }else {
                                AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.not_exist);
                            }
                            return true;
                        }
                        DataManager.cache.get(player.getUniqueId()).equipNameplate(args[2]);
                        this.plugin.getScoreBoardManager().getTeam(args[1]).updateNameplates();
                        this.plugin.getDataManager().savePlayer(player.getUniqueId());
                        if (sender instanceof Player){
                            AdventureManager.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.force_equip.replace("{Nameplate}", plugin.getResourceManager().getNameplateInfo(args[2]).getConfig().getName()).replace("{Player}", args[1]));
                        }else {
                            AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.force_equip.replace("{Nameplate}", plugin.getResourceManager().getNameplateInfo(args[2]).getConfig().getName()).replace("{Player}", args[1]));
                        }
                    }else {
                        //玩家不存在，不在线
                        if(sender instanceof Player){
                            AdventureManager.playerMessage((Player) sender,ConfigManager.Message.prefix + ConfigManager.Message.not_online.replace("{Player}",args[1]));
                        }else {
                            AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.not_online.replace("{Player}",args[1]));
                        }
                    }
                }else {
                    AdventureManager.playerMessage((Player) sender,ConfigManager.Message.prefix + ConfigManager.Message.noPerm);
                }
                return true;
            }
            case "unequip" -> {
                if (sender instanceof Player player){
                    DataManager.cache.get(player.getUniqueId()).equipNameplate("none");
                    this.plugin.getScoreBoardManager().getTeam(player.getName()).updateNameplates();
                    this.plugin.getDataManager().savePlayer(player.getUniqueId());
                    AdventureManager.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.unequip);
                }else {
                    AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.no_console);
                }
                return true;
            }
            case "forceunequip" -> {
                if (args.length < 2){
                    if(sender instanceof Player){
                        AdventureManager.playerMessage((Player) sender,ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                    }else {
                        AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                    }
                    return true;
                }
                if (sender.hasPermission("customnameplates.forceunequip")){
                    if (Bukkit.getPlayer(args[1]) != null){
                        Player player = Bukkit.getPlayer(args[1]);
                        DataManager.cache.get(player.getUniqueId()).equipNameplate("none");
                        this.plugin.getScoreBoardManager().getTeam(args[1]).updateNameplates();
                        this.plugin.getDataManager().savePlayer(player.getUniqueId());
                        if (sender instanceof Player){
                            AdventureManager.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.force_unequip.replace("{Player}", args[1]));
                        }else {
                            AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.force_unequip.replace("{Player}", args[1]));
                        }
                    }else {
                        //玩家不存在，不在线
                        if(sender instanceof Player){
                            AdventureManager.playerMessage((Player) sender,ConfigManager.Message.prefix + ConfigManager.Message.not_online.replace("{Player}",args[1]));
                        }else {
                            AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.not_online.replace("{Player}",args[1]));
                        }
                    }
                }
            }
            case "preview" -> {
                if (sender instanceof Player player){
                    if (player.hasPermission("customnameplates.preview") || player.isOp()){
                        //指令冷却
                        long time = System.currentTimeMillis();
                        //冷却时间判断
                        if (time - (coolDown.getOrDefault(player, time - ConfigManager.MainConfig.preview * 1050)) < ConfigManager.MainConfig.preview * 1050) {
                            AdventureManager.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.cooldown);
                            return true;
                        }
                        //重置冷却时间
                        coolDown.put(player, time);
                        AdventureManager.playerMessage(player,ConfigManager.Message.prefix + ConfigManager.Message.preview);
                        NameplatesTeam team = this.plugin.getScoreBoardManager().getOrCreateTeam(player);
                        Component full = team.getPrefix().append(Component.text(player.getName()).color(TextColor.color(color2decimal(team.getColor()))).font(Key.key("default")).append(team.getSuffix()));
                        showNameplate(player, full);
                    }else {
                        AdventureManager.playerMessage((Player) sender,ConfigManager.Message.prefix + ConfigManager.Message.noPerm);
                    }
                }else {
                    AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.no_console);
                }
            }
            case "forcepreview" -> {
                if (sender.hasPermission("customnameplates.forcepreview") || sender.isOp()) {
                    if (args.length < 3){
                        if(sender instanceof Player){
                            AdventureManager.playerMessage((Player) sender,ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                        }else {
                            AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.lackArgs);
                        }
                        return true;
                    }
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null){
                        if (sender instanceof Player){
                            AdventureManager.playerMessage((Player) sender, ConfigManager.Message.prefix + ConfigManager.Message.not_online);
                        }else {
                            AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.not_online);
                        }
                        return true;
                    }
                    FontCache fontCache = plugin.getResourceManager().getNameplateInfo(args[2]);
                    if (fontCache == null){
                        if(sender instanceof Player){
                            AdventureManager.playerMessage((Player) sender,ConfigManager.Message.prefix + ConfigManager.Message.not_exist);
                        }else {
                            AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.not_exist);
                        }
                        return true;
                    }
                    long time = System.currentTimeMillis();
                    if (time - (coolDown.getOrDefault(player, time - ConfigManager.MainConfig.preview * 1050)) < ConfigManager.MainConfig.preview * 1050) {
                        AdventureManager.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.cooldown);
                        return true;
                    }
                    coolDown.put(player, time);
                    NameplateUtil nameplateUtil = new NameplateUtil(fontCache);
                    String playerPrefix;
                    String playerSuffix;
                    if (plugin.getHookManager().hasPlaceholderAPI()) {
                        playerPrefix = this.plugin.getHookManager().parsePlaceholders(player, ConfigManager.MainConfig.player_prefix);
                        playerSuffix = this.plugin.getHookManager().parsePlaceholders(player, ConfigManager.MainConfig.player_suffix);
                    }else {
                        playerPrefix = ConfigManager.MainConfig.player_prefix;
                        playerSuffix = ConfigManager.MainConfig.player_suffix;
                    }
                    Component prefix = Component.text(nameplateUtil.makeCustomNameplate(playerPrefix, args[1], playerSuffix)).font(ConfigManager.MainConfig.key).append(Component.text(playerPrefix).font(Key.key("default")));
                    Component suffix = Component.text(playerSuffix).append(Component.text(nameplateUtil.getSuffixLength(playerPrefix + args[1] + playerSuffix)).font(ConfigManager.MainConfig.key));
                    Component full = prefix.append(Component.text(player.getName()).color(TextColor.color(color2decimal(nameplateUtil.getColor()))).font(Key.key("default")).append(suffix));
                    showNameplate(player, full);
                }
            }
            case "list" -> {
                if (sender instanceof Player player){
                    if (player.isOp()){
                        StringBuilder stringBuilder = new StringBuilder();
                        this.plugin.getResourceManager().caches.keySet().forEach(key ->{
                            if(key.equalsIgnoreCase("none")) return;
                            stringBuilder.append(key).append(" ");
                        });
                        AdventureManager.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.available.replace("{Nameplates}", stringBuilder.toString()));
                    }else if(player.hasPermission("customnameplates.list")){
                        StringBuilder stringBuilder = new StringBuilder();
                        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
                            String permission = info.getPermission().toLowerCase();
                            if (permission.startsWith("customnameplates.equip.")) {
                                permission = StringUtils.replace(permission, "customnameplates.equip.", "");
                                if (this.plugin.getResourceManager().caches.get(permission) != null){
                                    stringBuilder.append(permission).append(" ");
                                }
                            }
                        }
                        AdventureManager.playerMessage(player, ConfigManager.Message.prefix + ConfigManager.Message.available.replace("{Nameplates}", stringBuilder.toString()));
                    }else {
                        AdventureManager.playerMessage(player,ConfigManager.Message.prefix + ConfigManager.Message.noPerm);
                    }
                }else {
                    AdventureManager.consoleMessage(ConfigManager.Message.prefix + ConfigManager.Message.no_console);
                }
            }
            default -> {
                if(sender instanceof Player player){
                    if (player.hasPermission("customnameplates.help")){
                        AdventureManager.playerMessage(player,"<color:#87CEFA>/nameplates help - <color:#7FFFAA>show the command list");
                        AdventureManager.playerMessage(player,"<color:#87CEFA>/nameplates reload - <color:#7FFFAA>reload the configuration");
                        AdventureManager.playerMessage(player,"<color:#87CEFA>/nameplates equip <nameplate> - <color:#7FFFAA>equip a specified nameplate");
                        AdventureManager.playerMessage(player,"<color:#87CEFA>/nameplates forceequip <player> <nameplate> - <color:#7FFFAA>force a player to equip a specified nameplate");
                        AdventureManager.playerMessage(player,"<color:#87CEFA>/nameplates unequip - <color:#7FFFAA>unequip your nameplate");
                        AdventureManager.playerMessage(player,"<color:#87CEFA>/nameplates forceunequip - <color:#7FFFAA>force unequip a player's nameplate");
                        AdventureManager.playerMessage(player,"<color:#87CEFA>/nameplates preview - <color:#7FFFAA>preview your nameplate");
                        AdventureManager.playerMessage(player,"<color:#87CEFA>/nameplates forcepreview  <player> <nameplate> - <color:#7FFFAA>force a player to preview a nameplate");
                        AdventureManager.playerMessage(player,"<color:#87CEFA>/nameplates list - <color:#7FFFAA>list your available nameplates");
                    }
                }else {
                    AdventureManager.consoleMessage("<color:#87CEFA>/nameplates help - <color:#7FFFAA>show the command list");
                    AdventureManager.consoleMessage("<color:#87CEFA>/nameplates reload - <color:#7FFFAA>reload the configuration");
                    AdventureManager.consoleMessage("<color:#87CEFA>/nameplates equip <nameplate> - <color:#7FFFAA>equip a specified nameplate");
                    AdventureManager.consoleMessage("<color:#87CEFA>/nameplates forceequip <player> <nameplate> - <color:#7FFFAA>force a player to equip a specified nameplate");
                    AdventureManager.consoleMessage("<color:#87CEFA>/nameplates unequip - <color:#7FFFAA>unequip your nameplate");
                    AdventureManager.consoleMessage("<color:#87CEFA>/nameplates forceunequip - <color:#7FFFAA>force unequip a player's nameplate");
                    AdventureManager.consoleMessage("<color:#87CEFA>/nameplates preview - <color:#7FFFAA>preview your nameplate");
                    AdventureManager.consoleMessage("<color:#87CEFA>/nameplates forcepreview  <player> <nameplate> - <color:#7FFFAA>force a player to preview a nameplate");
                    AdventureManager.consoleMessage("<color:#87CEFA>/nameplates list - <color:#7FFFAA>list your available nameplates");
                }
                return true;
            }
        }
        return true;
    }

    private void showNameplate(Player player, Component component) {
        ArmorStand entity = player.getWorld().spawn(player.getLocation().add(0,0.8,0), ArmorStand.class, a -> {
            a.setInvisible(true);
            a.setCollidable(false);
            a.setInvulnerable(true);
            a.setVisible(false);
            a.setCustomNameVisible(false);
            a.setSmall(true);
            a.setGravity(false);
        });
        pCache.add(entity);

        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        wrappedDataWatcher.setEntity(entity);
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Boolean.class);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(component)).getHandle()));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer), true);
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        packetContainer.getIntegers().write(0, entity.getEntityId());
        packetContainer.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
        }
        catch (Exception e) {
            AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to preview for "+ player.getName()+"</red>");
            e.printStackTrace();
        }
        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
        for (int i = 1; i < ConfigManager.MainConfig.preview * 20; i++){
            bukkitScheduler.runTaskLater(CustomNameplates.instance,()-> entity.teleport(player.getLocation().add(0,0.8,0)), i);
        }
        bukkitScheduler.runTaskLater(CustomNameplates.instance, ()->{
            entity.remove();
            pCache.remove(entity);
        }, ConfigManager.MainConfig.preview * 20L);
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
