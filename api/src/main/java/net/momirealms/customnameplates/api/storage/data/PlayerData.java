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

package net.momirealms.customnameplates.api.storage.data;

import java.util.UUID;

public interface PlayerData {

    String DEFAULT_NAMEPLATE = "none";
    String DEFAULT_BUBBLE  = "none";

    String nameplate();

    String bubble();

    UUID uuid();

    static Builder builder() {
        return new PlayerDataImpl.BuilderImpl();
    }

    static PlayerData empty(UUID uuid) {
        return builder().uuid(uuid)
                .nameplate(DEFAULT_NAMEPLATE)
                .bubble(DEFAULT_BUBBLE)
                .build();
    }

    interface Builder {

        Builder nameplate(String nameplate);

        Builder bubble(String bubble);

        Builder uuid(UUID uuid);

        PlayerData build();
    }

    default JsonData toGsonData() {
        return new JsonData(nameplate(), bubble());
    }
}
