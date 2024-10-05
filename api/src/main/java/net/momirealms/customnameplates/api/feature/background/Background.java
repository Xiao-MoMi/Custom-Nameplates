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

package net.momirealms.customnameplates.api.feature.background;

import net.momirealms.customnameplates.api.feature.AdaptiveImage;
import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;

public interface Background extends AdaptiveImage {

    String id();

    ConfiguredCharacter left();

    ConfiguredCharacter width_1();

    ConfiguredCharacter width_2();

    ConfiguredCharacter width_4();

    ConfiguredCharacter width_8();

    ConfiguredCharacter width_16();

    ConfiguredCharacter width_32();

    ConfiguredCharacter width_64();

    ConfiguredCharacter width_128();

    ConfiguredCharacter right();

    static Builder builder() {
        return new BackgroundImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder left(ConfiguredCharacter character);

        Builder width_1(ConfiguredCharacter character);

        Builder width_2(ConfiguredCharacter character);

        Builder width_4(ConfiguredCharacter character);

        Builder width_8(ConfiguredCharacter character);

        Builder width_16(ConfiguredCharacter character);

        Builder width_32(ConfiguredCharacter character);

        Builder width_64(ConfiguredCharacter character);

        Builder width_128(ConfiguredCharacter character);

        Builder right(ConfiguredCharacter character);

        Background build();
    }
}
