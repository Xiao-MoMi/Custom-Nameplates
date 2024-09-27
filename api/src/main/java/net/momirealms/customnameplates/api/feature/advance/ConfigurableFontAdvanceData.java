package net.momirealms.customnameplates.api.feature.advance;

import java.util.List;

public interface ConfigurableFontAdvanceData {

    float defaultAdvance();

    float getAdvance(int codePoint);

    String id();

    static Builder builder() {
        return new ConfigurableFontAdvanceDataImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder defaultAdvance(float width);

        Builder advance(int codePoint, float width);

        Builder parentFont(List<CharacterFontAdvanceData> font);

        ConfigurableFontAdvanceData build();
    }
}
