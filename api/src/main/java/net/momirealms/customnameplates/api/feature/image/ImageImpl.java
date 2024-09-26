package net.momirealms.customnameplates.api.feature.image;

import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;

public class ImageImpl implements Image {

    private final String id;
    private final boolean hasShadow;
    private final int opacity;
    private final ConfiguredCharacter character;

    public ImageImpl(String id, boolean hasShadow, int opacity, ConfiguredCharacter character) {
        this.id = id;
        this.hasShadow = hasShadow;
        this.opacity = opacity;
        this.character = character;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean hasShadow() {
        return hasShadow;
    }

    @Override
    public int opacity() {
        return opacity;
    }

    @Override
    public ConfiguredCharacter character() {
        return character;
    }

    public static class BuilderImpl implements Builder {

        private String id;
        private boolean hasShadow;
        private int opacity;
        private ConfiguredCharacter character;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder hasShadow(boolean has) {
            this.hasShadow = has;
            return this;
        }

        @Override
        public Builder opacity(int opacity) {
            this.opacity = opacity;
            return this;
        }

        @Override
        public Builder character(ConfiguredCharacter character) {
            this.character = character;
            return this;
        }

        @Override
        public Image build() {
            return new ImageImpl(id, hasShadow, opacity, character);
        }
    }
}
