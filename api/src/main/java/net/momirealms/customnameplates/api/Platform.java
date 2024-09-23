package net.momirealms.customnameplates.api;

import net.momirealms.customnameplates.api.feature.bossbar.BossBar;
import net.momirealms.customnameplates.api.placeholder.Placeholder;

import java.util.UUID;

public interface Platform {

    Object jsonToMinecraftComponent(String json);

    String minecraftComponentToJson(Object component);

    Placeholder registerPlatformPlaceholder(String id);

    void sendActionBar(CNPlayer<?> player, Object component);

    void createBossBar(CNPlayer<?> player, UUID uuid, Object component, float progress, BossBar.Overlay overlay, BossBar.Color color);

    void removeBossBar(CNPlayer<?> player, UUID uuid);

    void updateBossBarName(CNPlayer<?> player, UUID uuid, Object component);

    void onPacketSend(CNPlayer<?> player, Object packet);
}
