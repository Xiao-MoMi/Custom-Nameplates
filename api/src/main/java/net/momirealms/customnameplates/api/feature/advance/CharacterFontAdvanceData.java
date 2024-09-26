package net.momirealms.customnameplates.api.feature.advance;

import java.util.Map;

public interface CharacterFontAdvanceData {

    int size();

    Float getAdvance(int codePoint);

    Map<Integer, Float> data();

    String id();

    static Builder builder() {
        return new CharacterFontAdvanceDataImpl.BuilderImpl();
    }

    void close();

    interface Builder {

        Builder advance(Map<Integer, Float> data);

        Builder id(String id);

        CharacterFontAdvanceData build();
    }
}
