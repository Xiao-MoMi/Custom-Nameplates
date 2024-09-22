package net.momirealms.customnameplates.api.feature.actionbar;

import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;

public interface ActionBarConfig {

    String id();

    Requirement[] requirements();

    CarouselText[] carouselTexts();

    static Builder builder() {
        return new ActionBarConfigImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder requirement(Requirement[] requirements);

        Builder carouselText(CarouselText[] carouselTexts);

        ActionBarConfig build();
    }
}
