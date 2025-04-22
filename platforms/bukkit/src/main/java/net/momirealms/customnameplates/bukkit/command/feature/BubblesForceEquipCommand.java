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
import net.momirealms.customnameplates.api.feature.bubble.BubbleConfig;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import net.momirealms.customnameplates.bukkit.command.BukkitCommandFeature;
import net.momirealms.customnameplates.common.command.CustomNameplatesCommandManager;
import net.momirealms.customnameplates.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.concurrent.CompletableFuture;

public class BubblesForceEquipCommand extends BukkitCommandFeature<CommandSender> {

    public BubblesForceEquipCommand(CustomNameplatesCommandManager<CommandSender> commandManager, BukkitCustomNameplates plugin) {
        super(commandManager, plugin);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", PlayerParser.playerParser())
                .required("bubble", StringParser.stringComponent().suggestionProvider(new SuggestionProvider<>() {
                    @Override
                    public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<Object> context, @NonNull CommandInput input) {
                        return CompletableFuture.completedFuture(plugin.getBubbleManager().bubbleConfigs().stream().map(it -> Suggestion.suggestion(it.id())).toList());
                    }
                }))
                .handler(context -> {
                    if (!ConfigManager.bubbleModule()) return;
                    String bubbleId = context.get("bubble");
                    Player bukkitPlayer = context.get("player");
                    BubbleConfig bubble = plugin.getBubbleManager().bubbleConfigById(bubbleId);
                    if (bubble == null) {
                        handleFeedback(context, MessageConstants.COMMAND_BUBBLES_FORCE_EQUIP_FAILURE_NOT_EXISTS, Component.text(bukkitPlayer.getName()), Component.text(bubbleId));
                        return;
                    }
                    CNPlayer player = plugin.getPlayer(bukkitPlayer.getUniqueId());
                    if (player == null) {
                        return;
                    }
                    if (!player.isLoaded()) {
                        plugin.getPluginLogger().warn("Player " + player.name() + " tried to equip a bubble when data not loaded");
                        return;
                    }
                    player.setBubbleData(bubbleId);
                    player.save();
                    handleFeedback(context, MessageConstants.COMMAND_BUBBLES_FORCE_EQUIP_SUCCESS, Component.text(bukkitPlayer.getName()), Component.text(bubbleId), AdventureHelper.miniMessage(bubble.displayName()));
                });
    }

    @Override
    public String getFeatureID() {
        return "bubbles_force_equip";
    }
}
