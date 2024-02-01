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

package net.momirealms.customnameplates.api.mechanic.character;

public class CharacterArranger {

    public static char currentChar;

    public static void increase() {
        currentChar = (char) (currentChar + '\u0001');
    }

    /**
     * Get and increase the char arranged automatically by the plugin
     *
     * @return char
     */
    public static char getAndIncrease() {
        char temp = currentChar;
        increase();
        return temp;
    }

    /**
     * Increase and get the char arranged automatically by the plugin
     *
     * @return char
     */
    public static char increaseAndGet() {
        increase();
        return currentChar;
    }

    /**
     * Reset the initial char
     *
     * @param c char
     */
    public static void reset(char c) {
        currentChar = c;
    }
}
