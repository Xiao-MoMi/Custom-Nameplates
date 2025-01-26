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
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.momirealms.customnameplates.common.event.Cancellable;
import net.momirealms.customnameplates.common.event.CommandFeedbackEvent;
import net.momirealms.customnameplates.common.event.NameplatesEvent;
import net.momirealms.customnameplates.common.locale.CustomNameplatesCaptionFormatter;
import net.momirealms.customnameplates.common.locale.CustomNameplatesCaptionProvider;
import net.momirealms.customnameplates.common.locale.TranslationManager;
import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;
import net.momirealms.customnameplates.common.sender.Sender;
import net.momirealms.customnameplates.common.util.ArrayUtils;
import net.momirealms.customnameplates.common.util.TriConsumer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.exception.*;
import org.incendo.cloud.exception.handling.ExceptionContext;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * An abstract implementation of the {@link CustomNameplatesCommandManager} that handles the management and registration of command features,
 * exception handling, and feedback handling within the Custom Nameplates plugin.
 *
 * @param <C> The type of the sender (e.g., player, console) for the commands.
 */
public abstract class AbstractCommandManager<C> implements CustomNameplatesCommandManager<C> {
    /**
     * Registered commands
     */
    protected final HashSet<CommandComponent<C>> registeredRootCommandComponents = new HashSet<>();
    /**
     * Registered features
     */
    protected final HashSet<CommandFeature<C>> registeredFeatures = new HashSet<>();
    /**
     * The command manager
     */
    protected final CommandManager<C> commandManager;
    /**
     * The NameplatesPlugin
     */
    protected final NameplatesPlugin plugin;
    private final CustomNameplatesCaptionFormatter<C> captionFormatter = new CustomNameplatesCaptionFormatter<C>();
    private final MinecraftExceptionHandler.Decorator<C> decorator = (formatter, ctx, msg) -> msg;
    private TriConsumer<C, String, Component> feedbackConsumer;

    /**
     * Constructs an AbstractCommandManager instance with the provided plugin and command manager.
     *
     * @param plugin The plugin instance managing this command manager.
     * @param commandManager The command manager instance that handles the command registration.
     */
    public AbstractCommandManager(NameplatesPlugin plugin, CommandManager<C> commandManager) {
        this.commandManager = commandManager;
        this.plugin = plugin;
        this.inject();
        this.feedbackConsumer = defaultFeedbackConsumer();
    }

    @Override
    public void setFeedbackConsumer(@NotNull TriConsumer<C, String, Component> feedbackConsumer) {
        this.feedbackConsumer = feedbackConsumer;
    }

    @Override
    public TriConsumer<C, String, Component> defaultFeedbackConsumer() {
        return ((sender, node, component) -> {
            NameplatesEvent event = plugin.getEventManager().dispatch(CommandFeedbackEvent.class, sender, node, component);
            if (event instanceof Cancellable cancellable) {
                if (cancellable.cancelled())
                    return;
            }
            wrapSender(sender).sendMessage(
                component, true
            );
        });
    }

    /**
     * Wraps a sender base on platform
     *
     * @param c sender
     * @return the wrapped sender
     */
    protected abstract Sender wrapSender(C c);

    private void inject() {
        getCommandManager().captionRegistry().registerProvider(new CustomNameplatesCaptionProvider<>());
        injectExceptionHandler(InvalidSyntaxException.class, MinecraftExceptionHandler.createDefaultInvalidSyntaxHandler(), StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX);
        injectExceptionHandler(InvalidCommandSenderException.class, MinecraftExceptionHandler.createDefaultInvalidSenderHandler(), StandardCaptionKeys.EXCEPTION_INVALID_SENDER);
        injectExceptionHandler(NoPermissionException.class, MinecraftExceptionHandler.createDefaultNoPermissionHandler(), StandardCaptionKeys.EXCEPTION_NO_PERMISSION);
        injectExceptionHandler(ArgumentParseException.class, MinecraftExceptionHandler.createDefaultArgumentParsingHandler(), StandardCaptionKeys.EXCEPTION_INVALID_ARGUMENT);
        injectExceptionHandler(CommandExecutionException.class, MinecraftExceptionHandler.createDefaultCommandExecutionHandler(), StandardCaptionKeys.EXCEPTION_UNEXPECTED);
    }

    @SuppressWarnings("unchecked")
    private void injectExceptionHandler(Class<? extends Throwable> type, MinecraftExceptionHandler.MessageFactory<C, ?> factory, Caption key) {
        getCommandManager().exceptionController().registerHandler(type, ctx -> {
            final @Nullable ComponentLike message = factory.message(captionFormatter, (ExceptionContext) ctx);
            if (message != null) {
                handleCommandFeedback(ctx.context().sender(), key.key(), decorator.decorate(captionFormatter, ctx, message.asComponent()).asComponent());
            }
        });
    }

    @Override
    public CommandConfig getCommandConfig(YamlDocument document, String featureID) {
        Section section = document.getSection(featureID);
        if (section == null) return null;
        return new CommandConfig.Builder<C>()
                .permission(section.getString("permission"))
                .usages(section.getStringList("usage"))
                .enable(section.getBoolean("enable", false))
                .build();
    }

    @Override
    public Collection<Command.Builder<C>> buildCommandBuilders(CommandConfig config) {
        ArrayList<Command.Builder<C>> list = new ArrayList<>();
        for (String usage : config.getUsages()) {
            if (!usage.startsWith("/")) continue;
            String command = usage.substring(1).trim();
            String[] split = command.split(" ");
            Command.Builder<C> builder = new CommandBuilder.BasicCommandBuilder<>(getCommandManager(), split[0])
                    .setCommandNode(ArrayUtils.subArray(split, 1))
                    .setPermission(config.getPermission())
                    .getBuiltCommandBuilder();
            list.add(builder);
        }
        return list;
    }

    @Override
    public void registerFeature(CommandFeature<C> feature, CommandConfig config) {
        if (!config.isEnable()) throw new RuntimeException("Registering a disabled command feature is not allowed");
        for (Command.Builder<C> builder : buildCommandBuilders(config)) {
            Command<C> command = feature.registerCommand(commandManager, builder);
            this.registeredRootCommandComponents.add(command.rootComponent());
        }
        feature.registerRelatedFunctions();
        this.registeredFeatures.add(feature);
        ((AbstractCommandFeature<C>) feature).setCommandConfig(config);
    }

    @Override
    public void registerDefaultFeatures() {
        YamlDocument document = plugin.getConfigManager().loadConfig(commandsFile, '.');
        try {
            document.save(new File(plugin.getDataDirectory().toFile(), "commands.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.getFeatures().values().forEach(feature -> {
            CommandConfig config = getCommandConfig(document, feature.getFeatureID());
            if (config.isEnable()) {
                registerFeature(feature, config);
            }
        });
    }

    @Override
    public void unregisterFeatures() {
        this.registeredRootCommandComponents.forEach(component -> this.commandManager.commandRegistrationHandler().unregisterRootCommand(component));
        this.registeredRootCommandComponents.clear();
        this.registeredFeatures.forEach(CommandFeature::unregisterRelatedFunctions);
        this.registeredFeatures.clear();
    }

    @Override
    public CommandManager<C> getCommandManager() {
        return commandManager;
    }

    @Override
    public void handleCommandFeedback(C sender, TranslatableComponent.Builder key, Component... args) {
        TranslatableComponent component = key.arguments(args).build();
        this.feedbackConsumer.accept(sender, component.key(), TranslationManager.render(component));
    }

    @Override
    public void handleCommandFeedback(C sender, String node, Component component) {
        this.feedbackConsumer.accept(sender, node, component);
    }
}
