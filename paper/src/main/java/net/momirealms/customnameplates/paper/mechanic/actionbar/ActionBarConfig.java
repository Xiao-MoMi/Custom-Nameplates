package net.momirealms.customnameplates.paper.mechanic.actionbar;

import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.paper.mechanic.bossbar.BarColor;
import net.momirealms.customnameplates.paper.mechanic.bossbar.Overlay;
import net.momirealms.customnameplates.paper.mechanic.misc.TimeLimitText;

public class ActionBarConfig {

    private int checkFrequency;
    private Requirement[] requirements;
    private TimeLimitText[] textDisplayOrder;

    private ActionBarConfig() {
        checkFrequency = 1;
        requirements = new Requirement[0];
        textDisplayOrder = new TimeLimitText[0];
    }

    public ActionBarConfig(
            Overlay overlay,
            BarColor barColor,
            int checkFrequency,
            Requirement[] requirements,
            TimeLimitText[] textDisplayOrder
    ) {
        this.checkFrequency = checkFrequency;
        this.requirements = requirements;
        this.textDisplayOrder = textDisplayOrder;
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

        private final ActionBarConfig config;

        public Builder() {
            this.config = new ActionBarConfig();
        }

        public static Builder of() {
            return new Builder();
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

        public ActionBarConfig build() {
            return config;
        }
    }
}
