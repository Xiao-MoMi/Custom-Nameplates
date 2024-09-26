package net.momirealms.customnameplates.api.feature.advance;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class CharacterFontAdvanceDataImpl implements CharacterFontAdvanceData {

    private final HashMap<Integer, Float> data;
    private final String id;

    public CharacterFontAdvanceDataImpl(String id, HashMap<Integer, Float> data) {
        this.data = data;
        this.id = requireNonNull(id);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Float getAdvance(int codePoint) {
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
        public Builder advance(Map<Integer, Float> data) {
            this.data.putAll(data);
            return this;
        }

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public CharacterFontAdvanceData build() {
            return new CharacterFontAdvanceDataImpl(id, data);
        }
    }
}
