package net.momirealms.customnameplates.api.feature.nameplate;

import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;

public class NameplateImpl implements Nameplate {

    private final String id;
    private final String displayName;
    private final ConfiguredCharacter left;
    private final ConfiguredCharacter middle;
    private final ConfiguredCharacter right;

    public NameplateImpl(String id, String displayName, ConfiguredCharacter left, ConfiguredCharacter middle, ConfiguredCharacter right) {
        this.id = id;
        this.displayName = displayName;
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    @Override
    public String id() {
        return id;
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
    public ConfiguredCharacter middle() {
        return middle;
    }

    @Override
    public ConfiguredCharacter right() {
        return right;
    }

    public static class BuilderImpl implements Builder {

        private String id;
        private String displayName;
        private ConfiguredCharacter left;
        private ConfiguredCharacter middle;
        private ConfiguredCharacter right;

        @Override
        public Builder id(String id) {
            this.id = id;
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
        public Builder middle(ConfiguredCharacter middle) {
            this.middle = middle;
            return this;
        }

        @Override
        public Builder right(ConfiguredCharacter right) {
            this.right = right;
            return this;
        }

        @Override
        public Nameplate build() {
            return new NameplateImpl(id, displayName, left, middle, right);
        }
    }
}
