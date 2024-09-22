package net.momirealms.customnameplates.api.feature;

import net.momirealms.customnameplates.api.requirement.Requirement;

public class CarouselText {

    private final int duration;
    private final Requirement[] requirements;
    private final String rawText;
    private final PreParsedDynamicText preParsedDynamicText;

    public CarouselText(int duration, Requirement[] requirements, String rawText) {
        this.duration = duration;
        this.requirements = requirements;
        this.rawText = rawText;
        this.preParsedDynamicText = new PreParsedDynamicText(rawText);
    }

    public int duration() {
        return duration;
    }

    public Requirement[] requirements() {
        return requirements;
    }

    public String text() {
        return rawText;
    }

    public PreParsedDynamicText preParsedDynamicText() {
        return preParsedDynamicText;
    }
}
