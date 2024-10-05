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

package net.momirealms.customnameplates.api.feature.tag;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.ApiStatus;

public interface UnlimitedTagManager extends Reloadable {

    void onTick();

    boolean isAlwaysShow();

    NameTagConfig getConfig(String name);

    NameTagConfig[] allConfigs();

    void setPreviewing(CNPlayer player, boolean preview);

    int previewDuration();

    TagRenderer getTagRender(CNPlayer owner);

    @ApiStatus.Internal
    void onAddPlayer(CNPlayer owner, CNPlayer added);

    @ApiStatus.Internal
    void onRemovePlayer(CNPlayer owner, CNPlayer removed);

    @ApiStatus.Internal
    void onPlayerDataSet(CNPlayer owner, CNPlayer viewer, boolean isCrouching);

    @ApiStatus.Internal
    void onPlayerAttributeSet(CNPlayer owner, CNPlayer viewer, double scale);
}
