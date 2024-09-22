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

public interface CustomNameplatesCommandManager<C> {

    String commandsFile = "commands.yml";

    void unregisterFeatures();

    void registerFeature(CommandFeature<C> feature, CommandConfig<C> config);

    void registerDefaultFeatures();

    Index<String, CommandFeature<C>> getFeatures();

    void setFeedbackConsumer(@NotNull TriConsumer<C, String, Component> feedbackConsumer);

    TriConsumer<C, String, Component> defaultFeedbackConsumer();

    CommandConfig<C> getCommandConfig(YamlDocument document, String featureID);

    Collection<Command.Builder<C>> buildCommandBuilders(CommandConfig<C> config);

    CommandManager<C> getCommandManager();

    void handleCommandFeedback(C sender, TranslatableComponent.Builder key, Component... args);

    void handleCommandFeedback(C sender, String node, Component component);
}
