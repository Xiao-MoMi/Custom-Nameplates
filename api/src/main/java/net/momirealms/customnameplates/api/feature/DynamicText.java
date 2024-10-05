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

package net.momirealms.customnameplates.api.feature;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.placeholder.Placeholder;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Represents dynamic text that can be rendered for a specific player, with support for placeholders and custom text functions.
 */
public class DynamicText {

    private final String text;
    private final List<Function<CNPlayer, String>> texts;
    private final Set<Placeholder> placeholders;

    /**
     * Constructs a new DynamicText instance.
     *
     * @param text         the raw text string
     * @param texts        a list of functions that generate text dynamically based on the player
     * @param placeholders a set of placeholders used in the text
     */
    public DynamicText(String text, List<Function<CNPlayer, String>> texts, Set<Placeholder> placeholders) {
        this.text = text;
        this.texts = texts;
        this.placeholders = placeholders;
    }

    /**
     * Returns the set of placeholders associated with this dynamic text.
     *
     * @return a set of placeholders
     */
    public Set<Placeholder> placeholders() {
        return placeholders;
    }

    /**
     * Renders the dynamic text for the specified player by applying the text functions.
     *
     * @param viewer the player for whom the text is being rendered
     * @return the rendered text
     */
    public String render(CNPlayer viewer) {
        StringBuilder builder = new StringBuilder();
        for (Function<CNPlayer, String> function : texts) {
            builder.append(function.apply(viewer));
        }
        return builder.toString();
    }

    /**
     * Returns the raw text string.
     *
     * @return the raw text
     */
    public String text() {
        return text;
    }
}
