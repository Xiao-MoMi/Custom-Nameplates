/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customnameplates.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import net.momirealms.customnameplates.bukkit.command.BukkitCommandFeature;
import net.momirealms.customnameplates.common.command.CustomNameplatesCommandManager;
import net.momirealms.customnameplates.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.EnumParser;

import java.util.Optional;

public class ReloadCommand extends BukkitCommandFeature<CommandSender> {

    public ReloadCommand(CustomNameplatesCommandManager<CommandSender> commandManager, BukkitCustomNameplates plugin) {
        super(commandManager, plugin);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .optional("content", EnumParser.enumParser(ReloadArgument.class))
                .handler(context -> {
                    Optional<ReloadArgument> optional = context.optional("content");
                    ReloadArgument argument = ReloadArgument.ALL;
                    if (optional.isPresent()) {
                        argument = optional.get();
                    }
                    if (argument == ReloadArgument.ALL || argument == ReloadArgument.CONFIG) {
                        long time1 = System.currentTimeMillis();
                        plugin.reload();
                        handleFeedback(context, MessageConstants.COMMAND_RELOAD_SUCCESS, Component.text(System.currentTimeMillis() - time1));
                    }
                    if (argument == ReloadArgument.ALL || argument == ReloadArgument.PACK) {
                        long time1 = System.currentTimeMillis();
                        plugin.getScheduler().async().execute(() -> {
                            handleFeedback(context, MessageConstants.COMMAND_RELOAD_GENERATING);
                            plugin.getResourcePackManager().generate();
                            handleFeedback(context, MessageConstants.COMMAND_RELOAD_GENERATED, Component.text(System.currentTimeMillis() - time1));
                        });
                    }
                });
    }

    @Override
    public String getFeatureID() {
        return "reload";
    }

    public enum ReloadArgument {

        ALL,
        CONFIG,
        PACK
    }
}
