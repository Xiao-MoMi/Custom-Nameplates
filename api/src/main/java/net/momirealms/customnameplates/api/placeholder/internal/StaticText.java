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
import net.momirealms.customnameplates.api.CustomNameplatesAPI;
import net.momirealms.customnameplates.api.feature.PreParsedDynamicText;
import org.jetbrains.annotations.ApiStatus;

/**
 * Class representing a static text element with customizable width, position, and content.
 */
@ApiStatus.Internal
public class StaticText {
    private final int width;
    private final StaticPosition position;
    private final PreParsedDynamicText text;

    /**
     * Constructs a new StaticText instance.
     *
     * @param width the width of the text element (e.g., for layout purposes)
     * @param position the static position of the text element (LEFT, RIGHT, MIDDLE)
     * @param text the text content to be displayed, which can contain placeholders for dynamic rendering
     */
    public StaticText(int width, StaticPosition position, String text) {
        this.width = width;
        this.position = position;
        this.text = new PreParsedDynamicText(text);
    }

    /**
     * Gets the width of the text element.
     *
     * @return the width of the text element
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the static position of the text element.
     *
     * @return the position of the text element (LEFT, RIGHT, MIDDLE)
     */
    public StaticPosition getPosition() {
        return position;
    }

    /**
     * Gets the pre-parsed dynamic text associated with this static text element.
     *
     * @return the PreParsedDynamicText instance
     */
    public PreParsedDynamicText getText() {
        return text;
    }

    /**
     * Creates and renders the static text element, using null as the player context.
     * <p>
     * This method can be useful when rendering static text without needing player-specific dynamic values.
     *
     * @return the rendered static text as a String
     */
    public String create() {
        return create(text.fastCreate(null).render(null));
    }

    /**
     * Creates and renders the static text element for a specific player context.
     *
     * @param player the player whose context will be used for dynamic text rendering
     * @return the rendered static text as a String
     */
    public String create(CNPlayer player) {
        return create(text.fastCreate(player).render(player));
    }

    /**
     * Creates and renders the static text element with two player contexts (for relational rendering).
     *
     * @param p1 the first player whose context will be used for dynamic text rendering
     * @param p2 the second player whose context will be used for relational rendering
     * @return the rendered static text as a String
     */
    public String create(CNPlayer p1, CNPlayer p2) {
        return create(text.fastCreate(p1).render(p2));
    }

    /**
     * A helper method to create and render the static text element with a given plain text.
     * <p>
     * This method will pass the rendered text, width, and position to the API for final rendering.
     *
     * @param text the plain text to be rendered
     * @return the rendered static text as a String
     */
    public String create(String text) {
        return CustomNameplatesAPI.getInstance().createStaticText(text, width, position);
    }
}
