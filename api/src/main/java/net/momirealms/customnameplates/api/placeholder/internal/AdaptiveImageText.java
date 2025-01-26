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

package net.momirealms.customnameplates.api.placeholder.internal;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.CustomNameplatesAPI;
import net.momirealms.customnameplates.api.feature.AdaptiveImage;
import net.momirealms.customnameplates.api.feature.OffsetFont;
import net.momirealms.customnameplates.api.feature.PreParsedDynamicText;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents an adaptive image combined with dynamic text, allowing
 * for the generation of image strings with custom margins, and dynamically rendered text.
 * <p>
 * This class provides methods to create the full image and text output, including
 * any necessary font and shadow adjustments based on specific settings.
 * </p>
 *
 * @param <T> the type of {@link AdaptiveImage} associated with this class
 */
@ApiStatus.Internal
public class AdaptiveImageText<T extends AdaptiveImage> {
    private final String id;
    private final String text;
    private final T t;
    private final boolean removeShadowOld;
    private final boolean removeShadowNew;
    private final PreParsedDynamicText preParsedDynamicText;
    private final int leftMargin;
    private final int rightMargin;

    /**
     * Constructs an instance of {@link AdaptiveImageText} with the given parameters.
     *
     * @param id the unique identifier for this adaptive image text
     * @param text the base text to be used in the image
     * @param t the {@link AdaptiveImage} object that provides the image
     * @param removeShadowOld flag to indicate whether to remove shadow using old method
     * @param removeShadowNew flag to indicate whether to remove shadow using new method
     * @param leftMargin the left margin for positioning the image
     * @param rightMargin the right margin for positioning the image
     */
    public AdaptiveImageText(String id, String text, T t, boolean removeShadowOld, boolean removeShadowNew, int rightMargin, int leftMargin) {
        this.text = text;
        this.id = id;
        this.t = t;
        this.removeShadowOld = removeShadowOld;
        this.removeShadowNew = removeShadowNew;
        this.preParsedDynamicText = new PreParsedDynamicText(text);
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
    }

    /**
     * Creates an instance of {@link AdaptiveImageText} with the specified parameters.
     *
     * @param id the unique identifier for this adaptive image text
     * @param text the base text to be used in the image
     * @param t the {@link AdaptiveImage} object that provides the image
     * @param removeShadowOld flag to indicate whether to remove shadow using old method
     * @param removeShadowNew flag to indicate whether to remove shadow using new method
     * @param leftMargin the left margin for positioning the image
     * @param rightMargin the right margin for positioning the image
     * @param <T> the type of {@link AdaptiveImage} associated with this class
     * @return a new instance of {@link AdaptiveImageText}
     */
    public static <T extends AdaptiveImage> AdaptiveImageText<T> create(String id, final String text, final T t, final boolean removeShadowOld, final boolean removeShadowNew, int leftMargin, int rightMargin) {
        return new AdaptiveImageText<>(id, text, t, removeShadowOld, removeShadowNew, rightMargin, leftMargin);
    }

    /**
     * Gets the pre-parsed dynamic text associated with this adaptive image text.
     *
     * @return the pre-parsed dynamic text
     */
    public PreParsedDynamicText getPreParsedDynamicText() {
        return preParsedDynamicText;
    }

    /**
     * Gets the unique identifier for this adaptive image text.
     *
     * @return the unique identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the base text associated with this adaptive image text.
     *
     * @return the base text
     */
    public String getText() {
        return text;
    }

    /**
     * Generates the full string of text with both the prefix and suffix of the image,
     * rendering dynamic content and applying font and shadow adjustments.
     *
     * @param p1 the first {@link CNPlayer} used for dynamic text rendering
     * @param p2 the second {@link CNPlayer} used for dynamic text rendering
     * @return the full image and text string
     */
    @NotNull
    public String getFull(CNPlayer p1, CNPlayer p2) {
        String parsed = preParsedDynamicText.fastCreate(p1).render(p2);
        if (parsed.isEmpty()) return "";
        float advance = CustomNameplates.getInstance().getAdvanceManager().getLineAdvance(parsed);
        String prefix = t.createImagePrefix(advance, leftMargin, rightMargin);
        String suffix = t.createImageSuffix(advance, leftMargin, rightMargin);
        String prefixWithFont = AdventureHelper.surroundWithNameplatesFont(prefix);
        String suffixWithFont = AdventureHelper.surroundWithNameplatesFont(suffix);
        if (removeShadowOld) {
            prefixWithFont = AdventureHelper.removeShadowTricky(prefixWithFont);
            suffixWithFont = AdventureHelper.removeShadowTricky(suffixWithFont);
        } else if (removeShadowNew) {
            prefixWithFont = AdventureHelper.removeShadow(prefixWithFont);
            suffixWithFont = AdventureHelper.removeShadow(suffixWithFont);
        }
        return prefixWithFont + parsed + suffixWithFont;
    }

    /**
     * Generates the image string with dynamic content applied, rendering
     * the image and applying font and shadow adjustments.
     *
     * @param p1 the first {@link CNPlayer} used for dynamic text rendering
     * @param p2 the second {@link CNPlayer} used for dynamic text rendering
     * @return the generated image string
     */
    @NotNull
    public String getImage(CNPlayer p1, CNPlayer p2) {
        String parsed = preParsedDynamicText.fastCreate(p1).render(p2);
        if (parsed.isEmpty()) return "";
        float advance = CustomNameplates.getInstance().getAdvanceManager().getLineAdvance(parsed);
        String image = t.createImage(advance, leftMargin, rightMargin);
        String imageWithFont = AdventureHelper.surroundWithNameplatesFont(image);
        if (removeShadowOld) imageWithFont = AdventureHelper.removeShadowTricky(imageWithFont);
        else if (removeShadowNew) imageWithFont = AdventureHelper.removeShadow(imageWithFont);
        return imageWithFont;
    }

    /**
     * Gets the rendered dynamic text for the specified players.
     *
     * @param p1 the first {@link CNPlayer} used for dynamic text rendering
     * @param p2 the second {@link CNPlayer} used for dynamic text rendering
     * @return the rendered dynamic text
     */
    @NotNull
    public String getText(CNPlayer p1, CNPlayer p2) {
        return preParsedDynamicText.fastCreate(p1).render(p2);
    }

    /**
     * Generates the text offset characters for the specified players.
     * The offsets are calculated based on the rendered text and its advance.
     *
     * @param p1 the first {@link CNPlayer} used for dynamic text rendering
     * @param p2 the second {@link CNPlayer} used for dynamic text rendering
     * @return the generated text offset characters
     */
    @NotNull
    public String getTextOffsetCharacters(CNPlayer p1, CNPlayer p2) {
        return OffsetFont.createOffsets(CustomNameplatesAPI.getInstance().getTextAdvance(preParsedDynamicText.fastCreate(p1).render(p2)));
    }
}
