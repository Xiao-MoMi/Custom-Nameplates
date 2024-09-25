package net.momirealms.customnameplates.api.feature.bossbar;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.feature.DynamicText;
import net.momirealms.customnameplates.api.feature.Feature;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.placeholder.Placeholder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BossBarSender implements Feature, BossBar {

    private final UUID uuid = UUID.randomUUID();
    private final CNPlayer owner;

    private boolean isShown = false;
    private final BossBarConfig config;

    private int order;
    private int timeLeft;
    private DynamicText currentBossBar;

    private String latestContent;

    public BossBarSender(CNPlayer owner, BossBarConfig config) {
        this.owner = owner;
        this.config = config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BossBarSender that = (BossBarSender) o;
        return owner == that.owner && uuid == that.uuid;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public String name() {
        return "BossBarSender";
    }

    @Override
    public float progress() {
        return config.progress();
    }

    @Override
    public Color color() {
        return config.color();
    }

    @Override
    public Overlay overlay() {
        return config.overlay();
    }

    public boolean checkConditions() {
        return owner.isMet(config.requirements());
    }

    public void init() {
        order = config.carouselTexts().length - 1;
        timeLeft = 0;
    }

    public void tick() {
        if (timeLeft > 0)
            timeLeft--;

        if (timeLeft == 0) {
            int triedTimes = 0;

            do {
                if (triedTimes == config.carouselTexts().length) {
                    timeLeft = 20;
                    currentBossBar = null;
                    CustomNameplates.getInstance().getPluginLogger().warn("No text in order is available for player " + owner.name() + ". Please check your bossbar's conditions.");
                    return;
                }
                order++;
                if (order >= config.carouselTexts().length) {
                    order = 0;
                }
                triedTimes++;
            } while (
                    !owner.isMet(config.carouselTexts()[order].requirements())
            );

            CarouselText carouselText = config.carouselTexts()[order];
            timeLeft = carouselText.duration();
            currentBossBar = carouselText.preParsedDynamicText().fastCreate(owner);

            if (carouselText.updateOnDisplay()) {
                owner.forceUpdate(currentBossBar.placeholders());
            }

            refresh();
            if (isShown()) {
                sendLatestBossBarName();
            }
        }
    }

    public void hide() {
        isShown = false;
        Object packet = CustomNameplates.getInstance().getPlatform().removeBossBarPacket(uuid);
        CustomNameplates.getInstance().getPacketSender().sendPacket(owner, packet);
    }

    public void show() {
        isShown = true;
        if (latestContent == null) {
            refresh();
        }
        Object packet = CustomNameplates.getInstance().getPlatform().createBossBarPacket(uuid, AdventureHelper.miniMessageToMinecraftComponent(latestContent), progress(), overlay(), color());
        CustomNameplates.getInstance().getPacketSender().sendPacket(owner, packet);
    }

    public boolean isShown() {
        return isShown;
    }

    @Override
    public Set<Placeholder> activePlaceholders() {
        if (currentBossBar == null || !isShown()) return Collections.emptySet();
        return currentBossBar.placeholders();
    }

    @Override
    public Set<Placeholder> allPlaceholders() {
        HashSet<Placeholder> placeholders = new HashSet<>();
        for (CarouselText text : config.carouselTexts()) {
            placeholders.addAll(text.preParsedDynamicText().placeholders());
        }
        return placeholders;
    }

    @Override
    public void notifyPlaceholderUpdates(CNPlayer p1, boolean force) {
        refresh();
        if (isShown()) {
            sendLatestBossBarName();
        }
    }

    public void refresh() {
        latestContent = this.currentBossBar.render(owner);
    }

    public void sendLatestBossBarName() {
        if (latestContent != null) {
            Object packet = CustomNameplates.getInstance().getPlatform().updateBossBarNamePacket(uuid, AdventureHelper.miniMessageToMinecraftComponent(latestContent));
            CustomNameplates.getInstance().getPacketSender().sendPacket(owner, packet);
        }
    }
}
