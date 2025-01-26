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

package net.momirealms.customnameplates.api.feature;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.placeholder.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Represents a dynamically parsed text that can contain placeholders.
 * The text is parsed at initialization and placeholders are replaced with corresponding values.
 * This class provides functionality to manage placeholders and generate dynamic text for players.
 */
public class PreParsedDynamicText {
    private final String text;
    private final List<Function<CNPlayer, Function<CNPlayer, String>>> textFunctions = new ObjectArrayList<>();
    private final Set<Placeholder> set = new ObjectOpenHashSet<>();
    private boolean init = false;

    /**
     * Constructs a PreParsedDynamicText with the given text.
     *
     * @param text the original text containing placeholders to be parsed
     * @throws NullPointerException if the provided text is null
     */
    public PreParsedDynamicText(String text) {
        this.text = Objects.requireNonNull(text);
    }

    /**
     * Constructs a PreParsedDynamicText with the given text and initializes it (parses placeholders).
     *
     * @param text the original text containing placeholders to be parsed
     * @param init flag indicating whether to initialize the text immediately
     * @throws NullPointerException if the provided text is null
     */
    public PreParsedDynamicText(String text, boolean init) {
        this.text = Objects.requireNonNull(text);
        if (init) init();
    }

    /**
     * Initializes the PreParsedDynamicText by parsing the text for placeholders and creating
     * corresponding functions for replacing the placeholders with values.
     * This method is called automatically if the constructor is provided with `true` for the init flag.
     */
    public void init() {
        if (init) return;
        init = true;
        PlaceholderManager manager = CustomNameplates.getInstance().getPlaceholderManager();
        List<String> detectedPlaceholders = manager.detectPlaceholders(text);
        List<Function<CNPlayer, Function<CNPlayer, String>>> convertor = new ArrayList<>(detectedPlaceholders.size());
        List<Placeholder> placeholders = new ArrayList<>(detectedPlaceholders.size());
        for (String id : detectedPlaceholders) {
            Placeholder placeholder = manager.getPlaceholder(id);
            placeholders.add(placeholder);
            if (placeholder instanceof RelationalPlaceholder relationalPlaceholder) {
                convertor.add((owner) -> (viewer) -> owner.getCachedRelationalValue(relationalPlaceholder, viewer));
            } else if (placeholder instanceof PlayerPlaceholder playerPlaceholder) {
                convertor.add((owner) -> (viewer) -> {
                    if (owner != null) {
                        return owner.getCachedPlayerValue(playerPlaceholder);
                    } else {
                        return playerPlaceholder.request(null);
                    }
                });
            } else if (placeholder instanceof SharedPlaceholder sharedPlaceholder) {
                convertor.add((owner) -> (viewer) -> {
                    if (owner != null) {
                        return owner.getCachedSharedValue(sharedPlaceholder);
                    } else {
                        return sharedPlaceholder.request();
                    }
                });
            } else {
                convertor.add((owner) -> (viewer) -> id);
            }
        }
        int placeholderSize = placeholders.size();
        StringBuilder original0 = new StringBuilder(text);
        int lastIndex = 0;
        for (int i = 0; i < placeholderSize; i++) {
            String id = placeholders.get(i).id();
            int index = original0.indexOf(id, lastIndex);
            if (index == -1) {
                throw new RuntimeException("Placeholder ID not found in text");
            }
            if (index != lastIndex) {
                String textBefore = original0.substring(lastIndex, index);
                textFunctions.add((owner) -> (viewer) -> textBefore);
            }
            textFunctions.add(convertor.get(i));
            lastIndex = index + id.length();
        }
        if (lastIndex < original0.length()) {
            String remaining = original0.substring(lastIndex);
            textFunctions.add((owner) -> (viewer) -> remaining);
        }
        // To optimize the tree height
        set.addAll(new ObjectArrayList<>(placeholders));
    }

    /**
     * Creates a dynamic text based on the current pre-parsed text, tailored to the given player.
     * This method uses the player's data to generate the final text.
     *
     * @param player the player for whom the dynamic text is being generated
     * @return a DynamicText object containing the final text for the player
     */
    public DynamicText fastCreate(CNPlayer player) {
        List<Function<CNPlayer, String>> functions = new ObjectArrayList<>();
        for (Function<CNPlayer, Function<CNPlayer, String>> textFunction : textFunctions) {
            functions.add(textFunction.apply(player));
        }
        return new DynamicText(text, functions, set);
    }

    /**
     * Returns a set of placeholders detected in the text.
     *
     * @return a set of placeholders used in the text
     */
    public Set<Placeholder> placeholders() {
        return set;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PreParsedDynamicText that = (PreParsedDynamicText) object;
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }
}
