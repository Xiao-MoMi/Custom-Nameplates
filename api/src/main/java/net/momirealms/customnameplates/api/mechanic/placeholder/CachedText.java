package net.momirealms.customnameplates.api.mechanic.placeholder;

public class CachedText {

    private long refreshInterval;
    private String text;

    private CachedText() {
    }

    public CachedText(long refreshInterval, String text) {
        this.refreshInterval = refreshInterval;
        this.text = text;
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public String getText() {
        return text;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final CachedText text;

        public Builder() {
            this.text = new CachedText();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder refreshInterval(long time) {
            this.text.refreshInterval = time;
            return this;
        }

        public Builder text(String text) {
            this.text.text = text;
            return this;
        }

        public CachedText build() {
            return text;
        }
    }
}
