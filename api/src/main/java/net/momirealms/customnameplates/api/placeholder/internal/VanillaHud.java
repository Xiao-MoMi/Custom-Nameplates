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
import net.momirealms.customnameplates.api.feature.OffsetFont;
import net.momirealms.customnameplates.api.feature.PreParsedDynamicText;
import net.momirealms.customnameplates.api.feature.image.Image;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class VanillaHud {

    private final String empty;
    private final String half;
    private final String full;
    private final boolean reverse;
    private final PreParsedDynamicText current;
    private final PreParsedDynamicText max;

    public VanillaHud(Image empty, Image half, Image full, boolean reverse, String current, String max) {
        this.empty = String.valueOf(empty.character().character()) + OffsetFont.NEG_2.character();
        this.half = String.valueOf(half.character().character()) + OffsetFont.NEG_2.character();
        this.full = String.valueOf(full.character().character()) + OffsetFont.NEG_2.character();
        this.reverse = reverse;
        this.current = new PreParsedDynamicText(current);
        this.max = new PreParsedDynamicText(max);
    }

    public PreParsedDynamicText getCurrent() {
        return current;
    }

    public PreParsedDynamicText getMax() {
        return max;
    }

    public String create() {
        return create(current.fastCreate(null).render(null), max.fastCreate(null).render(null));
    }

    public String create(CNPlayer player) {
        return create(current.fastCreate(player).render(player), max.fastCreate(player).render(player));
    }

    public String create(CNPlayer p1, CNPlayer p2) {
        return create(current.fastCreate(p1).render(p2), max.fastCreate(p1).render(p2));
    }

    private String create(String current, String max) {
        double currentDouble;
        double maxDouble;
        try {
            currentDouble= Double.parseDouble(current);
            maxDouble = Double.parseDouble(max);
        } catch (NumberFormatException e) {
            currentDouble = 1;
            maxDouble = 1;
            CustomNameplates.getInstance().getPluginLogger().warn("Invalid number format when parsing: " + current + "/" + max);
        }
        if (currentDouble >= maxDouble) currentDouble = maxDouble;
        if (currentDouble < 0) currentDouble = 0;
        int point = (int) ((currentDouble / maxDouble) * 20);
        int full_amount = point / 2;
        int half_amount = point % 2;
        int empty_amount = 10 - full_amount - half_amount;
        StringBuilder builder = new StringBuilder();
        if (reverse) {
            builder
                    .append(String.valueOf(empty).repeat(empty_amount))
                    .append(String.valueOf(half).repeat(half_amount))
                    .append(String.valueOf(full).repeat(full_amount));
        } else {
            builder
                    .append(String.valueOf(full).repeat(full_amount))
                    .append(String.valueOf(half).repeat(half_amount))
                    .append(String.valueOf(empty).repeat(empty_amount));
        }
        return AdventureHelper.surroundWithNameplatesFont(builder.toString());
    }
}
