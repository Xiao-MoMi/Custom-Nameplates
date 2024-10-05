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

import net.momirealms.customnameplates.api.placeholder.AbstractPlaceholder;
import net.momirealms.customnameplates.api.placeholder.PlaceholderManager;
import net.momirealms.customnameplates.api.placeholder.SharedPlaceholder;

import java.util.function.Supplier;

public class SharedPlaceholderImpl extends AbstractPlaceholder implements SharedPlaceholder {

    private final Supplier<String> supplier;
    private String latestValue;

    protected SharedPlaceholderImpl(PlaceholderManager manager, String id, int refreshInterval, Supplier<String> supplier) {
        super(manager, id, refreshInterval);
        this.supplier = supplier;
    }

    @Override
    public String request() {
        return supplier.get();
    }

    @Override
    public void update() {
        latestValue = request();
    }

    @Override
    public String getLatestValue() {
        return latestValue;
    }
}
