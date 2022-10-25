package net.momirealms.customnameplates.commands.subcmd;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.commands.AbstractSubCommand;
import net.momirealms.customnameplates.commands.SubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.objects.nameplates.NameplatesTeam;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.utils.HoloUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PreviewCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new PreviewCommand();
    public PreviewCommand() {
        super("preview", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player player)) {
            AdventureUtil.consoleMessage(MessageManager.prefix + MessageManager.no_console);
            return true;
        }
        if (!(sender.hasPermission("nameplates.preview"))) {
            AdventureUtil.playerMessage((Player) sender, MessageManager.prefix + MessageManager.noPerm);
            return true;
        }

        long time = System.currentTimeMillis();
        if (time - (coolDown.getOrDefault(player, time - NameplateManager.preview * 1050)) < NameplateManager.preview * 1050) {
            AdventureUtil.playerMessage(player, MessageManager.prefix + MessageManager.coolDown);
            return true;
        }
        coolDown.put(player, time);

        AdventureUtil.playerMessage(player, MessageManager.prefix +MessageManager.preview);
        if (NameplateManager.mode.equalsIgnoreCase("team")) {
            NameplatesTeam team = CustomNameplates.plugin.getNameplateManager().getTeamManager().getNameplatesTeam(player);
            if (team != null) {
                Component full = team.getPrefix().append(Component.text(player.getName()).color(TextColor.color(color2decimal(team.getColor()))).font(Key.key("default")).append(team.getSuffix()));
                HoloUtil.showHolo(full, player, (int) NameplateManager.preview);
            }
        }
        else {
            showPlayerArmorStandTags(player);
        }
        return true;
    }
}
