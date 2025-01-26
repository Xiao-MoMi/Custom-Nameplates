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

import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.util.Index;
import net.momirealms.customnameplates.common.util.TriConsumer;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Interface for managing commands within the Custom Nameplates plugin. This interface defines methods for
 * registering features, handling feedback, and interacting with the command manager and configurations.
 *
 * @param <C> The type of the sender (e.g., player, console) for the commands.
 */
public interface CustomNameplatesCommandManager<C> {

    /**
     * The default configuration file name for commands
     */
    String commandsFile = "commands.yml";

    /**
     * Unregisters all previously registered command features.
     */
    void unregisterFeatures();

    /**
     * Registers a command feature with the provided configuration.
     *
     * @param feature The command feature to register.
     * @param config The configuration for the feature.
     */
    void registerFeature(CommandFeature<C> feature, CommandConfig config);

    /**
     * Registers all default features based on the plugin configuration.
     */
    void registerDefaultFeatures();

    /**
     * Retrieves all registered command features in the form of an index.
     *
     * @return The index of registered command features.
     */
    Index<String, CommandFeature<C>> getFeatures();

    /**
     * Sets a custom feedback consumer that handles the feedback sent to the command sender.
     *
     * @param feedbackConsumer The custom feedback consumer.
     */
    void setFeedbackConsumer(@NotNull TriConsumer<C, String, Component> feedbackConsumer);

    /**
     * Provides the default feedback consumer for sending feedback messages.
     *
     * @return The default feedback consumer.
     */
    TriConsumer<C, String, Component> defaultFeedbackConsumer();

    /**
     * Retrieves the command configuration from a YAML document and a given feature ID.
     *
     * @param document The YAML document containing the configuration.
     * @param featureID The ID of the feature to retrieve configuration for.
     * @return The command configuration for the specified feature, or null if not found.
     */
    CommandConfig getCommandConfig(YamlDocument document, String featureID);

    /**
     * Builds a collection of command builders based on the given configuration.
     *
     * @param config The command configuration.
     * @return A collection of command builders.
     */
    Collection<Command.Builder<C>> buildCommandBuilders(CommandConfig config);

    /**
     * Retrieves the command manager associated with this command manager.
     *
     * @return The command manager.
     */
    CommandManager<C> getCommandManager();

    /**
     * Sends feedback to the sender based on the provided key and arguments.
     *
     * @param sender The sender of the command.
     * @param key The key used to retrieve the feedback message.
     * @param args The arguments to be included in the feedback message.
     */
    void handleCommandFeedback(C sender, TranslatableComponent.Builder key, Component... args);

    /**
     * Sends feedback to the sender based on the provided node and component.
     *
     * @param sender The sender of the command.
     * @param node The feedback node.
     * @param component The component containing the feedback message.
     */
    void handleCommandFeedback(C sender, String node, Component component);
}
