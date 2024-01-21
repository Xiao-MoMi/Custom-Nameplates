package net.momirealms.customnameplates.api.mechanic.tag;

import org.bukkit.entity.Player;

public interface NameplatePlayer {

    void setPreview(boolean preview);

    boolean isPreviewing();

    Player getPlayer();

    void updateText();
}
