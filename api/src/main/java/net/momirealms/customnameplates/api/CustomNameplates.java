package net.momirealms.customnameplates.api;

import net.momirealms.customnameplates.api.feature.actionbar.ActionBarManager;
import net.momirealms.customnameplates.api.feature.bossbar.BossBarManager;
import net.momirealms.customnameplates.api.feature.nametag.UnlimitedTagManager;
import net.momirealms.customnameplates.api.network.PacketSender;
import net.momirealms.customnameplates.api.network.PipelineInjector;
import net.momirealms.customnameplates.api.placeholder.PlaceholderManager;
import net.momirealms.customnameplates.api.requirement.RequirementManager;
import net.momirealms.customnameplates.common.dependency.DependencyManager;
import net.momirealms.customnameplates.common.locale.TranslationManager;
import net.momirealms.customnameplates.common.plugin.CustomPlugin;
import net.momirealms.customnameplates.common.plugin.scheduler.SchedulerTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class CustomNameplates implements CustomPlugin {

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
