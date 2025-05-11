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
import net.momirealms.customnameplates.api.feature.JoinQuitListener;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * An interface manages unlimited tags
 */
public interface UnlimitedTagManager extends Reloadable, JoinQuitListener {

    /**
     * Called every tick to update the state of tags.
     */
    void onTick();

    /**
     * Checks if name tags should always be shown.
     *
     * @return true if name tags are always shown, false otherwise
     */
    boolean isAlwaysShow();

    /**
     * Retrieves the name tag configuration by its id.
     *
     * @param id the id of the tag configuration
     * @return the corresponding NameTagConfig
     */
    NameTagConfig configById(String id);

    /**
     * Returns all available name tag configurations.
     *
     * @return an array of all NameTagConfig instances
     */
    NameTagConfig[] nameTagConfigs();

    /**
     * Sets whether a player is in preview mode for tags.
     *
     * @param player  the player to update
     * @param preview true to enable preview mode, false to disable
     */
    void setTempPreviewing(CNPlayer player, boolean preview);

    /**
     * Sets whether a player can always see their tags
     *
     * @param player  the player to update
     * @param preview true to enable preview mode, false to disable
     */
    void togglePreviewing(CNPlayer player, boolean preview);

    /**
     * Returns the duration (in ticks) for which a tag preview is shown.
     *
     * @return the preview duration in ticks
     */
    int previewDuration();

    /**
     * Returns the TagRenderer responsible for rendering tags for a specific owner.
     *
     * @param owner the owner of the tags
     * @return the TagRenderer for the owner
     */
    @Nullable
    TagRenderer getTagRender(CNPlayer owner);

    /**
     * Internal method for handling when a player is added to the owner's viewer list.
     *
     * @param owner  the player who owns the tags
     * @param added  the player who is added to the viewer list
     */
    @ApiStatus.Internal
    void onAddPlayer(CNPlayer owner, CNPlayer added);

    /**
     * Internal method for handling when a player is removed from the owner's viewer list.
     *
     * @param owner   the player who owns the tags
     * @param removed the player who is removed from the viewer list
     */
    @ApiStatus.Internal
    void onRemovePlayer(CNPlayer owner, CNPlayer removed);

    /**
     * Internal method for updating a player's crouching state.
     *
     * @param owner      the player who owns the tags
     * @param viewer     the player viewing the tags
     * @param isCrouching true if the player is crouching, false otherwise
     */
    @ApiStatus.Internal
    void onPlayerDataSet(CNPlayer owner, CNPlayer viewer, boolean isCrouching);

    /**
     * Internal method for updating a player's scale attributes.
     *
     * @param owner   the player who owns the tags
     * @param viewer  the player viewing the tags
     * @param scale   the new scale value
     */
    @ApiStatus.Internal
    void onPlayerAttributeSet(CNPlayer owner, CNPlayer viewer, double scale);

    /**
     * Internal method for updating a player's game mode
     *
     * @param owner   the player who owns the tags
     * @param viewer  the player viewing the tags
     * @param isSpectator true if the player is a spectator, false otherwise
     */
    @ApiStatus.Internal
    void onPlayerGameModeChange(CNPlayer owner, CNPlayer viewer, boolean isSpectator);
}
