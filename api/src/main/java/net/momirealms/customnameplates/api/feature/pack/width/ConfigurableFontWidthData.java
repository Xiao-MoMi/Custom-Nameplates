package net.momirealms.customnameplates.api.feature.pack.width;

import java.util.List;

public interface ConfigurableFontWidthData {

    float defaultWidth();

    float getWidth(int codePoint);

    String id();

    static Builder builder() {
        return new ConfigurableFontWidthDataImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder defaultWidth(float width);

        Builder width(int codePoint, float width);

        Builder parentFont(List<CharacterFontWidthData> font);

        ConfigurableFontWidthData build();
    }
}
