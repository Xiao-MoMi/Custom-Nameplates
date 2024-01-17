package net.momirealms.customnameplates.api.mechanic.tag;

import net.momirealms.customnameplates.api.mechanic.nameplate.Nameplate;
import org.bukkit.entity.Player;

public interface NameplatePlayer {

    void preview();

    void preview(Nameplate nameplate);

    Player getOwner();
}
