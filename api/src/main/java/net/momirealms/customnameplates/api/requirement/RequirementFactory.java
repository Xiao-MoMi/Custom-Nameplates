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

/**
 * Interface representing a factory for creating {@link Requirement} instances.
 */
public interface RequirementFactory {

    /**
     * Processes the provided arguments and creates a new {@link Requirement} instance.
     *
     * @param args            the arguments needed to create the requirement
     * @param refreshInterval the refresh interval for the requirement
     * @return a new {@link Requirement} instance
     */
    Requirement process(Object args, int refreshInterval);
}
