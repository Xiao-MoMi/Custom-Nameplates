package net.momirealms.customnameplates.api.feature.image;

import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;

public interface Image {

    String id();

    boolean hasShadow();

    int opacity();

    ConfiguredCharacter character();

    static Builder builder() {
        return new ImageImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder hasShadow(boolean has);

        Builder opacity(int opacity);

        Builder character(ConfiguredCharacter character);

        Image build();
    }
}
