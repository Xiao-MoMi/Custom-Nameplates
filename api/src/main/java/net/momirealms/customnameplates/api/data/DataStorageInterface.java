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

package net.momirealms.customnameplates.api.data;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface DataStorageInterface {

    /**
     * Initialize the data resource
     */
    void initialize();

    /**
     * Close the data resource
     */
    void disable();

    /**
     * Get the storage data source type
     *
     * @return {@link StorageType}
     */
    StorageType getStorageType();

    CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid);

    CompletableFuture<Boolean> updatePlayerData(UUID uuid, PlayerData playerData);

    CompletableFuture<Boolean> updateOrInsertPlayerData(UUID uuid, PlayerData playerData);

    Set<UUID> getUniqueUsers(boolean legacy);
}
