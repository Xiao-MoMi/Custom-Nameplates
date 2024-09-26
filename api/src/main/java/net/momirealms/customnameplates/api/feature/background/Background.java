package net.momirealms.customnameplates.api.feature.background;

import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;

public interface Background {

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

    String createBackgroundWithOffsets(float advance, float leftMargin, float rightMargin);

    String createBackground(float advance, float leftMargin, float rightMargin);

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
