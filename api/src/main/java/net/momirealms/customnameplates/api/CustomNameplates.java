package net.momirealms.customnameplates.api;

import net.kyori.adventure.audience.Audience;
import net.momirealms.customnameplates.api.feature.actionbar.ActionBarManager;
import net.momirealms.customnameplates.api.packet.PacketSender;
import net.momirealms.customnameplates.api.placeholder.PlaceholderManager;
import net.momirealms.customnameplates.api.requirement.RequirementManager;
import net.momirealms.customnameplates.api.tracker.TrackerManager;
import net.momirealms.customnameplates.common.dependency.DependencyManager;
import net.momirealms.customnameplates.common.locale.TranslationManager;
import net.momirealms.customnameplates.common.plugin.CustomPlugin;
import net.momirealms.customnameplates.common.plugin.scheduler.SchedulerTask;
import net.momirealms.customnameplates.common.sender.SenderFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class CustomNameplates implements CustomPlugin {

    private static CustomNameplates instance;

    protected TrackerManager trackerManager;
    protected DependencyManager dependencyManager;
    protected TranslationManager translationManager;
    protected Consumer<Supplier<String>> debugger = (s) -> getPluginLogger().info("[DEBUG] " + s.get());
    protected ConfigManager configManager;
    protected PacketSender packetSender;
    protected PlaceholderManager placeholderManager;
    protected RequirementManager requirementManager;
    protected ActionBarManager actionBarManager;
    protected Platform platform;
    protected ConcurrentHashMap<UUID, CNPlayer<?>> onlinePlayerMap = new ConcurrentHashMap<>();
    protected MainTask mainTask = new MainTask(this);
    protected SchedulerTask scheduledMainTask;

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

    public TrackerManager getTrackerManager() {
        return trackerManager;
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

    public Platform getPlatform() {
        return platform;
    }

    public Collection<CNPlayer<?>> getOnlinePlayers() {
        return new HashSet<>(onlinePlayerMap.values());
    }

    public CNPlayer<?> getPlayer(UUID uuid) {
        return onlinePlayerMap.get(uuid);
    }

    public static CustomNameplates getInstance() {
        return instance;
    }
}
