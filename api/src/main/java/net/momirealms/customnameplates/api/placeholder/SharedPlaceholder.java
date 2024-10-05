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

/**
 * Represents a shared placeholder, which provides content that is not specific to any individual player but can be updated globally.
 */
public interface SharedPlaceholder extends Placeholder {

    /**
     * Requests the current placeholder content.
     *
     * @return the current placeholder content as a string
     */
    String request();

    /**
     * Updates the placeholder content. This is typically called when the placeholder's value needs to be refreshed.
     */
    void update();

    /**
     * Returns the latest value of the placeholder after the most recent update.
     *
     * @return the latest placeholder value as a string
     */
    String getLatestValue();
}
