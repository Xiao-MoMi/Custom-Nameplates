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

package net.momirealms.customnameplates.bukkit.util;

import net.momirealms.customnameplates.api.helper.VersionHelper;

import java.util.List;

public class EntityData<T> {

    private final int id;
    private final Object serializer;
    private final T defaultValue;

    // Entity
    public static final EntityData<Byte> EntityMasks = of(0, EntityDataValue.Serializers$BYTE, (byte) 0);

    // Display only
    public static final EntityData<Integer> InterpolationDelay = of(8, EntityDataValue.Serializers$INT, 0);

    // 1.19.4-1.20.1
    public static final EntityData<Integer> InterpolationDuration = of(9, EntityDataValue.Serializers$INT, 0);

    // 1.20.2+
    public static final EntityData<Integer> TransformationInterpolationDuration = of(9, EntityDataValue.Serializers$INT, 0);
    public static final EntityData<Integer> PositionRotationInterpolationDuration = of(10, EntityDataValue.Serializers$INT, 0);

    public static final EntityData<Object> Translation = of(11, EntityDataValue.Serializers$VECTOR3, Reflections.instance$Vector3f$None);
    public static final EntityData<Object> Scale = of(12, EntityDataValue.Serializers$VECTOR3, Reflections.instance$Vector3f$None);
    public static final EntityData<Object> RotationLeft = of(13, EntityDataValue.Serializers$QUATERNION, Reflections.instance$Quaternionf$None);
    public static final EntityData<Object> RotationRight = of(14, EntityDataValue.Serializers$QUATERNION, Reflections.instance$Quaternionf$None);
    public static final EntityData<Byte> BillboardConstraints = of(15, EntityDataValue.Serializers$BYTE, (byte) 0);
    public static final EntityData<Integer> BrightnessOverride = of(16, EntityDataValue.Serializers$INT, -1);
    public static final EntityData<Float> ViewRange = of(17, EntityDataValue.Serializers$FLOAT, 1f);
    public static final EntityData<Float> ShadowRadius = of(18, EntityDataValue.Serializers$FLOAT, 0f);
    public static final EntityData<Float> ShadowStrength = of(19, EntityDataValue.Serializers$FLOAT, 0f);
    public static final EntityData<Float> Width = of(20, EntityDataValue.Serializers$FLOAT, 0f);
    public static final EntityData<Float> Height = of(21, EntityDataValue.Serializers$FLOAT, 0f);
    public static final EntityData<Integer> GlowColorOverride = of(22, EntityDataValue.Serializers$INT, -1);

    // Text display only
    public static final EntityData<Object> Text = of(23, EntityDataValue.Serializers$COMPONENT, Reflections.instance$Component$empty);
    public static final EntityData<Integer> LineWidth = of(24, EntityDataValue.Serializers$INT, 200);
    public static final EntityData<Integer> BackgroundColor = of(25, EntityDataValue.Serializers$INT, 0x40000000);
    public static final EntityData<Byte> TextOpacity = of(26, EntityDataValue.Serializers$BYTE, (byte) -1);
    public static final EntityData<Byte> TextDisplayMasks = of(27, EntityDataValue.Serializers$BYTE, (byte) 0);

    public static <T> EntityData<T> of(final int id, final Object serializer, T defaultValue) {
        return new EntityData<>(id, serializer, defaultValue);
    }

    public EntityData(int id, Object serializer, T defaultValue) {
        if (!VersionHelper.isVersionNewerThan1_20_2()) {
            if (id >= 11) {
                id--;
            }
        }
        this.id = id;
        this.serializer = serializer;
        this.defaultValue = defaultValue;
    }

    public Object serializer() {
        return serializer;
    }

    public int id() {
        return id;
    }

    public T defaultValue() {
        return defaultValue;
    }

    public Object createEntityDataIfNotDefaultValue(T value) {
        if (defaultValue().equals(value)) return null;
        return EntityDataValue.create(id, serializer, value);
    }

    public void addEntityDataIfNotDefaultValue(T value, List<Object> list) {
        if (defaultValue().equals(value)) return;
        list.add(EntityDataValue.create(id, serializer, value));
    }

    public void addEntityData(T value, List<Object> list) {
        list.add(EntityDataValue.create(id, serializer, value));
    }

    private static final int HAS_SHADOW = 0x01; // 1
    private static final int IS_SEE_THROUGH = 0x02; // 2
    private static final int USE_DEFAULT_BACKGROUND = 0x04; // 4
    private static final int LEFT_ALIGNMENT = 0x08; // 8
    private static final int RIGHT_ALIGNMENT = 0x10; // 16

    public static byte encodeMask(boolean hasShadow, boolean isSeeThrough, boolean useDefaultBackground, int alignment) {
        int bitMask = 0;

        if (hasShadow) {
            bitMask |= HAS_SHADOW;
        }
        if (isSeeThrough) {
            bitMask |= IS_SEE_THROUGH;
        }
        if (useDefaultBackground) {
            bitMask |= USE_DEFAULT_BACKGROUND;
        }

        switch (alignment) {
            case 0: // CENTER
                break;
            case 1: // LEFT
                bitMask |= LEFT_ALIGNMENT;
                break;
            case 2: // RIGHT
                bitMask |= RIGHT_ALIGNMENT;
                break;
            default:
                throw new IllegalArgumentException("Invalid alignment value");
        }

        return (byte) bitMask;
    }

    private static final int IS_ON_FIRE = 0x01;            // 1
    private static final int IS_CROUCHING = 0x02;          // 2
    private static final int UNUSED = 0x04;                // 4
    private static final int IS_SPRINTING = 0x08;          // 8
    private static final int IS_SWIMMING = 0x10;           // 16
    private static final int IS_INVISIBLE = 0x20;          // 32
    private static final int HAS_GLOWING_EFFECT = 0x40;    // 64
    private static final int IS_FLYING_WITH_ELYTRA = 0x80; // 128

    public static byte encodeMask(boolean isOnFire, boolean isCrouching, boolean isUnused,
                                      boolean isSprinting, boolean isSwimming, boolean isInvisible,
                                      boolean hasGlowingEffect, boolean isFlyingWithElytra) {
        int bitMask = 0;

        if (isOnFire) {
            bitMask |= IS_ON_FIRE;
        }
        if (isCrouching) {
            bitMask |= IS_CROUCHING;
        }
        if (isUnused) {
            bitMask |= UNUSED;
        }
        if (isSprinting) {
            bitMask |= IS_SPRINTING;
        }
        if (isSwimming) {
            bitMask |= IS_SWIMMING;
        }
        if (isInvisible) {
            bitMask |= IS_INVISIBLE;
        }
        if (hasGlowingEffect) {
            bitMask |= HAS_GLOWING_EFFECT;
        }
        if (isFlyingWithElytra) {
            bitMask |= IS_FLYING_WITH_ELYTRA;
        }

        return (byte) bitMask;
    }

    public static boolean isCrouching(byte mask) {
        return (mask & IS_CROUCHING) != 0;
    }
}
