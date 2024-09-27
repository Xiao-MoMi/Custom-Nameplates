package net.momirealms.customnameplates.api.feature.bubble;

import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;

import java.util.function.Function;

public interface Bubble {

    String id();

    Formatter format();

    String displayName();

    ConfiguredCharacter left();

    ConfiguredCharacter right();

    ConfiguredCharacter middle();

    ConfiguredCharacter tail();

    static Builder builder() {
        return new BubbleImpl.BuilderImpl();
    }

    interface Formatter {

        Function<String, String> formatFunction();

        String format(String text);
    }

    interface Builder {

        Builder id(String id);

        Builder format(Formatter formatter);

        Builder displayName(String displayName);

        Builder left(ConfiguredCharacter left);

        Builder right(ConfiguredCharacter right);

        Builder middle(ConfiguredCharacter middle);

        Builder tail(ConfiguredCharacter tail);

        Bubble build();
    }
}
