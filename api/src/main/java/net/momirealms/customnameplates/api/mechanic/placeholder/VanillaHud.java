/*
 *  Copyright (C) <2022> <XiaoMoMi>
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

package net.momirealms.customnameplates.api.mechanic.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.util.FontUtils;
import net.momirealms.customnameplates.api.util.LogUtils;
import org.bukkit.entity.Player;

public class VanillaHud {

    private final char empty;
    private final char half;
    private final char full;
    private final String maxPapi;
    private final String currentPapi;
    private final boolean reverse;

    public VanillaHud(String empty, String half, String full, String maxPapi, String currentPapi, boolean reverse) {
        this.empty = CustomNameplatesPlugin.get().getImageManager().getImage(empty).getCharacter();
        this.half = CustomNameplatesPlugin.get().getImageManager().getImage(half).getCharacter();
        this.full = CustomNameplatesPlugin.get().getImageManager().getImage(full).getCharacter();
        this.maxPapi = maxPapi;
        this.currentPapi = currentPapi;
        this.reverse = reverse;
    }

    public String getValue(Player player) {
        double current;
        double max;
        try {
            current= Double.parseDouble(PlaceholderAPI.setPlaceholders(player, currentPapi));
            max = Double.parseDouble(PlaceholderAPI.setPlaceholders(player, maxPapi));
        } catch (NumberFormatException e) {
            current = 1;
            max = 1;
            LogUtils.warn("Invalid number format when parsing: " + currentPapi + "/" + maxPapi);
        }
        if (current >= max) current = max;
        if (current < 0) current = 0;
        int point = (int) ((current / max) * 20);
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
        return FontUtils.surroundNameplateFont(builder.toString());
    }
}
