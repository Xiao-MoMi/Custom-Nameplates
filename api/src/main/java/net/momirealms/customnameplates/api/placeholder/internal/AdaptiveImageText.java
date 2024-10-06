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
    private final boolean removeShadowOld;
    private final boolean removeShadowNew;
    private final PreParsedDynamicText preParsedDynamicText;
    private final int leftMargin;
    private final int rightMargin;

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

    public static <T extends AdaptiveImage> AdaptiveImageText<T> create(String id, final String text, final T t, final boolean removeShadowOld, final boolean removeShadowNew, int leftMargin, int rightMargin) {
        return new AdaptiveImageText<>(id, text, t, removeShadowOld, removeShadowNew, rightMargin, leftMargin);
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
        if (removeShadowOld) {
            prefixWithFont = AdventureHelper.removeShadowTricky(prefixWithFont);
            suffixWithFont = AdventureHelper.removeShadowTricky(suffixWithFont);
        } else if (removeShadowNew) {
            prefixWithFont = AdventureHelper.removeShadow(prefixWithFont);
            suffixWithFont = AdventureHelper.removeShadow(suffixWithFont);
        }
        return prefixWithFont + parsed + suffixWithFont;
    }

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

    @NotNull
    public String getText(CNPlayer p1, CNPlayer p2) {
        return preParsedDynamicText.fastCreate(p1).render(p2);
    }
}
