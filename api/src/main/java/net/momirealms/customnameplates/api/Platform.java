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
import net.momirealms.customnameplates.api.feature.tag.NameTagConfig;
import net.momirealms.customnameplates.api.network.PacketEvent;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.api.util.Alignment;
import net.momirealms.customnameplates.api.util.Vector3;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Platform interface to abstract platform-specific operations for Minecraft components, packets, and placeholders.
 */
public interface Platform {

    /**
     * Converts a JSON string to a Minecraft component.
     *
     * @param json the JSON string
     * @return the Minecraft component object
     */
    Object jsonToMinecraftComponent(String json);

    /**
     * Converts a Minecraft component to a JSON string.
     *
     * @param component the Minecraft component
     * @return the JSON string representation
     */
    String minecraftComponentToJson(Object component);

    /**
     * Creates a vector object from the specified coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return a vector object
     */
    Object vec3(double x, double y, double z);

    /**
     * Registers a platform-specific placeholder.
     *
     * @param id the placeholder ID
     * @return the registered {@link Placeholder} object
     */
    Placeholder registerPlatformPlaceholder(String id);

    /**
     * Handles an event triggered when a packet is sent to a player.
     *
     * @param player the player receiving the packet
     * @param event  the packet event
     */
    void onPacketSend(CNPlayer player, PacketEvent event);

    /**
     * Creates an action bar text packet for displaying a message in the action bar.
     *
     * @param component the component containing the text
     * @return the action bar packet object
     */
    Object setActionBarTextPacket(Object component);

    /**
     * Creates a boss bar packet for displaying a boss bar to a player.
     *
     * @param uuid      the UUID of the boss bar
     * @param component the component containing the boss bar text
     * @param progress  the boss bar progress (0.0 to 1.0)
     * @param overlay   the boss bar overlay style
     * @param color     the boss bar color
     * @return the boss bar packet object
     */
    Object createBossBarPacket(UUID uuid, Object component, float progress, BossBar.Overlay overlay, BossBar.Color color);

    /**
     * Creates a packet to remove a boss bar.
     *
     * @param uuid the UUID of the boss bar to remove
     * @return the remove boss bar packet object
     */
    Object removeBossBarPacket(UUID uuid);

    /**
     * Creates a packet to update the name of a boss bar.
     *
     * @param uuid      the UUID of the boss bar
     * @param component the component containing the new name
     * @return the update boss bar name packet object
     */
    Object updateBossBarNamePacket(UUID uuid, Object component);

    /**
     * Creates packets to display text on a custom text display entity.
     *
     * @param entityID                           the entity ID
     * @param uuid                               the entity UUID
     * @param position                           the position of the text
     * @param pitch                              the pitch of the entity
     * @param yaw                                the yaw of the entity
     * @param headYaw                            the head yaw of the entity
     * @param interpolationDelay                 the interpolation delay
     * @param transformationInterpolationDuration the transformation interpolation duration
     * @param positionRotationInterpolationDuration the position and rotation interpolation duration
     * @param component                          the component containing the text
     * @param backgroundColor                    the background color
     * @param opacity                            the opacity of the text
     * @param hasShadow                          whether the text has a shadow
     * @param isSeeThrough                       whether the text is see-through
     * @param useDefaultBackgroundColor          whether the default background color is used
     * @param alignment                          the alignment of the text
     * @param viewRange                          the view range of the text
     * @param shadowRadius                       the shadow radius
     * @param shadowStrength                     the shadow strength
     * @param scale                              the scale of the text
     * @param translation                        the translation of the text
     * @param lineWidth                          the width of the text line
     * @param isCrouching                        whether the entity is crouching
     * @return a list of packets for creating the text display
     */
    List<Object> createTextDisplayPacket(
            int entityID, UUID uuid,
            Vector3 position, float pitch, float yaw, double headYaw,
            int interpolationDelay, int transformationInterpolationDuration, int positionRotationInterpolationDuration,
            Object component, int backgroundColor, byte opacity,
            boolean hasShadow, boolean isSeeThrough, boolean useDefaultBackgroundColor, Alignment alignment,
            float viewRange, float shadowRadius, float shadowStrength,
            Vector3 scale, Vector3 translation, int lineWidth, boolean isCrouching
    );

    /**
     * Creates a modifier for interpolation delay.
     *
     * @param delay the interpolation delay
     * @return a consumer that applies the modifier to a list of packets
     */
    Consumer<List<Object>> createInterpolationDelayModifier(int delay);

    /**
     * Creates a modifier for transformation interpolation duration.
     *
     * @param duration the transformation interpolation duration
     * @return a consumer that applies the modifier to a list of packets
     */
    Consumer<List<Object>> createTransformationInterpolationDurationModifier(int duration);

    /**
     * Creates a modifier to change the text component of a packet.
     *
     * @param component the new text component
     * @return a consumer that applies the modifier to a list of packets
     */
    Consumer<List<Object>> createTextComponentModifier(Object component);

    /**
     * Creates a modifier to change the scale of a text display.
     *
     * @param scale the new scale
     * @return a consumer that applies the modifier to a list of packets
     */
    Consumer<List<Object>> createScaleModifier(Vector3 scale);

    /**
     * Creates a modifier to change the translation of a text display.
     *
     * @param translation the new translation
     * @return a consumer that applies the modifier to a list of packets
     */
    Consumer<List<Object>> createTranslationModifier(Vector3 translation);

    Consumer<List<Object>> createSneakModifier(boolean isSneaking, boolean seeThrough, NameTagConfig config);

    /**
     * Updates an existing text display entity with modifiers.
     *
     * @param entityID  the entity ID of the text display
     * @param modifiers the list of modifiers to apply
     * @return the update text display packet object
     */
    Object updateTextDisplayPacket(int entityID, List<Consumer<List<Object>>> modifiers);

    /**
     * Creates a packet to set passengers for a vehicle entity.
     *
     * @param vehicle    the vehicle entity ID
     * @param passengers the passenger entity IDs
     * @return the set passengers packet object
     */
    Object setPassengersPacket(int vehicle, int[] passengers);

    /**
     * Creates a packet to remove entities from the game world.
     *
     * @param entityID the entity IDs to remove
     * @return the remove entity packet object
     */
    Object removeEntityPacket(int... entityID);

    /**
     * Gets the biome based on location
     *
     * @param world world
     * @param x x
     * @param y y
     * @param z z
     * @return the biome key
     */
    String getBiome(String world, int x, int y, int z);
}
