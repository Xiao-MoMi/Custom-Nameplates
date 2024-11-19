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
import net.momirealms.customnameplates.api.feature.bubble.BubbleConfig;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import net.momirealms.customnameplates.bukkit.command.BukkitCommandFeature;
import net.momirealms.customnameplates.common.command.CustomNameplatesCommandManager;
import net.momirealms.customnameplates.common.locale.MessageConstants;
import net.momirealms.customnameplates.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.Collection;
import java.util.StringJoiner;

public class BubblesListCommand extends BukkitCommandFeature<CommandSender> {

    public BubblesListCommand(CustomNameplatesCommandManager<CommandSender> commandManager, BukkitCustomNameplates plugin) {
        super(commandManager, plugin);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(context -> {
                    if (!ConfigManager.bubbleModule()) return;
                    CNPlayer player = plugin.getPlayer(context.sender().getUniqueId());
                    if (player == null) {
                        throw new RuntimeException("Player should not be null");
                    }
                    Collection<BubbleConfig> available = plugin.getBubbleManager().availableBubbles(player);
                    if (available.isEmpty()) {
                        handleFeedback(context, MessageConstants.COMMAND_BUBBLES_LIST_FAILURE_NONE);
                    } else {
                        StringJoiner sj1 = new StringJoiner(TranslationManager.miniMessageTranslation(MessageConstants.COMMAND_BUBBLES_LIST_DELIMITER.build().key()));
                        StringJoiner sj2 = new StringJoiner(TranslationManager.miniMessageTranslation(MessageConstants.COMMAND_BUBBLES_LIST_DELIMITER.build().key()));
                        for (BubbleConfig bubble : available) {
                            sj1.add(bubble.id());
                            sj2.add(bubble.displayName());
                        }
                        handleFeedback(context, MessageConstants.COMMAND_BUBBLES_LIST_SUCCESS, AdventureHelper.miniMessage(sj1.toString()), AdventureHelper.miniMessage(sj2.toString()));
                    }
                });
    }

    @Override
    public String getFeatureID() {
        return "bubbles_list";
    }
}
