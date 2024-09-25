package net.momirealms.customnameplates.api.feature.pack.font;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class ConfigurableFontWidthDataImpl implements ConfigurableFontWidthData {

    private final int defaultWidth;
    private final HashMap<Integer, Integer> data = new HashMap<>();
    private final List<CharacterFontWidthData> parents = new ArrayList<>();
    private final String id;

    public ConfigurableFontWidthDataImpl(String id, int defaultWidth, HashMap<Integer, Integer> customData, List<CharacterFontWidthData> parentFonts) {
        this.id = requireNonNull(id);
        this.defaultWidth = defaultWidth;
        Collections.reverse(parentFonts);
        for (CharacterFontWidthData parent : parentFonts) {
            // To optimize memory, especially for those fonts that have over 100,000 characters
            if (parent.size() > 2048) {
                parents.add(parent);
            } else {
                data.putAll(parent.data());
            }
        }
        data.putAll(customData);
        Collections.reverse(parents);
    }

    @Override
    public int defaultWidth() {
        return defaultWidth;
    }

    @Override
    public int getWidth(int codePoint) {
        Integer width = data.get(codePoint);
        if (width == null) {
            for (CharacterFontWidthData parent : parents) {
                width = parent.getWidth(codePoint);
                if (width != null) {
                    return width;
                }
            }
        }
        return defaultWidth;
    }

    @Override
    public String id() {
        return id;
    }

    public static class BuilderImpl implements Builder {

        private final HashMap<Integer, Integer> customData = new HashMap<>();
        private final List<CharacterFontWidthData> parents = new ArrayList<>();
        private int defaultWidth = 0;
        private String id;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder defaultWidth(int width) {
            this.defaultWidth = width;
            return this;
        }

        @Override
        public Builder width(int codePoint, int width) {
            this.customData.put(codePoint, width);
            return this;
        }

        @Override
        public Builder parentFont(CharacterFontWidthData... font) {
            this.parents.addAll(Arrays.asList(font));
            return this;
        }

        @Override
        public ConfigurableFontWidthData build() {
            return new ConfigurableFontWidthDataImpl(id, defaultWidth, customData, parents);
        }
    }
}
