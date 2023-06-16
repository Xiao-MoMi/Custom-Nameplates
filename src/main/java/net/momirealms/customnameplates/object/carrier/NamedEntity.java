package net.momirealms.customnameplates.object.carrier;

import net.momirealms.customnameplates.object.DynamicText;
import org.bukkit.entity.Player;

public interface NamedEntity {

    void refresh();

    boolean canShow();

    boolean isShown();

    void destroy();

    void destroy(Player viewer);

    void spawn();

    void spawn(Player viewer);

    void teleport();

    void teleport(Player viewer);

    void setSneak(boolean sneaking, boolean respawn);

    void respawn(Player viewer);

    double getOffset();

    void setOffset(double v);

    DynamicText getDynamicText();

    int getEntityId();
}
