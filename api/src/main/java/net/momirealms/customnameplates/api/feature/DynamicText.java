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

public class DynamicText {

    private final String text;
    private final List<Function<CNPlayer, String>> texts;
    private final Set<Placeholder> placeholders;

    public DynamicText(String text, List<Function<CNPlayer, String>> texts, Set<Placeholder> placeholders) {
        this.text = text;
        this.texts = texts;
        this.placeholders = placeholders;
    }

    public Set<Placeholder> placeholders() {
        return placeholders;
    }

    public String render(CNPlayer viewer) {
        StringBuilder builder = new StringBuilder();
        for (Function<CNPlayer, String> function : texts) {
            builder.append(function.apply(viewer));
        }
        return builder.toString();
    }

    public String text() {
        return text;
    }
}
