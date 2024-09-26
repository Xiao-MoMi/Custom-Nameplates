package net.momirealms.customnameplates.api.feature.nameplate;

import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;

public interface Nameplate {

    String id();

    String displayName();

    ConfiguredCharacter left();

    ConfiguredCharacter middle();

    ConfiguredCharacter right();

    static Builder builder() {
        return new NameplateImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder displayName(String displayName);

        Builder left(ConfiguredCharacter left);

        Builder middle(ConfiguredCharacter middle);

        Builder right(ConfiguredCharacter right);

        Nameplate build();
    }
}
