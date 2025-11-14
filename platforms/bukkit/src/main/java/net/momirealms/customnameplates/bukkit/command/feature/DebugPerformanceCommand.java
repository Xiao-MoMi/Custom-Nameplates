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
import net.momirealms.customnameplates.api.MainTask;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import net.momirealms.customnameplates.bukkit.command.BukkitCommandFeature;
import net.momirealms.customnameplates.common.command.CustomNameplatesCommandManager;
import net.momirealms.customnameplates.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class DebugPerformanceCommand extends BukkitCommandFeature<CommandSender> {

    public DebugPerformanceCommand(CustomNameplatesCommandManager<CommandSender> commandManager, BukkitCustomNameplates plugin) {
        super(commandManager, plugin);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .handler(context -> {
                    if (VersionHelper.isFolia()) {
                        context.sender().sendMessage("Not available on Folia");
                        return;
                    }
                    MainTask.HealthyProfile profile = MainTask.getHealthyProfile();
                    handleFeedback(context, MessageConstants.COMMAND_DEBUG_PERFORMANCE,
                            Component.text(String.format("%.3f", profile.getLoad() * 100)),
                            Component.text(String.format("%.3f", (double) profile.getTotalTimeNS() / 1_000_000)),
                            Component.text(String.format("%.3f", (double) profile.getActionBarConditionNS() / 1_000_000)),
                            Component.text(String.format("%.3f", (double) profile.getRefreshPlaceholderNS() / 1_000_000))
                    );
                });
    }

    @Override
    public String getFeatureID() {
        return "debug_performance";
    }
}
