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

/**
 * Manages the registration, retrieval, and lifecycle of placeholders. Supports registering shared, player-specific,
 * and relational placeholders with optional refresh intervals.
 */
public interface PlaceholderManager extends Reloadable {

    /**
     * Refreshes all registered placeholders.
     */
    void refreshPlaceholders();

    /**
     * Gets the refresh interval for a placeholder by its countId.
     *
     * @param countId the countId of the placeholder
     * @return the refresh interval in ticks
     */
    int getRefreshInterval(int countId);

    /**
     * Gets the refresh interval for a placeholder by its id.
     *
     * @param id the ID of the placeholder
     * @return the refresh interval in ticks
     */
    int getRefreshInterval(String id);

    /**
     * Registers a placeholder and returns the registered instance.
     *
     * @param placeholder the placeholder to register
     * @param <T>         the type of the placeholder
     * @return the registered placeholder instance
     */
    <T extends Placeholder> T registerPlaceholder(T placeholder);

    /**
     * Registers a shared placeholder with a specific refresh interval and context supplier.
     *
     * @param id              the placeholder ID
     * @param refreshInterval the refresh interval in ticks
     * @param contextSupplier the supplier providing the placeholder's content
     * @return the registered shared placeholder
     */
    SharedPlaceholder registerSharedPlaceholder(String id, int refreshInterval, Supplier<String> contextSupplier);

    /**
     * Registers a shared placeholder using the default refresh interval.
     *
     * @param id              the placeholder ID
     * @param contextSupplier the supplier providing the placeholder's content
     * @return the registered shared placeholder
     */
    default SharedPlaceholder registerSharedPlaceholder(String id, Supplier<String> contextSupplier) {
        return registerSharedPlaceholder(id, getRefreshInterval(id), contextSupplier);
    }

    /**
     * Registers a player-specific placeholder with a specific refresh interval and function.
     *
     * @param id              the placeholder ID
     * @param refreshInterval the refresh interval in ticks
     * @param function        the function providing the placeholder's content for a specific player
     * @return the registered player placeholder
     */
    PlayerPlaceholder registerPlayerPlaceholder(String id, int refreshInterval, Function<CNPlayer, String> function);

    /**
     * Registers a player-specific placeholder using the default refresh interval.
     *
     * @param id       the placeholder ID
     * @param function the function providing the placeholder's content for a specific player
     * @return the registered player placeholder
     */
    default PlayerPlaceholder registerPlayerPlaceholder(String id, Function<CNPlayer, String> function) {
        return registerPlayerPlaceholder(id, getRefreshInterval(id), function);
    }

    /**
     * Registers a relational placeholder with a specific refresh interval and function.
     *
     * @param id              the placeholder ID
     * @param refreshInterval the refresh interval in ticks
     * @param function        the function providing the placeholder's content based on two players
     * @return the registered relational placeholder
     */
    RelationalPlaceholder registerRelationalPlaceholder(String id, int refreshInterval, BiFunction<CNPlayer, CNPlayer, String> function);

    /**
     * Registers a relational placeholder using the default refresh interval.
     *
     * @param id       the placeholder ID
     * @param function the function providing the placeholder's content based on two players
     * @return the registered relational placeholder
     */
    default RelationalPlaceholder registerRelationalPlaceholder(String id, BiFunction<CNPlayer, CNPlayer, String> function) {
        return registerRelationalPlaceholder(id, getRefreshInterval(id), function);
    }

    /**
     * Retrieves a placeholder by its ID.
     *
     * @param id the ID of the placeholder
     * @return the placeholder, or null if not found
     */
    Placeholder getPlaceholder(String id);

    /**
     * Retrieves a registered placeholder by its ID.
     *
     * @param id the ID of the placeholder
     * @return the registered placeholder, or null if not found
     */
    Placeholder getRegisteredPlaceholder(String id);

    /**
     * Unregisters a placeholder by its ID.
     *
     * @param id the ID of the placeholder to unregister
     */
    void unregisterPlaceholder(String id);

    /**
     * Unregisters the specified placeholder.
     *
     * @param placeholder the placeholder to unregister
     */
    default void unregisterPlaceholder(Placeholder placeholder) {
        unregisterPlaceholder(placeholder.id());
    }

    /**
     * Detects and returns a list of placeholder IDs present in the given raw string.
     *
     * @param raw the raw string containing potential placeholders
     * @return a list of detected placeholder IDs
     */
    List<String> detectPlaceholders(String raw);
}
