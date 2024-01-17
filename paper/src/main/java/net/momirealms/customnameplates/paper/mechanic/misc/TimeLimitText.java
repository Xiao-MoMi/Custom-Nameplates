package net.momirealms.customnameplates.paper.mechanic.misc;

import net.momirealms.customnameplates.api.requirement.Requirement;

public class TimeLimitText {

    private int duration;
    private int refreshFrequency;
    private String text;
    private Requirement[] requirements;

    private TimeLimitText() {
        this.duration = 100;
        this.refreshFrequency = -1;
        this.text = "";
        this.requirements = new Requirement[0];
    }

    public TimeLimitText(int duration, int refreshFrequency, String text, Requirement[] requirements) {
        this.duration = duration;
        this.text = text;
        this.refreshFrequency = refreshFrequency;
        this.requirements = requirements;
    }

    public int getDuration() {
        return duration;
    }

    public String getText() {
        return text;
    }

    public Requirement[] getRequirements() {
        return requirements;
    }

    public int getRefreshFrequency() {
        return refreshFrequency;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final TimeLimitText text;

        public Builder() {
            this.text = new TimeLimitText();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder duration(int duration) {
            text.duration = duration;
            return this;
        }

        public Builder refreshFrequency(int refreshFrequency) {
            text.refreshFrequency = refreshFrequency;
            return this;
        }

        public Builder text(String content) {
            text.text = content;
            return this;
        }

        public Builder requirement(Requirement[] requirements) {
            text.requirements = requirements;
            return this;
        }

        public TimeLimitText build() {
            return text;
        }
    }
}
