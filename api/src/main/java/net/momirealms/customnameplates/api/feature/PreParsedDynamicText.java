package net.momirealms.customnameplates.api.feature;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.api.placeholder.PlaceholderManager;
import net.momirealms.customnameplates.api.placeholder.RelationalPlaceholder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PreParsedDynamicText {

    private final String text;
    private final List<Function<CNPlayer<?>, Function<CNPlayer<?>, String>>> textFunctions = new ArrayList<>();
    private final List<Placeholder> placeholders = new ArrayList<>();

    public PreParsedDynamicText(String text) {
        this.text = text;
        PlaceholderManager manager = CustomNameplates.getInstance().getPlaceholderManager();
        List<Function<CNPlayer<?>, Function<CNPlayer<?>, String>>> convertor = new ArrayList<>();
        for (String id : manager.detectPlaceholders(text)) {
            placeholders.add(manager.getPlaceholder(id));
        }
        for (Placeholder placeholder : placeholders) {
            if (placeholder instanceof RelationalPlaceholder) {
                convertor.add((owner) -> (viewer) -> owner.getRelationalValue(placeholder.id(), viewer));
            } else {
                convertor.add((owner) -> (viewer) -> owner.getValue(placeholder.id()));
            }
        }
        int placeholderSize = placeholders.size();
        String original0 = text;
        for (int i = 0; i < placeholderSize; i++) {
            String id = placeholders.get(i).id();
            int index = original0.indexOf(id);
            if (index == -1) {
                throw new RuntimeException();
            } else {
                if (index != 0) {
                    String textBefore = original0.substring(0, index);
                    textFunctions.add((owner) -> (viewer) -> textBefore);
                }
                textFunctions.add(convertor.get(i));
                original0 = original0.substring(index + id.length());
            }
        }
        if (!original0.isEmpty()) {
            String remaining = original0;
            textFunctions.add((owner) -> (viewer) -> remaining);
        }
    }

    public DynamicText fastCreate(CNPlayer<?> player) {
        List<Function<CNPlayer<?>, String>> functions = new ArrayList<>();
        for (Function<CNPlayer<?>, Function<CNPlayer<?>, String>> textFunction : textFunctions) {
            functions.add(textFunction.apply(player));
        }
        return new DynamicText(
                player,
                text,
                functions,
                placeholders
        );
    }

    public List<Placeholder> placeholders() {
        return placeholders;
    }
}
