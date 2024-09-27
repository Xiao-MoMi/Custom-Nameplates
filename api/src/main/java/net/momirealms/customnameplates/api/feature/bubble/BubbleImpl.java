package net.momirealms.customnameplates.api.feature.bubble;

import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;

public class BubbleImpl implements Bubble {

    private final String id;
    private final Formatter formatter;
    private final String displayName;
    private final ConfiguredCharacter left;
    private final ConfiguredCharacter right;
    private final ConfiguredCharacter middle;
    private final ConfiguredCharacter tail;

    public BubbleImpl(String id, Formatter formatter, String displayName, ConfiguredCharacter left, ConfiguredCharacter right, ConfiguredCharacter middle, ConfiguredCharacter tail) {
        this.id = id;
        this.formatter = formatter;
        this.displayName = displayName;
        this.left = left;
        this.right = right;
        this.middle = middle;
        this.tail = tail;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Formatter format() {
        return formatter;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public ConfiguredCharacter left() {
        return left;
    }

    @Override
    public ConfiguredCharacter right() {
        return right;
    }

    @Override
    public ConfiguredCharacter middle() {
        return middle;
    }

    @Override
    public ConfiguredCharacter tail() {
        return tail;
    }

    public static class BuilderImpl implements Builder {

        private String id;
        private Formatter formatter;
        private String displayName;
        private ConfiguredCharacter left;
        private ConfiguredCharacter right;
        private ConfiguredCharacter middle;
        private ConfiguredCharacter tail;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder format(Formatter formatter) {
            this.formatter = formatter;
            return this;
        }

        @Override
        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        @Override
        public Builder left(ConfiguredCharacter left) {
            this.left = left;
            return this;
        }

        @Override
        public Builder right(ConfiguredCharacter right) {
            this.right = right;
            return this;
        }

        @Override
        public Builder middle(ConfiguredCharacter middle) {
            this.middle = middle;
            return this;
        }

        @Override
        public Builder tail(ConfiguredCharacter tail) {
            this.tail = tail;
            return this;
        }

        @Override
        public Bubble build() {
            return new BubbleImpl(id, formatter, displayName, left, right, middle, tail);
        }
    }
}
