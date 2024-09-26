package net.momirealms.customnameplates.api.feature.pack.width;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class CharacterFontWidthDataImpl implements CharacterFontWidthData {

    private final HashMap<Integer, Float> data;
    private final String id;

    public CharacterFontWidthDataImpl(String id, HashMap<Integer, Float> data) {
        this.data = data;
        this.id = requireNonNull(id);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Float getWidth(int codePoint) {
        return data.get(codePoint);
    }

    @Override
    public Map<Integer, Float> data() {
        return data;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void close() {
        data.clear();
    }

    public static class BuilderImpl implements Builder {

        private final HashMap<Integer, Float> data = new HashMap<>();
        private String id;

        @Override
        public Builder width(Map<Integer, Float> data) {
            this.data.putAll(data);
            return this;
        }

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public CharacterFontWidthData build() {
            return new CharacterFontWidthDataImpl(id, data);
        }
    }
}
