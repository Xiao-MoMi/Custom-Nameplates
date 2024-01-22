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
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.data.OnlineUser;
import net.momirealms.customnameplates.api.mechanic.nameplate.Nameplate;
import net.momirealms.customnameplates.api.mechanic.tag.NameplatePlayer;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.CustomNameplatesPluginImpl;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.customnameplates.paper.setting.CNLocale;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("DuplicatedCode")
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
                        getPreviewCommand(),
                        getListCommand(),
                        getForceEquipCommand(),
                        getForceUnEquipCommand(),
                        getForcePreviewCommand()
                )
                .register();
    }

    private CommandAPICommand getForceEquipCommand() {
        return new CommandAPICommand("force-equip")
                .withPermission("customnameplates.admin")
                .withArguments(new PlayerArgument("player"))
                .withArguments(new StringArgument("nameplate").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> plugin.getNameplateManager().getNameplateKeys().toArray(new String[0]))))
                .executes((sender, args) -> {
                        Player player = (Player) args.get("player");
                        String nameplate = (String) args.get("nameplate");
                        if (player == null) return;
                        if (!plugin.getNameplateManager().equipNameplate(player, nameplate, false)) {
                                AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_NAMEPLATE_NOT_EXISTS);
                                return;
                        }
                        Nameplate nameplateInstance = plugin.getNameplateManager().getNameplate(nameplate);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_FORCE_EQUIP_NAMEPLATE.replace("{Nameplate}", nameplateInstance.getDisplayName()).replace("{Player}", player.getName()));
                });
    }

    private CommandAPICommand getForceUnEquipCommand() {
        return new CommandAPICommand("force-unequip")
                .withPermission("customnameplates.admin")
                .withArguments(new PlayerArgument("player"))
                .executes((sender, args) -> {
                        Player player = (Player) args.get("player");
                        if (player == null) return;
                        plugin.getNameplateManager().unEquipNameplate(player, false);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_FORCE_UNEQUIP_NAMEPLATE.replace("{Player}", player.getName()));
                });
    }

    private CommandAPICommand getPreviewCommand() {
        return new CommandAPICommand("preview")
                .withPermission("nameplates.preview")
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

    private CommandAPICommand getForcePreviewCommand() {
        return new CommandAPICommand("force-preview")
                .withPermission("customnameplates.admin")
                .withArguments(new PlayerArgument("player"))
                .withOptionalArguments(new StringArgument("nameplate").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> plugin.getNameplateManager().getNameplateKeys().toArray(new String[0]))))
                .executes((sender, args) -> {
                        Player player = (Player) args.get("player");
                        String nameplate = (String) args.getOrDefault("nameplate", "");
                        if (player == null) return;
                        NameplatePlayer nameplatePlayer = plugin.getNameplateManager().getNameplatePlayer(player.getUniqueId());
                        if (nameplatePlayer == null) {
                                LogUtils.warn(player.getName() + " failed to preview because no tag is created");
                                return;
                        }
                        if (nameplatePlayer.isPreviewing()) {
                                AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_PREVIEW_COOLDOWN);
                                return;
                        }
                        Optional<OnlineUser> user = plugin.getStorageManager().getOnlineUser(player.getUniqueId());
                        if (user.isEmpty()) {
                                LogUtils.warn(player.getName() + " failed to preview because data not loaded");
                                return;
                        }
                        String previous = user.get().getNameplateKey();
                        if (!plugin.getNameplateManager().equipNameplate(player, nameplate, true)) {
                                AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_NAMEPLATE_NOT_EXISTS);
                                return;
                        }
                        nameplatePlayer.setPreview(true);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_FORCE_PREVIEW.replace("{Player}", player.getName()));
                        plugin.getScheduler().runTaskAsyncLater(() -> {
                                nameplatePlayer.setPreview(false);
                                plugin.getNameplateManager().equipNameplate(player, previous, true);
                        }, plugin.getNameplateManager().getPreviewDuration(), TimeUnit.SECONDS);
                });
    }

    private CommandAPICommand getEquipCommand() {
        return new CommandAPICommand("equip")
                .withPermission("nameplates.equip")
                .withArguments(new StringArgument("nameplate").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> plugin.getNameplateManager().getAvailableNameplates((Player) commandSenderSuggestionInfo.sender()).toArray(new String[0]))))
                .executesPlayer((player, args) -> {
                        String nameplate = (String) args.get("nameplate");
                        Nameplate nameplateInstance = plugin.getNameplateManager().getNameplate(nameplate);
                        if (nameplateInstance == null) {
                                AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_NAMEPLATE_NOT_AVAILABLE);
                                return;
                        }
                        if (!plugin.getNameplateManager().equipNameplate(player, nameplate, false)) {
                                AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_NAMEPLATE_NOT_EXISTS);
                                return;
                        }
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_EQUIP_NAMEPLATE.replace("{Nameplate}", nameplateInstance.getDisplayName()));
                });
    }

    private CommandAPICommand getUnEquipCommand() {
        return new CommandAPICommand("unequip")
                .withPermission("nameplates.unequip")
                .executesPlayer((player, args) -> {
                        plugin.getNameplateManager().unEquipNameplate(player, false);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_UNEQUIP_NAMEPLATE);
                });
    }

    private CommandAPICommand getListCommand() {
        return new CommandAPICommand("list")
                .withPermission("nameplates.list")
                .executesPlayer((player, args) -> {
                        List<String> nameplates = plugin.getNameplateManager().getAvailableNameplateDisplayNames(player);
                        if (nameplates.size() != 0) {
                                StringJoiner stringJoiner = new StringJoiner(", ");
                                for (String availableNameplate : nameplates) {
                                        stringJoiner.add(availableNameplate);
                                }
                                AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_AVAILABLE_NAMEPLATE.replace("{Nameplates}", stringJoiner.toString()));
                        } else {
                                AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_HAVE_NO_NAMEPLATE);
                        }
                });
    }

    private CommandAPICommand getReloadCommand() {
        return new CommandAPICommand("reload")
                .withPermission("customnameplates.admin")
                .withOptionalArguments(new BooleanArgument("generate pack"))
                .executes((sender, args) -> {
                        long time = System.currentTimeMillis();
                        plugin.reload(false);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_RELOAD.replace("{time}", String.valueOf(System.currentTimeMillis()-time)));
                        boolean generate = (boolean) args.getOrDefault("generate pack", true);
                        if (generate) {
                                AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_GENERATING);
                                plugin.getResourcePackManager().generateResourcePack();
                                AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_PACK_GENERATED);
                        }
                });
    }

    private CommandAPICommand getAboutCommand() {
        return new CommandAPICommand("about")
                .withPermission("customnameplates.about")
                .executes((sender, args) -> {
                AdventureManagerImpl.getInstance().sendMessage(sender, "<#3CB371>⚓ CustomNameplates <gray>- <#98FB98>" + CustomNameplatesPlugin.getInstance().getVersionManager().getPluginVersion());
                AdventureManagerImpl.getInstance().sendMessage(sender, "<#7FFFAA>A plugin that provides adjustable images for texts");
                AdventureManagerImpl.getInstance().sendMessage(sender, "<#DA70D6>\uD83E\uDDEA Author: <#FFC0CB>XiaoMoMi");
                AdventureManagerImpl.getInstance().sendMessage(sender, "<#FF7F50>\uD83D\uDD25 Contributors: <#FFA07A>TopOrigin<white>");
                AdventureManagerImpl.getInstance().sendMessage(sender, "<#FFD700>⭐ <click:open_url:https://mo-mi.gitbook.io/xiaomomi-plugins/plugin-wiki/customnameplates>Document</click> <#A9A9A9>| <#FAFAD2>⛏ <click:open_url:https://github.com/Xiao-MoMi/Custom-Nameplates>Github</click> <#A9A9A9>| <#48D1CC>\uD83D\uDD14 <click:open_url:https://polymart.org/resource/customnameplates.2543>Polymart</click>");
        });
    }
}
