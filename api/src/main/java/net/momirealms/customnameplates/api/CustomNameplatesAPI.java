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

import net.momirealms.customnameplates.api.feature.AdaptiveImage;
import net.momirealms.customnameplates.api.feature.OffsetFont;
import net.momirealms.customnameplates.api.feature.background.Background;
import net.momirealms.customnameplates.api.feature.bubble.Bubble;
import net.momirealms.customnameplates.api.feature.nameplate.Nameplate;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.placeholder.internal.StaticPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * CustomNameplatesAPI
 */
public record CustomNameplatesAPI(CustomNameplates plugin) {
    public CustomNameplatesAPI(CustomNameplates plugin) {
        this.plugin = plugin;
        instance = this;
    }

    private static CustomNameplatesAPI instance;

    /**
     * Gets the CustomNameplates plugin instance.
     *
     * @return the CustomNameplates plugin
     */
    @Override
    public CustomNameplates plugin() {
        return plugin;
    }

    /**
     * Retrieves a player by their UUID.
     *
     * @param uuid the player's UUID
     * @return the CNPlayer object if found, otherwise null
     */
    @Nullable
    public CNPlayer getPlayer(UUID uuid) {
        return plugin.getPlayer(uuid);
    }

    /**
     * Retrieves a Background by its ID.
     *
     * @param id the background ID
     * @return an Optional containing the Background if found, or empty if not
     */
    @NotNull
    public Optional<Background> getBackground(@NotNull String id) {
        return Optional.ofNullable(plugin.getBackgroundManager().backgroundById(id));
    }

    /**
     * Retrieves a Nameplate by its ID.
     *
     * @param id the nameplate ID
     * @return an Optional containing the Nameplate if found, or empty if not
     */
    @NotNull
    public Optional<Nameplate> getNameplate(@NotNull String id) {
        return Optional.ofNullable(plugin.getNameplateManager().nameplateById(id));
    }

    /**
     * Retrieves a Bubble by its ID.
     *
     * @param id the bubble ID
     * @return an Optional containing the Bubble if found, or empty if not
     */
    @NotNull
    public Optional<Bubble> getBubble(@NotNull String id) {
        return Optional.ofNullable(plugin.getBubbleManager().bubbleById(id));
    }

    /**
     * Creates a text string with an image, applying margins around the image.
     *
     * @param text          the text to be displayed
     * @param adaptiveImage the image to insert with the text
     * @param leftMargin    the margin to apply on the left of the image
     * @param rightMargin   the margin to apply on the right of the image
     * @return the resulting string with the image and margins
     */
    @NotNull
    public String createTextWithImage(@NotNull String text, @NotNull AdaptiveImage adaptiveImage, float leftMargin, float rightMargin) {
        if (AdventureHelper.legacySupport) {
            text = AdventureHelper.legacyToMiniMessage(text);
        }
        float advance = plugin.getAdvanceManager().getLineAdvance(text);
        return adaptiveImage.createImagePrefix(advance, leftMargin, rightMargin) + text + adaptiveImage.createImageSuffix(advance, leftMargin, rightMargin);
    }

    /**
     * Gets the width advance (text length in visual representation) for a given text.
     *
     * @param text the text to measure
     * @return the advance width of the text
     */
    public float getTextAdvance(@NotNull String text) {
        return plugin.getAdvanceManager().getLineAdvance(text);
    }

    /**
     * Creates a statically positioned text, aligning it to the left, right, or middle
     * of the specified width.
     *
     * @param text     the text to be positioned
     * @param width    the total width for the text positioning
     * @param position the desired static position (LEFT, RIGHT, MIDDLE)
     * @return the resulting string with the appropriate positioning
     */
    @NotNull
    public String createStaticText(@NotNull String text, int width, @NotNull StaticPosition position) {
        float parsedWidth = CustomNameplates.getInstance().getAdvanceManager().getLineAdvance(text);
        switch (position) {
            case LEFT -> {
                return text + AdventureHelper.surroundWithNameplatesFont(OffsetFont.createOffsets(width - parsedWidth));
            }
            case RIGHT -> {
                return AdventureHelper.surroundWithNameplatesFont(OffsetFont.createOffsets(width - parsedWidth)) + text;
            }
            case MIDDLE -> {
                int half = (int) ((width - parsedWidth) / 2);
                String left = AdventureHelper.surroundWithNameplatesFont(OffsetFont.createOffsets(half));
                String right = AdventureHelper.surroundWithNameplatesFont(OffsetFont.createOffsets(width - parsedWidth - half));
                return left + text + right;
            }
            default -> {
                return "";
            }
        }
    }

    /**
     * Returns the singleton instance of CustomNameplatesAPI.
     * Throws an exception if the API has not been initialized.
     *
     * @return the CustomNameplatesAPI instance
     */
    public static CustomNameplatesAPI getInstance() {
        if (instance == null) {
            throw new RuntimeException("Nameplates API has not been initialized yet.");
        }
        return instance;
    }
}
