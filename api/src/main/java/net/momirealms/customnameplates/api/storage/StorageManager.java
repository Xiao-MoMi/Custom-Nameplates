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

package net.momirealms.customnameplates.api.storage;

import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface StorageManager extends Reloadable {

	@NotNull
	String getServerID();

	@NotNull
	DataStorageProvider getDataSource();

	boolean isRedisEnabled();

	byte[] toBytes(@NotNull PlayerData data);

	@NotNull
	String toJson(@NotNull PlayerData data);

	@NotNull
	PlayerData fromJson(UUID uuid, String json);

	@NotNull
	PlayerData fromBytes(UUID uuid, byte[] data);
}
