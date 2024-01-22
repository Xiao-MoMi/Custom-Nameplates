package net.momirealms.customnameplates.api.mechanic.tag.unlimited;

import net.momirealms.customnameplates.api.mechanic.tag.NameplatePlayer;

import java.util.Vector;

public interface EntityTagPlayer extends NameplatePlayer {

    void addTag(DynamicTextEntity tag);

    void addTag(StaticTextEntity tag);

    DynamicTextEntity addTag(DynamicTextTagSetting setting);

    StaticTextEntity addTag(StaticTextTagSetting setting);

    void removeTag(DynamicTextEntity tag);

    void removeTag(StaticTextEntity tag);

    Vector<DynamicTextEntity> getDynamicTags();

    Vector<StaticTextEntity> getStaticTags();

    void setHatOffset(double hatOffset);

    void setManageTeams(boolean manageTeams);

    double getHatOffset();

    void destroy();
}
