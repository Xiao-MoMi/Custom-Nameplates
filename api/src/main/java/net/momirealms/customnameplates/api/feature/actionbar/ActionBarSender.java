package net.momirealms.customnameplates.api.feature.actionbar;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.feature.DynamicText;
import net.momirealms.customnameplates.api.feature.Feature;
import net.momirealms.customnameplates.api.packet.WrappedActionBarPacket;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ActionBarSender implements Feature {

    private final ActionBarManager manager;
    private final CNPlayer<?> owner;
    private long lastUpdateTime;

    private ActionBarConfig currentConfig;
    private int order;
    private int timeLeft;
    private DynamicText currentActionBar;

    private String externalActionBar;
    private long externalExpireTime;

    private String latestActionBar;

    @Override
    public String name() {
        return "ActionBarSender";
    }

    public ActionBarSender(ActionBarManager manager, CNPlayer<?> owner) {
        this.owner = owner;
        this.manager = manager;
        this.owner.addFeature(this);
        this.onConditionTimerCheck();
    }

    public void onConditionTimerCheck() {
        ActionBarConfig[] configs = manager.allConfigs();



        outer: {
            for (ActionBarConfig config : configs) {
                if (owner.isMet(config.requirements())) {
                    if (config != currentConfig) {
                        currentConfig = config;
                        order = config.carouselTexts().length - 1;
                        timeLeft = 0;
                    }
                    break outer;
                }
            }

            // no actionbar available
            currentConfig = null;
            currentActionBar = null;
            latestActionBar = null;
        }

        if (currentConfig != null) {

            if (timeLeft > 0)
                timeLeft--;

            if (timeLeft == 0) {
                int triedTimes = 0;

                do {
                    if (triedTimes == currentConfig.carouselTexts().length) {
                        timeLeft = 20;
                        latestActionBar = null;
                        CustomNameplates.getInstance().getPluginLogger().warn("No text in order is available for player " + owner.name() + ". Please check your actionbar's conditions.");
                        return;
                    }
                    order++;
                    if (order >= currentConfig.carouselTexts().length) {
                        order = 0;
                    }
                    triedTimes++;
                } while (
                        !owner.isMet(currentConfig.carouselTexts()[order].requirements())
                );

                CarouselText carouselText = currentConfig.carouselTexts()[order];
                timeLeft = carouselText.duration();
                currentActionBar = carouselText.preParsedDynamicText().fastCreate(owner);
                owner.forceUpdate(currentActionBar.placeholders());
                notifyPlaceholderUpdates(owner, false);
            }
        }
    }

    public void onHeartBeatTimer() {
        if (shouldSendBeatPacket()) {
            sendLatestActionBar();
        }
    }

    public void refresh() {
        latestActionBar = this.currentActionBar.render(owner);
    }

    public void sendLatestActionBar() {
        if (latestActionBar != null) {
            updateLastUpdateTime();
            CustomNameplates.getInstance().getPlatform().sendActionBar(owner, new WrappedActionBarPacket(AdventureHelper.miniMessageToMinecraftComponent(latestActionBar)));
        }
    }

    public void destroy() {
        this.owner.removeFeature(this);
    }

    @Override
    public Set<Placeholder> activePlaceholders() {
        if (currentActionBar == null) return Collections.emptySet();
        return new HashSet<>(currentActionBar.placeholders());
    }

    @Override
    public Set<Placeholder> allPlaceholders() {
        HashSet<Placeholder> placeholders = new HashSet<>();
        for (ActionBarConfig config : manager.allConfigs()) {
            for (CarouselText text : config.carouselTexts()) {
                placeholders.addAll(text.preParsedDynamicText().placeholders());
            }
        }
        return placeholders;
    }

    @Override
    public void notifyPlaceholderUpdates(CNPlayer<?> player, boolean force) {
        refresh();
        sendLatestActionBar();
    }

    public void updateLastUpdateTime() {
        lastUpdateTime = System.currentTimeMillis();
    }

    public boolean shouldSendBeatPacket() {
        return System.currentTimeMillis() - lastUpdateTime > 1500;
    }

    @Nullable
    public String externalActionBar() {
        if (System.currentTimeMillis() > externalExpireTime) {
            externalActionBar = null;
        }
        return externalActionBar;
    }

    public void externalActionBar(String externalActionBar) {
        this.externalActionBar = externalActionBar;
        this.externalExpireTime = System.currentTimeMillis() + 3000;
    }
}
