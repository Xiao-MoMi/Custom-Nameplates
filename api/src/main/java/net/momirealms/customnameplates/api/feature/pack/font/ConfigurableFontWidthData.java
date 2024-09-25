package net.momirealms.customnameplates.api.feature.pack.font;

public interface ConfigurableFontWidthData {

    int defaultWidth();

    int getWidth(int codePoint);

    String id();

    static Builder builder() {
        return new ConfigurableFontWidthDataImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder defaultWidth(int width);

        Builder width(int codePoint, int width);

        Builder parentFont(CharacterFontWidthData... font);

        ConfigurableFontWidthData build();
    }
}
