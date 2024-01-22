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

package net.momirealms.customnameplates.api.manager;

import java.util.concurrent.CompletionStage;

public interface VersionManager {

    boolean isFolia();

    String getServerVersion();

    CompletionStage<Boolean> checkUpdate();

    boolean isVersionNewerThan1_19();

    boolean isVersionNewerThan1_19_R2();

    boolean isVersionNewerThan1_19_R3();

    boolean isVersionNewerThan1_20();

    boolean isVersionNewerThan1_20_R2();

    String getPluginVersion();

    boolean isLatest();

    int getPackFormat();

}
