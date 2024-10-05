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

package net.momirealms.customnameplates.api.requirement;

import net.momirealms.customnameplates.api.CNPlayer;

public class EmptyRequirement implements Requirement {

    public static final EmptyRequirement INSTANCE = new EmptyRequirement();

    public static Requirement instance() {
        return INSTANCE;
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        return true;
    }

    @Override
    public String type() {
        return "empty";
    }

    @Override
    public int refreshInterval() {
        return -1;
    }

    @Override
    public int hashCode() {
        return type().hashCode();
    }
}
