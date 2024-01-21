package net.momirealms.customnameplates.api.mechanic.tag.unlimited;

public class StaticTextTagSetting {

    private double verticalOffset;

    private NearbyRule comeRule;
    private NearbyRule leaveRule;

    public StaticTextTagSetting(double verticalOffset, NearbyRule comeRule, NearbyRule leaveRule) {
        this.verticalOffset = verticalOffset;
        this.comeRule = comeRule;
        this.leaveRule = leaveRule;
    }

    private StaticTextTagSetting() {
        verticalOffset = 0;
        comeRule = (player, entity) -> true;
        leaveRule = (player, entity) -> true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final StaticTextTagSetting setting;

        public static Builder of() {
            return new Builder();
        }

        public Builder() {
            this.setting = new StaticTextTagSetting();
        }

        public Builder verticalOffset(double verticalOffset) {
            this.setting.verticalOffset = verticalOffset;
            return this;
        }

        public Builder comeRule(NearbyRule rule) {
            this.setting.comeRule = rule;
            return this;
        }

        public Builder leaveRule(NearbyRule rule) {
            this.setting.leaveRule = rule;
            return this;
        }

        public StaticTextTagSetting build() {
            return setting;
        }
    }

    public double getVerticalOffset() {
        return verticalOffset;
    }

    public NearbyRule getComeRule() {
        return comeRule;
    }

    public NearbyRule getLeaveRule() {
        return leaveRule;
    }
}
