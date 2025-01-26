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

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

/**
 * A builder interface for constructing commands with configurable parameters.
 * This interface allows setting permissions, command nodes (subcommands), and
 * building the final {@link Command.Builder} object.
 */
public interface CommandBuilder<C> {

    /**
     * Sets the permission required to execute the command.
     *
     * @param permission the permission string required for executing the command.
     * @return the current {@link CommandBuilder} instance for method chaining.
     */
    CommandBuilder<C> setPermission(String permission);

    /**
     * Sets the subcommands (nodes) for the command.
     * This allows defining a hierarchy of commands.
     *
     * @param subNodes the subcommands or nodes to add to the command.
     * @return the current {@link CommandBuilder} instance for method chaining.
     */
    CommandBuilder<C> setCommandNode(String... subNodes);

    /**
     * Retrieves the fully constructed {@link Command.Builder} instance.
     * This method returns the builder object that can be used to further configure
     * and register the command.
     *
     * @return the built {@link Command.Builder} instance.
     */
    Command.Builder<C> getBuiltCommandBuilder();

    /**
     * Basic implementation of the {@link CommandBuilder} interface.
     * This class provides a default implementation for constructing a command
     * with the given root node, permission, and subcommand nodes.
     *
     * @param <C> The type of context or object associated with the command.
     */
    class BasicCommandBuilder<C> implements CommandBuilder<C> {

        /**
         * The actual command builder used to construct the command.
         */
        private Command.Builder<C> commandBuilder;

        /**
         * Constructs a new {@link BasicCommandBuilder} for building a command.
         *
         * @param commandManager the {@link CommandManager} instance used for creating the command builder.
         * @param rootNode the root node for the command.
         */
        public BasicCommandBuilder(CommandManager<C> commandManager, String rootNode) {
            this.commandBuilder = commandManager.commandBuilder(rootNode);
        }

        /**
         * Sets the permission for the command.
         *
         * @param permission the permission required for executing the command.
         * @return the current {@link CommandBuilder} instance for method chaining.
         */
        @Override
        public CommandBuilder<C> setPermission(String permission) {
            this.commandBuilder = this.commandBuilder.permission(permission);
            return this;
        }

        /**
         * Sets the subcommands (nodes) for the command.
         *
         * @param subNodes the subcommands to add to the command.
         * @return the current {@link CommandBuilder} instance for method chaining.
         */
        @Override
        public CommandBuilder<C> setCommandNode(String... subNodes) {
            for (String sub : subNodes) {
                this.commandBuilder = this.commandBuilder.literal(sub);
            }
            return this;
        }

        /**
         * Retrieves the built {@link Command.Builder} instance.
         *
         * @return the built {@link Command.Builder} instance.
         */
        @Override
        public Command.Builder<C> getBuiltCommandBuilder() {
            return commandBuilder;
        }
    }
}
