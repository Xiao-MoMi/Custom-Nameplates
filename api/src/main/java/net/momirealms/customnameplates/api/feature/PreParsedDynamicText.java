package net.momirealms.customnameplates.api.feature;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.api.placeholder.PlaceholderManager;
import net.momirealms.customnameplates.api.placeholder.RelationalPlaceholder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class PreParsedDynamicText {

    private final String text;
    private final List<Function<CNPlayer, Function<CNPlayer, String>>> textFunctions = new ArrayList<>();
    private final Set<Placeholder> set;

    public PreParsedDynamicText(String text) {
        this.text = text;
        PlaceholderManager manager = CustomNameplates.getInstance().getPlaceholderManager();
        List<String> detectedPlaceholders = manager.detectPlaceholders(text);
        List<Function<CNPlayer, Function<CNPlayer, String>>> convertor = new ArrayList<>(detectedPlaceholders.size());
        List<Placeholder> placeholders = new ArrayList<>(detectedPlaceholders.size());
        for (String id : detectedPlaceholders) {
            Placeholder placeholder = manager.getPlaceholder(id);
            placeholders.add(placeholder);
            if (placeholder instanceof RelationalPlaceholder) {
                convertor.add((owner) -> (viewer) -> owner.getRelationalValue(placeholder.id(), viewer));
            } else {
                convertor.add((owner) -> (viewer) -> owner.getValue(placeholder.id()));
            }
        }
        int placeholderSize = placeholders.size();
        StringBuilder original0 = new StringBuilder(text);
        int lastIndex = 0;
        // 优化字符串处理，减少 substring 操作
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
        // To optimize the tree height, call new HashSet twice here
        set = new HashSet<>(new HashSet<>(placeholders));
    }

    public DynamicText fastCreate(CNPlayer player) {
        List<Function<CNPlayer, String>> functions = new ArrayList<>();
        for (Function<CNPlayer, Function<CNPlayer, String>> textFunction : textFunctions) {
            functions.add(textFunction.apply(player));
        }
        return new DynamicText(
                text,
                functions,
                set
        );
    }

    public Set<Placeholder> placeholders() {
        return set;
    }
}
