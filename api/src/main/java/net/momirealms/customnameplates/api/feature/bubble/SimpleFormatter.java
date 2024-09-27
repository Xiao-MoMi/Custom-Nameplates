package net.momirealms.customnameplates.api.feature.bubble;

import java.util.function.Function;

public class SimpleFormatter implements Bubble.Formatter {

    private final String left;
    private final String right;

    public SimpleFormatter(String left, String right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Function<String, String> formatFunction() {
        return (text) -> left + text + right;
    }

    @Override
    public String format(String text) {
        return formatFunction().apply(text);
    }
}
