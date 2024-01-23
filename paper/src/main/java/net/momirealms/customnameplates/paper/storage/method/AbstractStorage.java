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

package net.momirealms.customnameplates.paper.storage.method;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.data.DataStorageInterface;
import net.momirealms.customnameplates.api.data.PlayerData;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * An abstract class that implements the DataStorageInterface and provides common functionality for data storage.
 */
public abstract class AbstractStorage implements DataStorageInterface {

    protected CustomNameplatesPlugin plugin;

    public AbstractStorage(CustomNameplatesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        // This method can be overridden in subclasses to perform initialization tasks specific to the storage type.
    }

    @Override
    public void disable() {
        // This method can be overridden in subclasses to perform cleanup or shutdown tasks specific to the storage type.
    }

    @Override
    public CompletableFuture<Boolean> updateOrInsertPlayerData(UUID uuid, PlayerData playerData) {
        // By default, delegate to the updatePlayerData method to update or insert player data.
        return updatePlayerData(uuid, playerData);
    }
}
