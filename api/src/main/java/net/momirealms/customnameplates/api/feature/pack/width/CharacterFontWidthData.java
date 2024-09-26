package net.momirealms.customnameplates.api.feature.pack.width;

import java.util.Map;

public interface CharacterFontWidthData {

    int size();

    Float getWidth(int codePoint);

    Map<Integer, Float> data();

    String id();

    static Builder builder() {
        return new CharacterFontWidthDataImpl.BuilderImpl();
    }

    void close();

    interface Builder {

        Builder width(Map<Integer, Float> data);

        Builder id(String id);

        CharacterFontWidthData build();
    }
}
