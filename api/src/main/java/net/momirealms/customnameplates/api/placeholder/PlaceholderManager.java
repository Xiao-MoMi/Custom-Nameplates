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

package net.momirealms.customnameplates.api.placeholder;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface PlaceholderManager extends Reloadable {

    void refreshPlaceholders();

    int getRefreshInterval(String id);

    <T extends Placeholder> T registerPlaceholder(T placeholder);

    SharedPlaceholder registerSharedPlaceholder(
            String id,
            int refreshInterval,
            Supplier<String> contextSupplier
    );

    default SharedPlaceholder registerSharedPlaceholder(
            String id,
            Supplier<String> contextSupplier
    ) {
        return registerSharedPlaceholder(id, getRefreshInterval(id), contextSupplier);
    }

    PlayerPlaceholder registerPlayerPlaceholder(
            String id,
            int refreshInterval,
            Function<CNPlayer, String> function
    );

    default PlayerPlaceholder registerPlayerPlaceholder(
            String id,
            Function<CNPlayer, String> function
    ) {
        return registerPlayerPlaceholder(id, getRefreshInterval(id), function);
    }

    RelationalPlaceholder registerRelationalPlaceholder(
            String id,
            int refreshInterval,
            BiFunction<CNPlayer, CNPlayer, String> function
    );

    default RelationalPlaceholder registerRelationalPlaceholder(
            String id,
            BiFunction<CNPlayer, CNPlayer, String> function
    ) {
        return registerRelationalPlaceholder(id, getRefreshInterval(id), function);
    }

    Placeholder getPlaceholder(String id);

    Placeholder getRegisteredPlaceholder(String id);

    void unregisterPlaceholder(String id);

    default void unregisterPlaceholder(Placeholder placeholder) {
        unregisterPlaceholder(placeholder.id());
    }

    List<String> detectPlaceholders(String raw);
}
