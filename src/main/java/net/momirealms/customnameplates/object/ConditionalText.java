package net.momirealms.customnameplates.object;

import net.momirealms.customnameplates.object.requirements.Requirement;

public class ConditionalText {

    private final Requirement[] requirements;
    private final String text;

    public ConditionalText(Requirement[] requirements, String text) {
        this.requirements = requirements;
        this.text = text;
    }

    public Requirement[] getRequirements() {
        return requirements;
    }

    public String getText() {
        return text;
    }
}
