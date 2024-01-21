package net.momirealms.customnameplates.api.mechanic.tag.team;

import net.momirealms.customnameplates.api.mechanic.tag.NameplatePlayer;

public interface TeamTagPlayer extends NameplatePlayer {
    void setPrefix(String prefix);

    void setSuffix(String suffix);

    void destroy();
}
