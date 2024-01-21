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

package net.momirealms.customnameplates.paper.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.mechanic.tag.NameplatePlayer;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.CustomNameplatesPluginImpl;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.customnameplates.paper.setting.CNLocale;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class CommandManager {

    private final CustomNameplatesPluginImpl plugin;

    public CommandManager(CustomNameplatesPluginImpl plugin) {
        this.plugin = plugin;
        if (!CommandAPI.isLoaded())
            CommandAPI.onLoad(new CommandAPIBukkitConfig(plugin).silentLogs(true));
    }

    public void load() {
        new CommandAPICommand("customnameplates")
                .withAliases("nameplates", "cnameplates")
                .withSubcommands(
                        getReloadCommand(),
                        getAboutCommand(),
                        getEquipCommand(),
                        getUnEquipCommand(),
                        getPreviewCommand()
                )
                .register();
    }

    private CommandAPICommand getPreviewCommand() {
        return new CommandAPICommand("preview")
                .executesPlayer((player, args) -> {
                    NameplatePlayer nameplatePlayer = plugin.getNameplateManager().getNameplatePlayer(player.getUniqueId());
                    if (nameplatePlayer == null) {
                        LogUtils.warn(player.getName() + " failed to preview because no tag is created");
                        return;
                    }
                    if (nameplatePlayer.isPreviewing()) {
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_PREVIEW_COOLDOWN);
                        return;
                    }
                    nameplatePlayer.setPreview(true);
                    AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_PREVIEW_START);
                    plugin.getScheduler().runTaskAsyncLater(() -> {
                        nameplatePlayer.setPreview(false);
                    }, plugin.getNameplateManager().getPreviewDuration(), TimeUnit.SECONDS);
                });
    }

    private CommandAPICommand getEquipCommand() {
        return new CommandAPICommand("equip")
                .withPermission("nameplates.equip")
                .withArguments(new StringArgument("nameplate").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> plugin.getNameplateManager().getAvailableNameplates((Player) commandSenderSuggestionInfo.sender()).toArray(new String[0]))))
                .executesPlayer((player, args) -> {
                    String nameplate = (String) args.get("nameplate");
                    if (!plugin.getNameplateManager().hasNameplate(player, nameplate)) {
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_NAMEPLATE_NOT_AVAILABLE);
                        return;
                    }

                    if (!plugin.getNameplateManager().equipNameplate(player, nameplate)) {
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_NAMEPLATE_NOT_EXISTS);
                        return;
                    }

                    AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_EQUIP_NAMEPLATE);
                });
    }

    private CommandAPICommand getUnEquipCommand() {
        return new CommandAPICommand("unequip")
                .withPermission("nameplates.unequip")
                .executesPlayer((player, args) -> {
                    plugin.getNameplateManager().unEquipNameplate(player);
                    AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_UNEQUIP_NAMEPLATE);
                });
    }

    private CommandAPICommand getReloadCommand() {
        return new CommandAPICommand("reload")
                .withPermission("nameplates.admin")
                .executes((sender, args) -> {
                    long time = System.currentTimeMillis();
                    plugin.reload(true);
                    AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_RELOAD.replace("{time}", String.valueOf(System.currentTimeMillis()-time)));
                });
    }

    private CommandAPICommand getAboutCommand() {
        return new CommandAPICommand("about")
                .withPermission("nameplates.about")
                .executes((sender, args) -> {
            AdventureManagerImpl.getInstance().sendMessage(sender, "<#3CB371>⚓ CustomNameplates <gray>- <#98FB98>" + CustomNameplatesPlugin.getInstance().getVersionManager().getPluginVersion());
            AdventureManagerImpl.getInstance().sendMessage(sender, "<#7FFFAA>A plugin that provides adjustable images for texts");
            AdventureManagerImpl.getInstance().sendMessage(sender, "<#DA70D6>\uD83E\uDDEA Author: <#FFC0CB>XiaoMoMi");
            AdventureManagerImpl.getInstance().sendMessage(sender, "<#FF7F50>\uD83D\uDD25 Contributors: <#FFA07A>TopOrigin<white>");
            AdventureManagerImpl.getInstance().sendMessage(sender, "<#FFD700>⭐ <click:open_url:https://mo-mi.gitbook.io/xiaomomi-plugins/plugin-wiki/customnameplates>Document</click> <#A9A9A9>| <#FAFAD2>⛏ <click:open_url:https://github.com/Xiao-MoMi/Custom-Nameplates>Github</click> <#A9A9A9>| <#48D1CC>\uD83D\uDD14 <click:open_url:https://polymart.org/resource/customnameplates.2543>Polymart</click>");
        });
    }
}
