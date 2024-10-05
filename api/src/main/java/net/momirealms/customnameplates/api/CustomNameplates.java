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

import net.momirealms.customnameplates.api.feature.actionbar.ActionBarManager;
import net.momirealms.customnameplates.api.feature.advance.AdvanceManager;
import net.momirealms.customnameplates.api.feature.background.BackgroundManager;
import net.momirealms.customnameplates.api.feature.bossbar.BossBarManager;
import net.momirealms.customnameplates.api.feature.bubble.BubbleManager;
import net.momirealms.customnameplates.api.feature.bubble.chat.ChatManager;
import net.momirealms.customnameplates.api.feature.image.ImageManager;
import net.momirealms.customnameplates.api.feature.nameplate.NameplateManager;
import net.momirealms.customnameplates.api.feature.pack.ResourcePackManager;
import net.momirealms.customnameplates.api.feature.tag.UnlimitedTagManager;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class CustomNameplates implements NameplatesPlugin {

    private static CustomNameplates instance;

    protected DependencyManager dependencyManager;
    protected TranslationManager translationManager;
    protected Consumer<Supplier<String>> debugger = (s) -> getPluginLogger().info("[DEBUG] " + s.get());
    protected ConfigManager configManager;
    protected PacketSender packetSender;
    protected PipelineInjector pipelineInjector;
    protected PlaceholderManager placeholderManager;
    protected RequirementManager requirementManager;
    protected ActionBarManager actionBarManager;
    protected BossBarManager bossBarManager;
    protected UnlimitedTagManager unlimitedTagManager;
    protected Platform platform;
    protected MainTask mainTask = new MainTask(this);
    protected SchedulerTask scheduledMainTask;
    protected ConcurrentHashMap<UUID, CNPlayer> onlinePlayerMap = new ConcurrentHashMap<>();
    protected HashMap<Integer, CNPlayer> entityIDFastLookup = new HashMap<>();
    protected AdvanceManager advanceManager;
    protected BackgroundManager backgroundManager;
    protected EventManager eventManager;
    protected StorageManager storageManager;
    protected BubbleManager bubbleManager;
    protected ChatManager chatManager;
    protected ImageManager imageManager;
    protected NameplateManager nameplateManager;
    protected ResourcePackManager resourcePackManager;

    protected CustomNameplates() {
        instance = this;
    }

    @Override
    public void reload() {
        if (scheduledMainTask != null)
            scheduledMainTask.cancel();
        scheduledMainTask = getScheduler().asyncRepeating(mainTask, 50, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void disable() {
        if (this.scheduledMainTask != null) this.scheduledMainTask.cancel();
    }

    @Override
    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    @Override
    public TranslationManager getTranslationManager() {
        return translationManager;
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void debug(Supplier<String> supplier) {
        this.debugger.accept(supplier);
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public PacketSender getPacketSender() {
        return packetSender;
    }

    public RequirementManager getRequirementManager() {
        return requirementManager;
    }

    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    public UnlimitedTagManager getUnlimitedTagManager() {
        return unlimitedTagManager;
    }

    public AdvanceManager getAdvanceManager() {
        return advanceManager;
    }

    public BackgroundManager getBackgroundManager() {
        return backgroundManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public NameplateManager getNameplateManager() {
        return nameplateManager;
    }

    public ImageManager getImageManager() {
        return imageManager;
    }

    public BubbleManager getBubbleManager() {
        return bubbleManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    public Platform getPlatform() {
        return platform;
    }

    public Collection<CNPlayer> getOnlinePlayers() {
        return new HashSet<>(onlinePlayerMap.values());
    }

    public CNPlayer getPlayer(UUID uuid) {
        return onlinePlayerMap.get(uuid);
    }

    public CNPlayer getPlayer(int entityID) {
        return entityIDFastLookup.get(entityID);
    }

    public static CustomNameplates getInstance() {
        return instance;
    }
}
