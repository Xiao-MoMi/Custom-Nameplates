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
import net.momirealms.customnameplates.common.sender.SenderFactory;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

/**
 * Abstract base class for command feature implementations within the Custom Nameplates command system.
 * This class provides utility methods for command registration, feedback handling, and configuration.
 *
 * @param <C> The type of the sender (e.g., a player or console sender) for the command.
 */
public abstract class AbstractCommandFeature<C> implements CommandFeature<C> {
    /**
     * The command manager
     */
    protected final CustomNameplatesCommandManager<C> commandManager;
    /**
     * The command config
     */
    protected CommandConfig commandConfig;

    /**
     * Constructs an AbstractCommandFeature instance with the given command manager.
     *
     * @param commandManager The command manager to manage the commands associated with this feature.
     */
    public AbstractCommandFeature(CustomNameplatesCommandManager<C> commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Abstract method to retrieve the sender factory for the command.
     * Implementations must provide the logic for creating senders of type C.
     *
     * @return The sender factory for the command.
     */
    protected abstract SenderFactory<?, C> getSenderFactory();

    /**
     * Abstract method to assemble the command builder with the given manager.
     * Implementations must define how to build the command.
     *
     * @param manager The command manager responsible for managing the command.
     * @param builder The builder used to construct the command.
     * @return A command builder ready to be built.
     */
    public abstract Command.Builder<? extends C> assembleCommand(CommandManager<C> manager, Command.Builder<C> builder);

    /**
     * Registers the command with the provided command manager.
     * This method builds the command and registers it with the command manager.
     *
     * @param manager The command manager to register the command with.
     * @param builder The builder used to create the command.
     * @return The registered command.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Command<C> registerCommand(CommandManager<C> manager, Command.Builder<C> builder) {
        Command<C> command = (Command<C>) assembleCommand(manager, builder).build();
        manager.command(command);
        return command;
    }

    /**
     * Registers any related functions or hooks. This method is a no-op by default
     * and can be overridden in subclasses to register additional functionality.
     */
    @Override
    public void registerRelatedFunctions() {
        // empty
    }

    /**
     * Unregisters any related functions or hooks. This method is a no-op by default
     * and can be overridden in subclasses to unregister additional functionality.
     */
    @Override
    public void unregisterRelatedFunctions() {
        // empty
    }

    /**
     * Handles feedback for the command execution, sending a translation message to the sender.
     * The feedback is suppressed if the "silent" flag is present in the command context.
     *
     * @param context The command context containing information about the sender and flags.
     * @param key The key for the translation component to be used as feedback.
     * @param args The arguments to be passed to the translation.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void handleFeedback(CommandContext<?> context, TranslatableComponent.Builder key, Component... args) {
        if (context.flags().hasFlag("silent")) {
            return;
        }
        commandManager.handleCommandFeedback((C) context.sender(), key, args);
    }

    /**
     * Handles feedback for the command execution, sending a translation message to the sender.
     *
     * @param sender The sender to receive the feedback.
     * @param key The key for the translation component to be used as feedback.
     * @param args The arguments to be passed to the translation.
     */
    @Override
    public void handleFeedback(C sender, TranslatableComponent.Builder key, Component... args) {
        commandManager.handleCommandFeedback(sender, key, args);
    }

    /**
     * Retrieves the command manager associated with this feature.
     *
     * @return The command manager.
     */
    @Override
    public CustomNameplatesCommandManager<C> getCustomNameplatesCommandManager() {
        return commandManager;
    }

    /**
     * Retrieves the configuration associated with the command.
     *
     * @return The command configuration.
     */
    @Override
    public CommandConfig getCommandConfig() {
        return commandConfig;
    }

    /**
     * Sets the configuration for the command.
     *
     * @param commandConfig The configuration to set for the command.
     */
    public void setCommandConfig(CommandConfig commandConfig) {
        this.commandConfig = commandConfig;
    }
}
