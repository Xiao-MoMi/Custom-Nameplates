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

    Object vec3(double x, double y, double z);

    Placeholder registerPlatformPlaceholder(String id);

    void onPacketSend(CNPlayer player, PacketEvent event);

    Object setActionBarTextPacket(Object component);

    Object createBossBarPacket(UUID uuid, Object component, float progress, BossBar.Overlay overlay, BossBar.Color color);

    Object removeBossBarPacket(UUID uuid);

    Object updateBossBarNamePacket(UUID uuid, Object component);

    List<Object> createTextDisplayPacket(
            int entityID, UUID uuid,
            Vector3 position, float pitch, float yaw, double headYaw,
            int interpolationDelay, int transformationInterpolationDuration, int positionRotationInterpolationDuration,
            Object component, int backgroundColor, byte opacity,
            boolean hasShadow, boolean isSeeThrough, boolean useDefaultBackgroundColor, Alignment alignment,
            float viewRange, float shadowRadius, float shadowStrength,
            Vector3 scale, Vector3 translation, int lineWidth, boolean isCrouching
    );

    Consumer<List<Object>> createInterpolationDelayModifier(int delay);

    Consumer<List<Object>> createTransformationInterpolationDurationModifier(int duration);

    Consumer<List<Object>> createTextComponentModifier(Object component);

    Consumer<List<Object>> createScaleModifier(Vector3 scale);

    Consumer<List<Object>> createTranslationModifier(Vector3 translation);

    Consumer<List<Object>> createOpacityModifier(byte opacity);

    Object updateTextDisplayPacket(int entityID, List<Consumer<List<Object>>> modifiers);

    Object setPassengersPacket(int vehicle, int[] passengers);

    Object removeEntityPacket(int... entityID);
}
