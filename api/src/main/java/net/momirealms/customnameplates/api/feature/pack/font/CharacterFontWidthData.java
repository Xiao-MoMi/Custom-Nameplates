package net.momirealms.customnameplates.api.feature.pack.font;

import java.util.Map;

public interface CharacterFontWidthData {

    int size();

    Integer getWidth(int codePoint);

    Map<Integer, Integer> data();

    String id();

    static Builder builder() {
        return new CharacterFontWidthDataImpl.BuilderImpl();
    }

    interface Builder {

        Builder width(Map<Integer, Integer> data);

        Builder id(String id);

        CharacterFontWidthData build();
    }
}
