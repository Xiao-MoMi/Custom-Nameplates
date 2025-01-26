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

package net.momirealms.customnameplates.common.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

/**
 * Represents the contract for a feature that provides a command in the Custom Nameplates system.
 * This interface defines methods for registering commands, handling feedback, and managing related functionality.
 *
 * @param <C> The type of the sender (e.g., player, console) for the command.
 */
public interface CommandFeature<C> {

    /**
     * Registers the command with the provided command manager and builder.
     *
     * @param cloudCommandManager The command manager responsible for managing commands.
     * @param builder The command builder used to create the command.
     * @return The registered command.
     */
    Command<C> registerCommand(CommandManager<C> cloudCommandManager, Command.Builder<C> builder);

    /**
     * Retrieves a unique identifier for this feature.
     * The feature ID is used to identify the feature within the system.
     *
     * @return A unique identifier for this command feature.
     */
    String getFeatureID();

    /**
     * Registers any related functions or hooks that this feature may require.
     * This method may be used to register additional functionality necessary for the feature.
     */
    void registerRelatedFunctions();

    /**
     * Unregisters any related functions or hooks that were previously registered.
     * This method is used for cleaning up after the feature when it's no longer needed.
     */
    void unregisterRelatedFunctions();

    /**
     * Handles feedback for the command execution, sending a translation message to the sender.
     * The feedback may be affected by the flags in the command context (e.g., silent mode).
     *
     * @param context The command context containing information about the sender and flags.
     * @param key The key for the translation component to be used as feedback.
     * @param args The arguments to be passed to the translation.
     */
    void handleFeedback(CommandContext<?> context, TranslatableComponent.Builder key, Component... args);

    /**
     * Handles feedback for the command execution, sending a translation message to the sender.
     *
     * @param sender The sender to receive the feedback.
     * @param key The key for the translation component to be used as feedback.
     * @param args The arguments to be passed to the translation.
     */
    void handleFeedback(C sender, TranslatableComponent.Builder key, Component... args);

    /**
     * Retrieves the command manager associated with this feature.
     *
     * @return The command manager.
     */
    CustomNameplatesCommandManager<C> getCustomNameplatesCommandManager();

    /**
     * Retrieves the configuration associated with the command.
     *
     * @return The command configuration.
     */
    CommandConfig getCommandConfig();
}