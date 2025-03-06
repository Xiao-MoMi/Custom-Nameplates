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
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.feature.JoinQuitListener;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import net.momirealms.customnameplates.bukkit.command.BukkitCommandFeature;
import net.momirealms.customnameplates.bukkit.command.misc.PreviewTasks;
import net.momirealms.customnameplates.common.command.CustomNameplatesCommandManager;
import net.momirealms.customnameplates.common.locale.MessageConstants;
import net.momirealms.customnameplates.common.plugin.scheduler.SchedulerTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class NameplatesForcePreviewCommand extends BukkitCommandFeature<CommandSender> implements JoinQuitListener {

    public NameplatesForcePreviewCommand(CustomNameplatesCommandManager<CommandSender> commandManager, BukkitCustomNameplates plugin) {
        super(commandManager, plugin);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", PlayerParser.playerParser())
                .optional("nameplate", StringParser.stringComponent().suggestionProvider(new SuggestionProvider<>() {
                    @Override
                    public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<Object> context, @NonNull CommandInput input) {
                        return CompletableFuture.completedFuture(plugin.getNameplateManager().nameplates().stream().map(it -> Suggestion.suggestion(it.id())).toList());
                    }
                }))
                .optional("time", IntegerParser.integerParser(0))
                .handler(context -> {
                    if (!ConfigManager.nametagModule()) return;
                    Player bukkitPlayer = context.get("player");
                    CNPlayer player = plugin.getPlayer(bukkitPlayer.getUniqueId());
                    if (player == null || !player.isLoaded()) {
                        return;
                    }

                    Optional<String> optionalNameplate = context.optional("nameplate");
                    boolean specified = optionalNameplate.isPresent();

                    int time = (int) context.optional("time").orElse(plugin.getUnlimitedTagManager().previewDuration());

                    String previousNameplate = player.nameplateData();
                    if (specified) {
                        player.setCurrentNameplate(optionalNameplate.get());
                    }

                    if (!player.isTempPreviewing())
                        plugin.getUnlimitedTagManager().setTempPreviewing(player, true);

                    SchedulerTask task = plugin.getScheduler().asyncLater(() -> {
                        if (!player.isOnline()) {
                            return;
                        }

                        if (!plugin.getUnlimitedTagManager().isAlwaysShow()) {
                            plugin.getUnlimitedTagManager().setTempPreviewing(player, false);
                        }

                        if (specified) {
                            player.setCurrentNameplate(previousNameplate);
                        }
                    }, time, TimeUnit.SECONDS);

                    SchedulerTask previous = PreviewTasks.delayedTasks.put(player.uuid(), task);
                    if (previous != null && !previous.cancelled()) {
                        previous.cancel();
                    }

                    handleFeedback(context, MessageConstants.COMMAND_NAMEPLATES_FORCE_PREVIEW_SUCCESS, Component.text(player.name()));
                });
    }

    @Override
    public String getFeatureID() {
        return "nameplates_force_preview";
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
