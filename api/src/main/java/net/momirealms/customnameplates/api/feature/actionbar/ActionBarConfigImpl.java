package net.momirealms.customnameplates.api.feature.actionbar;

import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;

import static java.util.Objects.requireNonNull;

public class ActionBarConfigImpl implements ActionBarConfig {

    private final String id;
    private final Requirement[] requirements;
    private final CarouselText[] carouselTexts;

    public ActionBarConfigImpl(String id, Requirement[] requirements, CarouselText[] carouselTexts) {
        this.id = requireNonNull(id);
        this.requirements = requireNonNull(requirements);
        this.carouselTexts = requireNonNull(carouselTexts);
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

    public static class BuilderImpl implements Builder {

        private String id;
        private Requirement[] requirements;
        private CarouselText[] carouselTexts;

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
        public ActionBarConfig build() {
            return new ActionBarConfigImpl(id, requirements, carouselTexts);
        }
    }
}
