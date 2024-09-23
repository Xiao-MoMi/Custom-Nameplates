package net.momirealms.customnameplates.api.feature.bossbar;

import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;

import static java.util.Objects.requireNonNull;

public class BossBarConfigImpl implements BossBarConfig {

    private final String id;
    private final Requirement[] requirements;
    private final CarouselText[] carouselTexts;
    private final BossBar.Overlay overlay;
    private final BossBar.Color color;
    private final float progress;

    public BossBarConfigImpl(String id, Requirement[] requirements, CarouselText[] carouselTexts, BossBar.Overlay overlay, BossBar.Color color, float progress) {
        this.id = requireNonNull(id);
        this.requirements = requireNonNull(requirements);
        this.carouselTexts = requireNonNull(carouselTexts);
        this.overlay = requireNonNull(overlay);
        this.color = requireNonNull(color);
        this.progress = progress;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Requirement[] requirements() {
        return requirements;
    }

    @Override
    public CarouselText[] carouselTexts() {
        return carouselTexts;
    }

    @Override
    public BossBar.Overlay overlay() {
        return overlay;
    }

    @Override
    public BossBar.Color color() {
        return color;
    }

    @Override
    public float progress() {
        return progress;
    }

    public static class BuilderImpl implements Builder {

        private String id;
        private Requirement[] requirements;
        private CarouselText[] carouselTexts;
        private BossBar.Overlay overlay = DEFAULT_OVERLAY;
        private BossBar.Color color = DEFAULT_COLOR;
        private float progress = 0f;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder requirement(Requirement[] requirements) {
            this.requirements = requirements;
            return this;
        }

        @Override
        public Builder carouselText(CarouselText[] carouselTexts) {
            this.carouselTexts = carouselTexts;
            return this;
        }

        @Override
        public Builder overlay(BossBar.Overlay overlay) {
            this.overlay = overlay;
            return this;
        }

        @Override
        public Builder color(BossBar.Color color) {
            this.color = color;
            return this;
        }

        @Override
        public Builder progress(float progress) {
            this.progress = progress;
            return this;
        }

        @Override
        public BossBarConfig build() {
            return new BossBarConfigImpl(id, requirements, carouselTexts, overlay, color, progress);
        }
    }
}
