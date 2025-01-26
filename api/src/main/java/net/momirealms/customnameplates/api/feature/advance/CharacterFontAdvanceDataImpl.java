/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.api.feature.advance;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of the CharacterFontAdvanceData interface.
 */
public class CharacterFontAdvanceDataImpl implements CharacterFontAdvanceData {
    private final Map<Integer, Float> data;
    private final String id;
    private final Function<Map<String, Object>, List<JsonObject>> fontProviderFunction;

    /**
     * Constructs a new instance of CharacterFontAdvanceDataImpl.
     *
     * @param id                    the unique identifier for this font advance data
     * @param data                  a map of character code points and their corresponding advance values
     * @param fontProviderFunction  a function that generates font provider configurations
     */
    private CharacterFontAdvanceDataImpl(String id, Map<Integer, Float> data, Function<Map<String, Object>, List<JsonObject>> fontProviderFunction) {
        this.data = data;
        this.id = requireNonNull(id);
        this.fontProviderFunction = requireNonNull(fontProviderFunction);
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
    public List<JsonObject> fontProvider(Map<String, Object> properties) {
        return fontProviderFunction.apply(properties);
    }

    /**
     * The builder implementation
     */
    public static class BuilderImpl implements Builder {
        private final Int2FloatOpenHashMap data = new Int2FloatOpenHashMap();
        private String id;
        private Function<Map<String, Object>, List<JsonObject>> fontProviderFunction = (stringObjectMap -> null);

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
        public Builder fontProviderFunction(Function<Map<String, Object>, List<JsonObject>> function) {
            this.fontProviderFunction = function;
            return this;
        }

        @Override
        public CharacterFontAdvanceData build() {
            return new CharacterFontAdvanceDataImpl(id, data, fontProviderFunction);
        }
    }
}
