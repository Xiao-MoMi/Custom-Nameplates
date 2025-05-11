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

package net.momirealms.customnameplates.api;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.momirealms.customnameplates.api.feature.actionbar.ActionBarManager;
import net.momirealms.customnameplates.api.feature.advance.AdvanceManager;
import net.momirealms.customnameplates.api.feature.background.BackgroundManager;
import net.momirealms.customnameplates.api.feature.bossbar.BossBarManager;
import net.momirealms.customnameplates.api.feature.bubble.BubbleManager;
import net.momirealms.customnameplates.api.feature.chat.ChatManager;
import net.momirealms.customnameplates.api.feature.image.ImageManager;
import net.momirealms.customnameplates.api.feature.nameplate.NameplateManager;
import net.momirealms.customnameplates.api.feature.pack.ResourcePackManager;
import net.momirealms.customnameplates.api.feature.tag.UnlimitedTagManager;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.api.network.PacketSender;
import net.momirealms.customnameplates.api.network.PipelineInjector;
import net.momirealms.customnameplates.api.placeholder.PlaceholderManager;
import net.momirealms.customnameplates.api.requirement.RequirementManager;
import net.momirealms.customnameplates.api.storage.StorageManager;
import net.momirealms.customnameplates.common.dependency.DependencyManager;
import net.momirealms.customnameplates.common.event.EventManager;
import net.momirealms.customnameplates.common.locale.TranslationManager;
import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;
import net.momirealms.customnameplates.common.plugin.scheduler.SchedulerTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Abstract class representing the core plugin management of CustomNameplates.
 * Provides access to managers for various features.
 */
public abstract class CustomNameplates implements NameplatesPlugin {
    private static CustomNameplates instance;

    /**
     * Manages the dependencies required by the plugin or system.
     */
    protected DependencyManager dependencyManager;

    /**
     * Handles translations for the plugin, ensuring text is localized based on the player's language.
     */
    protected TranslationManager translationManager;

    /**
     * A debugger function that logs debug information with a "[DEBUG]" prefix.
     * It consumes a supplier of strings and logs the result.
     */
    protected Consumer<Supplier<String>> debugger = (s) -> getPluginLogger().info("[DEBUG] " + s.get());

    /**
     * Manages configuration settings, loading and accessing configuration data for the plugin.
     */
    protected ConfigManager configManager;

    /**
     * Responsible for sending packets to clients or other network entities.
     */
    protected PacketSender packetSender;

    /**
     * Injects custom pipelines into the system to modify or handle data processing flows.
     */
    protected PipelineInjector pipelineInjector;

    /**
     * Manages placeholders used for dynamic text replacements in the plugin's output.
     */
    protected PlaceholderManager placeholderManager;

    /**
     * Manages requirements or conditions that must be met for certain actions or events to occur.
     */
    protected RequirementManager requirementManager;

    /**
     * Manages the action bar, responsible for displaying dynamic text to players in the action bar.
     */
    protected ActionBarManager actionBarManager;

    /**
     * Manages the boss bar, which displays a customizable progress bar and messages to players.
     */
    protected BossBarManager bossBarManager;

    /**
     * Manages unlimited tags that can be applied to players, items, or other entities.
     */
    protected UnlimitedTagManager unlimitedTagManager;

    /**
     * Represents the platform on which the plugin or system is running (e.g., Spigot, Bukkit, etc.).
     */
    protected Platform platform;

    /**
     * The main task or process that the plugin or system executes.
     */
    protected MainTask mainTask;

    /**
     * A scheduled task that runs alongside the main task, typically used for periodic or delayed actions.
     */
    protected SchedulerTask scheduledMainTask;

    /**
     * A map that tracks online players by their unique UUID, allowing quick access to player data.
     */
    protected Map<UUID, CNPlayer> onlinePlayerMap = new ConcurrentHashMap<>();

    /**
     * A fast lookup map that associates entity IDs to player data (CNPlayer) for quick access.
     */
    protected Map<Integer, CNPlayer> entityIDFastLookup = new ConcurrentHashMap<>();

    /**
     * Manages advances or progressions for players, such as achievements or level ups.
     */
    protected AdvanceManager advanceManager;

    /**
     * Manages background tasks, likely for asynchronous processing or UI updates.
     */
    protected BackgroundManager backgroundManager;

    /**
     * Manages events and event handling within the plugin or system.
     */
    protected EventManager eventManager;

    /**
     * Manages the plugin's data storage system, including saving and retrieving player data.
     */
    protected StorageManager storageManager;

    /**
     * Manages bubble effects, likely for UI or visual effects displayed to players.
     */
    protected BubbleManager bubbleManager;

    /**
     * Manages chat functionality, including message formatting, commands, and interactions.
     */
    protected ChatManager chatManager;

    /**
     * Manages images, such as textures or images for resource packs or player displays.
     */
    protected ImageManager imageManager;

    /**
     * Manages nameplates displayed above players or entities.
     */
    protected NameplateManager nameplateManager;

    /**
     * Manages resource packs, allowing the plugin to send custom content to players.
     */
    protected ResourcePackManager resourcePackManager;

    /**
     * Provides an API for external access to the plugin's functionality and data.
     */
    protected CustomNameplatesAPI api;

    private String buildByBit = "%%__BUILTBYBIT__%%";
    private String polymart = "%%__POLYMART__%%";
    private String time = "%%__TIMESTAMP__%%";
    private String user = "%%__USER__%%";
    private String username = "%%__USERNAME__%%";
    private boolean isLatest = false;

    /**
     * Creates the CustomNameplates instance
     */
    protected CustomNameplates() {
        instance = this;
    }

    /**
     * Reloads the plugin
     */
    @Override
    public abstract void reload();

    /**
     * Disables the plugin, canceling any scheduled tasks.
     */
    @Override
    public abstract void disable();

    @Override
    public void enable() {
        boolean downloadFromPolymart = polymart.equals("1");
        boolean downloadFromBBB = buildByBit.equals("true");
        if (ConfigManager.checkUpdate()) {
            VersionHelper.UPDATE_CHECKER.apply(this).thenAccept(result -> {
                String link;
                if (downloadFromPolymart) {
                    link = "https://polymart.org/resource/2543/";
                } else if (downloadFromBBB) {
                    link = "https://builtbybit.com/resources/36359/";
                } else {
                    link = "https://github.com/Xiao-MoMi/Custom-Nameplates/";
                }
                if (!result) {
                    this.getPluginLogger().info("You are using the latest version.");
                    isLatest = true;
                } else {
                    this.getPluginLogger().warn("Update is available: " + link);
                    isLatest = false;
                }
            });
        }
    }

    @Override
    public boolean isUpToDate() {
        return isLatest;
    }

    /**
     * Logs debug messages through the provided supplier.
     *
     * @param supplier a supplier that provides debug messages
     */
    @Override
    public void debug(Supplier<String> supplier) {
        this.debugger.accept(supplier);
    }

    /**
     * Returns the dependency manager for managing external dependencies.
     *
     * @return the {@link DependencyManager} instance
     */
    @Override
    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    /**
     * Returns the translation manager for managing language translations.
     *
     * @return the {@link TranslationManager} instance
     */
    @Override
    public TranslationManager getTranslationManager() {
        return translationManager;
    }

    /**
     * Returns the configuration manager for managing plugin configurations.
     *
     * @return the {@link ConfigManager} instance
     */
    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Returns the manager responsible for handling placeholders.
     *
     * @return the {@link PlaceholderManager} instance
     */
    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    /**
     * Returns the packet sender responsible for sending network packets to players.
     *
     * @return the {@link PacketSender} instance
     */
    public PacketSender getPacketSender() {
        return packetSender;
    }

    /**
     * Returns the requirement manager responsible for managing requirements.
     *
     * @return the {@link RequirementManager} instance
     */
    public RequirementManager getRequirementManager() {
        return requirementManager;
    }

    /**
     * Returns the action bar manager responsible for managing action bar displays.
     *
     * @return the {@link ActionBarManager} instance
     */
    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    /**
     * Returns the manager responsible for handling unlimited tags.
     *
     * @return the {@link UnlimitedTagManager} instance
     */
    public UnlimitedTagManager getUnlimitedTagManager() {
        return unlimitedTagManager;
    }

    /**
     * Returns the manager responsible for handling font advances.
     *
     * @return the {@link AdvanceManager} instance
     */
    public AdvanceManager getAdvanceManager() {
        return advanceManager;
    }

    /**
     * Returns the background manager responsible for handling background images.
     *
     * @return the {@link BackgroundManager} instance
     */
    public BackgroundManager getBackgroundManager() {
        return backgroundManager;
    }

    /**
     * Returns the event manager responsible for managing events.
     *
     * @return the {@link EventManager} instance
     */
    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * Returns the storage manager responsible for handling data storage.
     *
     * @return the {@link StorageManager} instance
     */
    public StorageManager getStorageManager() {
        return storageManager;
    }

    /**
     * Returns the nameplate manager responsible for managing nameplate images.
     *
     * @return the {@link NameplateManager} instance
     */
    public NameplateManager getNameplateManager() {
        return nameplateManager;
    }

    /**
     * Returns the image manager responsible for handling images used within the plugin.
     *
     * @return the {@link ImageManager} instance
     */
    public ImageManager getImageManager() {
        return imageManager;
    }

    /**
     * Returns the bubble manager responsible for managing chat bubbles.
     *
     * @return the {@link BubbleManager} instance
     */
    public BubbleManager getBubbleManager() {
        return bubbleManager;
    }

    /**
     * Returns the chat manager responsible for managing chat-related features.
     *
     * @return the {@link ChatManager} instance
     */
    public ChatManager getChatManager() {
        return chatManager;
    }

    /**
     * Returns the resource pack manager responsible for managing custom resource packs.
     *
     * @return the {@link ResourcePackManager} instance
     */
    public ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    /**
     * Get the API class
     *
     * @return api
     */
    public CustomNameplatesAPI getAPI() {
        return api;
    }

    /**
     * Returns the platform object for accessing platform-specific functionality.
     *
     * @return the {@link Platform} instance
     */
    public Platform getPlatform() {
        return platform;
    }

    /**
     * Returns a collection of all currently online players.
     *
     * @return a collection of {@link CNPlayer} instances
     */
    public Collection<CNPlayer> getOnlinePlayers() {
        return new ObjectArrayList<>(onlinePlayerMap.values());
    }

    /**
     * Retrieves a player by their UUID.
     *
     * @param uuid the UUID of the player
     * @return the {@link CNPlayer} instance, or null if not found
     */
    public CNPlayer getPlayer(UUID uuid) {
        return onlinePlayerMap.get(uuid);
    }

    /**
     * Retrieves a player by their entity ID.
     *
     * @param entityID the entity ID of the player
     * @return the {@link CNPlayer} instance, or null if not found
     */
    public CNPlayer getPlayer(int entityID) {
        return entityIDFastLookup.get(entityID);
    }

    /**
     * Returns the singleton instance of CustomNameplates.
     *
     * @return the {@link CustomNameplates} instance
     */
    public static CustomNameplates getInstance() {
        return instance;
    }

    @ApiStatus.Experimental
    public void addPlayerUnsafe(int entityId, CNPlayer player) {
        if (this.entityIDFastLookup.containsKey(entityId)) return;
        this.entityIDFastLookup.put(entityId, player);
    }

    @Nullable
    @ApiStatus.Experimental
    public CNPlayer removePlayerUnsafe(int entityId) {
        return this.entityIDFastLookup.remove(entityId);
    }
}
