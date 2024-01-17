package net.momirealms.customnameplates.paper.mechanic.bossbar;

import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.paper.mechanic.misc.TimeLimitText;

public class BossBarConfig {

    private Overlay overlay;
    private BarColor barColor;
    private int checkFrequency;
    private Requirement[] requirements;
    private TimeLimitText[] textDisplayOrder;

    private BossBarConfig() {
        overlay = Overlay.PROGRESS;
        barColor = BarColor.YELLOW;
        checkFrequency = 1;
        requirements = new Requirement[0];
        textDisplayOrder = new TimeLimitText[0];
    }

    public BossBarConfig(
            Overlay overlay,
            BarColor barColor,
            int checkFrequency,
            Requirement[] requirements,
            TimeLimitText[] textDisplayOrder
    ) {
        this.overlay = overlay;
        this.barColor = barColor;
        this.checkFrequency = checkFrequency;
        this.requirements = requirements;
        this.textDisplayOrder = textDisplayOrder;
    }

    public Overlay getOverlay() {
        return overlay;
    }

    public BarColor getBarColor() {
        return barColor;
    }

    public int getCheckFrequency() {
        return checkFrequency;
    }

    public Requirement[] getRequirements() {
        return requirements;
    }

    public TimeLimitText[] getTextDisplayOrder() {
        return textDisplayOrder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final BossBarConfig config;

        public Builder() {
            this.config = new BossBarConfig();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder overlay(Overlay overlay) {
            config.overlay = overlay;
            return this;
        }

        public Builder barColor(BarColor barColor) {
            config.barColor = barColor;
            return this;
        }

        public Builder checkFrequency(int checkFrequency) {
            config.checkFrequency = checkFrequency;
            return this;
        }

        public Builder requirement(Requirement[] requirements) {
            config.requirements = requirements;
            return this;
        }

        public Builder displayOrder(TimeLimitText[] textDisplayOrder) {
            config.textDisplayOrder = textDisplayOrder;
            return this;
        }

        public BossBarConfig build() {
            return config;
        }
    }
}
