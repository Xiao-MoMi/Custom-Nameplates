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

package net.momirealms.customnameplates.api.feature;

/**
 * Utility class for automatically arranging characters.
 * Provides methods to increase, retrieve, and reset the current character.
 */
public class CharacterArranger {

    /**
     * The current character being managed by the arranger.
     */
    public static char currentChar;

    /**
     * Increases the current character by 1.
     */
    public static void increase() {
        currentChar = (char) (currentChar + '\u0001');
    }

    /**
     * Retrieves the current character and then increases it.
     *
     * @return the current character before increase
     */
    public static char getAndIncrease() {
        char temp = currentChar;
        increase();
        return temp;
    }

    /**
     * Increases the current character and then retrieves it.
     *
     * @return the current character after increase
     */
    public static char increaseAndGet() {
        increase();
        return currentChar;
    }

    /**
     * Resets the current character to the specified value.
     *
     * @param c the character to reset to
     */
    public static void reset(char c) {
        currentChar = c;
    }
}