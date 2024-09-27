package net.momirealms.customnameplates.api.feature.advance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class ConfigurableFontAdvanceDataImpl implements ConfigurableFontAdvanceData {

    private final float defaultAdvance;
    private final HashMap<Integer, Float> data = new HashMap<>();
    private final List<CharacterFontAdvanceData> parents = new ArrayList<>();
    private final String id;

    public ConfigurableFontAdvanceDataImpl(String id, float defaultAdvance, HashMap<Integer, Float> customData, List<CharacterFontAdvanceData> parentFonts) {
        this.id = requireNonNull(id);
        this.defaultAdvance = defaultAdvance;
        Collections.reverse(parentFonts);
        for (CharacterFontAdvanceData parent : parentFonts) {
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
    public float defaultAdvance() {
        return defaultAdvance;
    }

    @Override
    public float getAdvance(int codePoint) {
        Float width = data.get(codePoint);
        if (width == null) {
            for (CharacterFontAdvanceData parent : parents) {
                width = parent.getAdvance(codePoint);
                if (width != null) {
                    return width;
                }
            }
            return defaultAdvance;
        }
        return width;
    }

    @Override
    public String id() {
        return id;
    }

    public static class BuilderImpl implements Builder {

        private final HashMap<Integer, Float> customData = new HashMap<>();
        private final List<CharacterFontAdvanceData> parents = new ArrayList<>();
        private float defaultAdvance = 0;
        private String id;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder defaultAdvance(float width) {
            this.defaultAdvance = width;
            return this;
        }

        @Override
        public Builder advance(int codePoint, float width) {
            this.customData.put(codePoint, width);
            return this;
        }

        @Override
        public Builder parentFont(List<CharacterFontAdvanceData> font) {
            this.parents.addAll(font);
            return this;
        }

        @Override
        public ConfigurableFontAdvanceData build() {
            return new ConfigurableFontAdvanceDataImpl(id, defaultAdvance, customData, parents);
        }
    }
}
