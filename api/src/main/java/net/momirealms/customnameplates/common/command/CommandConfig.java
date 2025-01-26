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

import java.util.ArrayList;
import java.util.List;

/**
 * A configuration class for setting up command-related options such as enabling/disabling,
 * command usages, and required permissions.
 * <p>
 * This class provides a builder pattern implementation to easily create and configure
 * a {@link CommandConfig} object with a fluent API.
 */
public class CommandConfig {

    /**
     * Flag indicating whether the command is enabled.
     */
    private boolean enable = false;

    /**
     * List of command usage examples or syntax hints.
     */
    private List<String> usages = new ArrayList<>();

    /**
     * Permission required to execute the command.
     */
    private String permission = null;

    /**
     * Private constructor to prevent direct instantiation.
     * Use the {@link Builder} class to construct an instance.
     */
    private CommandConfig() {
    }

    /**
     * Constructs a new {@link CommandConfig} with the specified values.
     *
     * @param enable whether the command is enabled.
     * @param usages the list of command usages.
     * @param permission the permission required to execute the command.
     */
    public CommandConfig(boolean enable, List<String> usages, String permission) {
        this.enable = enable;
        this.usages = usages;
        this.permission = permission;
    }

    /**
     * Gets whether the command is enabled.
     *
     * @return true if the command is enabled, false otherwise.
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Gets the list of command usages or syntax examples.
     *
     * @return the list of command usages.
     */
    public List<String> getUsages() {
        return usages;
    }

    /**
     * Gets the permission required to execute the command.
     *
     * @return the permission string.
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Builder class for constructing a {@link CommandConfig} object with customizable options.
     * This allows setting values for enabling the command, defining usages, and setting permissions.
     *
     * @param <C> the context type associated with the command.
     */
    public static class Builder<C> {

        /**
         * The {@link CommandConfig} object being built.
         */
        private final CommandConfig config;

        /**
         * Constructs a new {@link Builder} for {@link CommandConfig}.
         * Initializes the config object to default values.
         */
        public Builder() {
            this.config = new CommandConfig();
        }

        /**
         * Sets the list of command usages (examples or syntax hints).
         *
         * @param usages the list of command usages.
         * @return the current {@link Builder} instance for method chaining.
         */
        public Builder<C> usages(List<String> usages) {
            config.usages = usages;
            return this;
        }

        /**
         * Sets the permission required to execute the command.
         *
         * @param permission the permission string.
         * @return the current {@link Builder} instance for method chaining.
         */
        public Builder<C> permission(String permission) {
            config.permission = permission;
            return this;
        }

        /**
         * Sets whether the command is enabled.
         *
         * @param enable true to enable the command, false to disable it.
         * @return the current {@link Builder} instance for method chaining.
         */
        public Builder<C> enable(boolean enable) {
            config.enable = enable;
            return this;
        }

        /**
         * Builds and returns the constructed {@link CommandConfig} object.
         *
         * @return the {@link CommandConfig} object.
         */
        public CommandConfig build() {
            return config;
        }
    }
}
