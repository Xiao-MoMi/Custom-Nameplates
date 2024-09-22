package net.momirealms.customnameplates.api.feature;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.placeholder.Placeholder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class DynamicText {

    private final CNPlayer<?> owner;
    private final String text;
    private final List<Function<CNPlayer<?>, String>> texts = new ArrayList<>();
    private final Set<Placeholder> placeholders;

    public DynamicText(CNPlayer<?> owner, String text, List<Function<CNPlayer<?>, String>> texts, Set<Placeholder> placeholders) {
        this.owner = owner;
        this.text = text;
        this.texts.addAll(texts);
        this.placeholders = placeholders;
    }

    public Set<Placeholder> placeholders() {
        return placeholders;
    }

    public String render(CNPlayer<?> viewer) {
        StringBuilder builder = new StringBuilder();
        for (Function<CNPlayer<?>, String> function : texts) {
            builder.append(function.apply(viewer));
        }
        return builder.toString();
    }

    public String text() {
        return text;
    }
}
