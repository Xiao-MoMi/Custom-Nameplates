package net.momirealms.customnameplates.api;

import net.momirealms.customnameplates.api.feature.bossbar.BossBar;
import net.momirealms.customnameplates.api.network.PacketEvent;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.api.util.Alignment;
import net.momirealms.customnameplates.api.util.Vector3;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface Platform {

    Object jsonToMinecraftComponent(String json);

    String minecraftComponentToJson(Object component);

    Placeholder registerPlatformPlaceholder(String id);

    void sendActionBar(CNPlayer player, Object component);

    void createBossBar(CNPlayer player, UUID uuid, Object component, float progress, BossBar.Overlay overlay, BossBar.Color color);

    void removeBossBar(CNPlayer player, UUID uuid);

    void updateBossBarName(CNPlayer player, UUID uuid, Object component);

    void onPacketSend(CNPlayer player, PacketEvent event);

    void createTextDisplay(
            CNPlayer player,
            int entityID, UUID uuid,
            Vector3 position, float pitch, float yaw, double headYaw,
            Object component, int backgroundColor, byte opacity,
            boolean hasShadow, boolean isSeeThrough, boolean useDefaultBackgroundColor, Alignment alignment,
            float viewRange, float shadowRadius, float shadowStrength,
            Vector3 scale, Vector3 translation, int lineWidth, boolean isCrouching
    );

    Consumer<List<Object>> createTextComponentModifier(Object component);

    void updateTextDisplay(CNPlayer player, int entityID, List<Consumer<List<Object>>> modifiers);

    void setPassengers(CNPlayer player, int vehicle, int[] passengers);

    void removeEntity(CNPlayer player, int... entityID);

    Object vec3(double x, double y, double z);
}
