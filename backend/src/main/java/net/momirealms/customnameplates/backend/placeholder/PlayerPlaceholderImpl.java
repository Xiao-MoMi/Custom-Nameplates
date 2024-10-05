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

package net.momirealms.customnameplates.backend.placeholder;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.placeholder.AbstractPlaceholder;
import net.momirealms.customnameplates.api.placeholder.PlaceholderManager;
import net.momirealms.customnameplates.api.placeholder.PlayerPlaceholder;

import java.util.function.Function;

public class PlayerPlaceholderImpl extends AbstractPlaceholder implements PlayerPlaceholder {

    private final Function<CNPlayer, String> function;

    protected PlayerPlaceholderImpl(PlaceholderManager manager, String id, int refreshInterval, Function<CNPlayer, String> function) {
        super(manager, id, refreshInterval);
        this.function = function;
    }

    @Override
    public String request(CNPlayer player) {
        return function.apply(player);
    }
}
