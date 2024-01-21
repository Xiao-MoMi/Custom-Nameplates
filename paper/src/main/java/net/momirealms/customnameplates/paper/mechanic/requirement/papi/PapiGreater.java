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

package net.momirealms.customnameplates.paper.mechanic.requirement.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public record PapiGreater(String papi, String requirement) implements PapiRequirement{

    @Override
    public boolean isMet(OfflinePlayer player) {
        double value = Double.parseDouble(PlaceholderAPI.setPlaceholders(player, papi));
        return value > Double.parseDouble(PlaceholderAPI.setPlaceholders(player, requirement));
    }
}
