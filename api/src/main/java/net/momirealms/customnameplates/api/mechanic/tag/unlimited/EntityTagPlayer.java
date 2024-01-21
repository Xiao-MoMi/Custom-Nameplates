package net.momirealms.customnameplates.api.mechanic.tag.unlimited;

import net.momirealms.customnameplates.api.mechanic.tag.NameplatePlayer;

import java.util.Vector;

public interface EntityTagPlayer extends NameplatePlayer {

    void addTag(DynamicTextEntity tag);

    DynamicTextEntity addTag(DynamicTextTagSetting setting);

    void removeTag(DynamicTextEntity tag);

    Vector<DynamicTextEntity> getTags();

    void setHatOffset(double hatOffset);

    double getHatOffset();

    void destroy();
}
