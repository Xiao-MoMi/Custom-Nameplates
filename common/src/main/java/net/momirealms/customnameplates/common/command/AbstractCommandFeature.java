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

public abstract class AbstractCommandFeature<C> implements CommandFeature<C> {

    protected final CustomNameplatesCommandManager<C> commandManager;
    protected CommandConfig<C> commandConfig;

    public AbstractCommandFeature(CustomNameplatesCommandManager<C> commandManager) {
        this.commandManager = commandManager;
    }

    protected abstract SenderFactory<?, C> getSenderFactory();

    public abstract Command.Builder<? extends C> assembleCommand(CommandManager<C> manager, Command.Builder<C> builder);

    @Override
    @SuppressWarnings("unchecked")
    public Command<C> registerCommand(CommandManager<C> manager, Command.Builder<C> builder) {
        Command<C> command = (Command<C>) assembleCommand(manager, builder).build();
        manager.command(command);
        return command;
    }

    @Override
    public void registerRelatedFunctions() {
        // empty
    }

    @Override
    public void unregisterRelatedFunctions() {
        // empty
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleFeedback(CommandContext<?> context, TranslatableComponent.Builder key, Component... args) {
        if (context.flags().hasFlag("silent")) {
            return;
        }
        commandManager.handleCommandFeedback((C) context.sender(), key, args);
    }

    @Override
    public void handleFeedback(C sender, TranslatableComponent.Builder key, Component... args) {
        commandManager.handleCommandFeedback(sender, key, args);
    }

    @Override
    public CustomNameplatesCommandManager<C> getCustomFishingCommandManager() {
        return commandManager;
    }

    @Override
    public CommandConfig<C> getCommandConfig() {
        return commandConfig;
    }

    public void setCommandConfig(CommandConfig<C> commandConfig) {
        this.commandConfig = commandConfig;
    }
}
