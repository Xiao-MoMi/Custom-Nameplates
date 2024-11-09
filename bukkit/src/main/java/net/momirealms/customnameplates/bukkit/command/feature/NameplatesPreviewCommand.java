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

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.JoinQuitListener;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import net.momirealms.customnameplates.bukkit.command.BukkitCommandFeature;
import net.momirealms.customnameplates.bukkit.command.misc.PreviewTasks;
import net.momirealms.customnameplates.common.command.CustomNameplatesCommandManager;
import net.momirealms.customnameplates.common.locale.MessageConstants;
import net.momirealms.customnameplates.common.plugin.scheduler.SchedulerTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.concurrent.TimeUnit;

public class NameplatesPreviewCommand extends BukkitCommandFeature<CommandSender> implements JoinQuitListener {

    public NameplatesPreviewCommand(CustomNameplatesCommandManager<CommandSender> commandManager, BukkitCustomNameplates plugin) {
        super(commandManager, plugin);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(context -> {
                    if (!ConfigManager.nametagModule()) return;
                    if (CustomNameplates.getInstance().getUnlimitedTagManager().isAlwaysShow()) return;
                    CNPlayer player = plugin.getPlayer(context.sender().getUniqueId());
                    if (player == null) {
                        throw new RuntimeException("Player should not be null");
                    }
                    if (plugin.getUnlimitedTagManager().isAlwaysShow()) {
                        return;
                    }
                    if (player.isTempPreviewing()) {
                        handleFeedback(context, MessageConstants.COMMAND_NAMEPLATES_PREVIEW_FAILURE_COOLDOWN);
                        return;
                    }
                    plugin.getUnlimitedTagManager().setTempPreviewing(player, true);

                    SchedulerTask task = plugin.getScheduler().asyncLater(() -> {
                        if (!player.isOnline()) {
                            return;
                        }
                        plugin.getUnlimitedTagManager().setTempPreviewing(player, false);
                    }, plugin.getUnlimitedTagManager().previewDuration(), TimeUnit.SECONDS);

                    PreviewTasks.delayedTasks.put(player.uuid(), task);

                    handleFeedback(context, MessageConstants.COMMAND_NAMEPLATES_PREVIEW_SUCCESS);
                });
    }

    @Override
    public String getFeatureID() {
        return "nameplates_preview";
    }

    @Override
    public void registerRelatedFunctions() {
        plugin.registerJoinQuitListener(this);
    }

    @Override
    public void unregisterRelatedFunctions() {
        for (SchedulerTask task : PreviewTasks.delayedTasks.values()) {
            if (!task.cancelled())
                task.cancel();
        }
        PreviewTasks.delayedTasks.clear();
        plugin.unregisterJoinQuitListener(this);
    }

    @Override
    public void onPlayerJoin(CNPlayer player) {
        SchedulerTask previous = PreviewTasks.delayedTasks.remove(player.uuid());
        if (previous != null && !previous.cancelled()) {
            previous.cancel();
        }
    }

    @Override
    public void onPlayerQuit(CNPlayer player) {
        SchedulerTask previous = PreviewTasks.delayedTasks.remove(player.uuid());
        if (previous != null && !previous.cancelled()) {
            previous.cancel();
        }
    }
}
