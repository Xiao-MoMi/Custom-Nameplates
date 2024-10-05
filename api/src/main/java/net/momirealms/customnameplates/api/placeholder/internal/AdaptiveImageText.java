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
import net.momirealms.customnameplates.api.feature.AdaptiveImage;
import net.momirealms.customnameplates.api.feature.PreParsedDynamicText;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class AdaptiveImageText<T extends AdaptiveImage> {

    private final String id;
    private final String text;
    private final T t;
    private final boolean removeShadow;
    private final PreParsedDynamicText preParsedDynamicText;
    private final int leftMargin;
    private final int rightMargin;

    public AdaptiveImageText(String id, String text, T t, boolean removeShadow, int leftMargin, int rightMargin) {
        this.text = text;
        this.id = id;
        this.t = t;
        this.removeShadow = removeShadow;
        this.preParsedDynamicText = new PreParsedDynamicText(text);
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
    }

    public static <T extends AdaptiveImage> AdaptiveImageText<T> create(String id, final String text, final T t, final boolean removeShadow, int leftMargin, int rightMargin) {
        return new AdaptiveImageText<>(id, text, t, removeShadow, leftMargin, rightMargin);
    }

    public PreParsedDynamicText getPreParsedDynamicText() {
        return preParsedDynamicText;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @NotNull
    public String getFull(CNPlayer p1, CNPlayer p2) {
        String parsed = preParsedDynamicText.fastCreate(p1).render(p2);
        if (parsed.isEmpty()) return "";
        float advance = CustomNameplates.getInstance().getAdvanceManager().getLineAdvance(parsed);
        String prefix = t.createImagePrefix(advance, leftMargin, rightMargin);
        String suffix = t.createImageSuffix(advance, leftMargin, rightMargin);
        String prefixWithFont = AdventureHelper.surroundWithNameplatesFont(prefix);
        String suffixWithFont = AdventureHelper.surroundWithNameplatesFont(suffix);
        if (removeShadow) prefixWithFont = AdventureHelper.removeShadowTricky(prefixWithFont);
        if (removeShadow) suffixWithFont = AdventureHelper.removeShadowTricky(suffixWithFont);
        return prefixWithFont + parsed + suffixWithFont;
    }

    @NotNull
    public String getImage(CNPlayer p1, CNPlayer p2) {
        String parsed = preParsedDynamicText.fastCreate(p1).render(p2);
        if (parsed.isEmpty()) return "";
        float advance = CustomNameplates.getInstance().getAdvanceManager().getLineAdvance(parsed);
        String image = t.createImage(advance, leftMargin, rightMargin);
        String imageWithFont = AdventureHelper.surroundWithNameplatesFont(image);
        if (removeShadow) imageWithFont = AdventureHelper.removeShadowTricky(imageWithFont);
        return imageWithFont;
    }

    @NotNull
    public String getText(CNPlayer p1, CNPlayer p2) {
        return preParsedDynamicText.fastCreate(p1).render(p2);
    }
}
