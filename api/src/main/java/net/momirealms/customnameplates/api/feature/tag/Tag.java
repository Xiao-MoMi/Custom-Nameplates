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
import net.momirealms.customnameplates.api.util.Vector3;

import java.util.UUID;

/**
 * Represents a tag
 */
public interface Tag {

    /**
     * Returns the ID of the tag.
     *
     * @return the tag ID
     */
    String id();

    /**
     * Returns the entity ID associated with the tag.
     *
     * @return the entity ID
     */
    int entityID();

    /**
     * Returns the UUID of the tag.
     *
     * @return the tag UUID
     */
    UUID uuid();

    /**
     * Checks if the tag is affected by crouching.
     *
     * @return true if affected by crouching, false otherwise
     */
    boolean affectedByCrouching();

    /**
     * Checks if the tag is affected by scaling.
     *
     * @return true if affected by scaling, false otherwise
     */
    boolean affectedByScaling();

    /**
     * Checks if the tag is affected by spectator mode.
     *
     * @return true if affected by spectator mode, false otherwise
     */
    boolean affectedBySpectator();

    /**
     * Hides the tag for all viewers.
     */
    void hide();

    /**
     * Hides the tag for a specific viewer.
     *
     * @param viewer the player for whom the tag will be hidden
     */
    void hide(CNPlayer viewer);

    /**
     * Shows the tag to all viewers.
     */
    void show();

    /**
     * Shows the tag to a specific viewer.
     *
     * @param viewer the player for whom the tag will be shown
     */
    void show(CNPlayer viewer);

    /**
     * Respawns the tag for all viewers.
     */
    void respawn();

    /**
     * Respawns the tag for a specific viewer.
     *
     * @param viewer the player for whom the tag will be respawned
     */
    void respawn(CNPlayer viewer);

    /**
     * Returns the opacity of the tag.
     *
     * @return the opacity value as a byte
     */
    byte opacity();

    /**
     * Checks if the tag can be shown to all viewers.
     *
     * @return true if the tag can be shown, false otherwise
     */
    boolean canShow();

    /**
     * Checks if the tag can be shown to a specific viewer.
     *
     * @param viewer the player to check
     * @return true if the tag can be shown, false otherwise
     */
    boolean canShow(CNPlayer viewer);

    /**
     * Checks if the tag is currently shown to all viewers.
     *
     * @return true if the tag is shown, false otherwise
     */
    boolean isShown();

    /**
     * Checks if the tag is currently shown to a specific viewer.
     *
     * @param viewer the player to check
     * @return true if the tag is shown, false otherwise
     */
    boolean isShown(CNPlayer viewer);

    /**
     * Called every tick to update the tag's state.
     */
    void tick();

    /**
     * Initializes the tag.
     */
    void init();

    /**
     * Returns the text height of the tag for a specific viewer.
     *
     * @param viewer the player to check
     * @return the text height
     */
    double getTextHeight(CNPlayer viewer);

    /**
     * Sets the dark mode for the tag.
     *
     * @param dark whether the tag should be dark
     */
    void darkTag(boolean dark);

    /**
     * Sets the dark mode for the tag for a specific viewer.
     *
     * @param viewer the player for whom the dark mode should be applied
     * @param dark whether the tag should be dark for the specific viewer
     */
    void darkTag(CNPlayer viewer, boolean dark);

    /**
     * Updates the tag scale when the player's scale changes.
     *
     * @param scale the new scale value
     */
    void onPlayerScaleUpdate(double scale);

    /**
     * Updates the tag scale when the player's scale changes for a specific viewer.
     *
     * @param viewer the player to update
     * @param scale the new scale value
     */
    void onPlayerScaleUpdate(CNPlayer viewer, double scale);

    /**
     * Updates the translation of the tag.
     */
    void updateTranslation();

    /**
     * Updates the translation of the tag for a certain player
     *
     * @param viewer the player to update
     */
    void updateTranslation(CNPlayer viewer);

    /**
     * Returns the scaling vector of the tag for a specific viewer.
     *
     * @param viewer the player to check
     * @return the scaling vector
     */
    Vector3 scale(CNPlayer viewer);

    /**
     * Returns the translation vector of the tag for a specific viewer.
     *
     * @param viewer the player to check
     * @return the translation vector
     */
    Vector3 translation(CNPlayer viewer);

    /**
     * Returns if the tag has a relative translation
     *
     * @return has or not
     */
    boolean relativeTranslation();
}
