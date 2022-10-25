package net.momirealms.customnameplates.commands.subcmd;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.commands.AbstractSubCommand;
import net.momirealms.customnameplates.commands.SubCommand;
import net.momirealms.customnameplates.manager.ConfigManager;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.objects.nameplates.NameplateConfig;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.utils.HoloUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ForcePreviewCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new ForcePreviewCommand();

    public ForcePreviewCommand() {
        super("forcepreview", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {

        if (!sender.hasPermission("nameplates.forcepreview")) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.noPerm);
            return true;
        }

        if (args.size() < 1) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.lackArgs);
            return true;
        }

        Player player = Bukkit.getPlayer(args.get(0));
        if (player == null) {
            AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.not_online.replace("{Player}",args.get(0)));
            return true;
        }

        long time = System.currentTimeMillis();
        if (time - (coolDown.getOrDefault(player, time - NameplateManager.preview * 1050)) < NameplateManager.preview * 1050) {
            AdventureUtil.playerMessage(player, MessageManager.prefix + MessageManager.coolDown);
            return true;
        }
        coolDown.put(player, time);

        if (NameplateManager.mode.equalsIgnoreCase("team")) {
            if (args.size() < 2) {
                AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.lackArgs);
                return true;
            }
            NameplateConfig nameplateConfig = CustomNameplates.plugin.getResourceManager().getNameplateConfig(args.get(1));
            if (nameplateConfig == null){
                AdventureUtil.sendMessage(sender, MessageManager.prefix + MessageManager.np_not_exist);
                return true;
            }
            String playerPrefix = NameplateManager.hidePrefix ? "" : CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(player, NameplateManager.player_prefix);
            String playerSuffix = NameplateManager.hideSuffix ? "" : CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(player, NameplateManager.player_suffix);
            Component prefix = Component.text(CustomNameplates.plugin.getNameplateManager().makeCustomNameplate(MiniMessage.miniMessage().stripTags(playerPrefix), args.get(0), MiniMessage.miniMessage().stripTags(playerSuffix), nameplateConfig)).font(ConfigManager.key).append(MiniMessage.miniMessage().deserialize(playerPrefix));
            Component suffix = MiniMessage.miniMessage().deserialize(playerSuffix).append(Component.text(CustomNameplates.plugin.getNameplateManager().getSuffixChar(MiniMessage.miniMessage().stripTags(playerPrefix) + args.get(0) + MiniMessage.miniMessage().stripTags(playerSuffix))).font(ConfigManager.key));
            Component full = prefix.append(Component.text(player.getName()).color(TextColor.color(color2decimal(nameplateConfig.color()))).font(Key.key("default")).append(suffix));
            HoloUtil.showHolo(full, player, (int) NameplateManager.preview);
        }
        else {
            showPlayerArmorStandTags(player);
        }

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
        if (args.size() == 2 && NameplateManager.mode.equalsIgnoreCase("team")) {
            List<String> arrayList = new ArrayList<>();
            for (String string : nameplates()) {
                if (string.startsWith(args.get(1)))
                    arrayList.add(string);
            }
            return arrayList;
        }
        return super.onTabComplete(sender, args);
    }
}
