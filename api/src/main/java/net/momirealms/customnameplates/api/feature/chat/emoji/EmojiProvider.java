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

package net.momirealms.customnameplates.api.feature.chat.emoji;

import net.momirealms.customnameplates.api.CNPlayer;

/**
 * Interface for providing emoji replacement functionality.
 * Allows replacing certain text patterns with corresponding emojis for a given player.
 */
public interface EmojiProvider {

    /**
     * Replaces specified text patterns in the given string with emojis, based on the player context.
     *
     * @param player the player for whom the emoji replacement is being processed
     * @param text   the original text to process
     * @return the text with emojis replacing certain patterns
     */
    String replace(CNPlayer player, String text);
}