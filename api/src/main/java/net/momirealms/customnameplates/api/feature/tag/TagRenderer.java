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

import java.util.function.Predicate;

/**
 * An interface to control the rendering of tags
 */
public interface TagRenderer {

    /**
     * Returns the offset applied to the tag for positioning a "hat" or similar element above the entity's head.
     *
     * @return the hat offset value
     */
    double hatOffset();

    /**
     * Sets the offset applied to the tag for positioning a "hat" or similar element above the entity's head.
     *
     * @param hatOffset the new hat offset value
     */
    void hatOffset(double hatOffset);

    /**
     * Checks if the tag is valid
     *
     * @return valid or not
     */
    boolean isValid();

    /**
     * Called every tick to update the state of the tags managed by this renderer.
     */
    void onTick();

    /**
     * Destroys the renderer, cleaning up any resources or tags it manages.
     */
    void destroy();

    /**
     * Adds a new tag to be rendered.
     *
     * @param tag the tag to add
     */
    void addTag(Tag tag);

    /**
     * Returns the array of tags currently managed by this renderer.
     *
     * @return an array of Tag instances
     */
    Tag[] tags();

    /**
     * Removes tags that match the given predicate.
     *
     * @param predicate a predicate to test tags for removal
     * @return the number of tags removed
     */
    int removeTagIf(Predicate<Tag> predicate);

    /**
     * Returns the index of the specified tag in the renderer's tag list.
     *
     * @param tag the tag to check
     * @return the index of the tag, or -1 if not found
     */
    int tagIndex(Tag tag);

    /**
     * Adds a tag at the specified index in the renderer's tag list.
     *
     * @param tag   the tag to add
     * @param index the index at which to insert the tag
     */
    void addTag(Tag tag, int index);

    /**
     * Removes the specified tag from the renderer's tag list.
     *
     * @param tag the tag to remove
     */
    void removeTag(Tag tag);
}
