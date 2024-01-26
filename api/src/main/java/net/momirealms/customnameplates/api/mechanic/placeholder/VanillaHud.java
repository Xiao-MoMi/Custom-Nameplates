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
import net.momirealms.customnameplates.api.mechanic.font.OffsetFont;
import net.momirealms.customnameplates.api.util.FontUtils;
import net.momirealms.customnameplates.api.util.LogUtils;
import org.bukkit.OfflinePlayer;

public class VanillaHud {

    private String empty;
    private String half;
    private String full;
    private String maxPapi;
    private String currentPapi;
    private boolean reverse;

    private VanillaHud() {
    }

    public VanillaHud(char empty, char half, char full, String maxPapi, String currentPapi, boolean reverse) {
        this.empty = String.valueOf(empty) + OffsetFont.NEG_2.getCharacter();
        this.half = String.valueOf(half) + OffsetFont.NEG_2.getCharacter();
        this.full = String.valueOf(full) + OffsetFont.NEG_2.getCharacter();
        this.maxPapi = maxPapi;
        this.currentPapi = currentPapi;
        this.reverse = reverse;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getValue(OfflinePlayer player) {
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

    public static class Builder{

        private final VanillaHud hud;

        public Builder() {
            hud = new VanillaHud();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder full(char full) {
            hud.full = String.valueOf(full) + OffsetFont.NEG_2.getCharacter();
            return this;
        }

        public Builder half(char half) {
            hud.half = String.valueOf(half) + OffsetFont.NEG_2.getCharacter();
            return this;
        }

        public Builder empty(char empty) {
            hud.empty = String.valueOf(empty) + OffsetFont.NEG_2.getCharacter();
            return this;
        }

        public Builder reverse(boolean reverse) {
            hud.reverse = reverse;
            return this;
        }

        public Builder max(String max) {
            hud.maxPapi = max;
            return this;
        }

        public Builder current(String current) {
            hud.currentPapi = current;
            return this;
        }

        public VanillaHud build() {
            return hud;
        }
    }
}
