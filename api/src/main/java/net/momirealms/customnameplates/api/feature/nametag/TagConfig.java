package net.momirealms.customnameplates.api.feature.nametag;

import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.Alignment;
import net.momirealms.customnameplates.api.util.Vector3;

public interface TagConfig {

    String id();

    Requirement[] ownerRequirements();

    Requirement[] viewerRequirements();

    CarouselText[] carouselTexts();

    byte opacity();

    int backgroundColor();

    boolean hasShadow();

    boolean isSeeThrough();

    boolean useDefaultBackgroundColor();

    Alignment alignment();

    float viewRange();

    float shadowRadius();

    float shadowStrength();

    Vector3 scale();

    Vector3 translation();

    int lineWidth();

    static Builder builder() {
        return new TagConfigImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder lineWidth(int lineWidth);

        Builder ownerRequirement(Requirement[] requirements);

        Builder viewerRequirement(Requirement[] requirements);

        Builder carouselText(CarouselText[] carouselTexts);

        Builder opacity(byte opacity);

        Builder backgroundColor(int backgroundColor);

        Builder hasShadow(boolean hasShadow);

        Builder seeThrough(boolean seeThrough);

        Builder useDefaultBackgroundColor(boolean useDefaultBackgroundColor);

        Builder alignment(Alignment alignment);

        Builder viewRange(float viewRange);

        Builder shadowRadius(float shadowRadius);

        Builder shadowStrength(float shadowStrength);

        Builder scale(Vector3 scale);

        Builder translation(Vector3 translation);

        TagConfig build();
    }
}
