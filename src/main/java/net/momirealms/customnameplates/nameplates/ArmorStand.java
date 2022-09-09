package net.momirealms.customnameplates.nameplates;

import net.momirealms.customnameplates.objects.TextCache;
import org.bukkit.entity.Player;


public interface ArmorStand {

    void setOffset(double var1);

    double getOffset();

    TextCache getText();

    void teleport();

    void teleport(Player player);

    void setSneak(boolean isSneaking, boolean respawn);

    void destroy();

    void destroy(Player player);

    void refresh();

    int getEntityId();

    void spawn(Player player);

    void respawn(Player player);
}
