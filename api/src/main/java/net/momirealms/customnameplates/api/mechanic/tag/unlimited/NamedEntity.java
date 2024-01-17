package net.momirealms.customnameplates.api.mechanic.tag.unlimited;

import net.momirealms.customnameplates.api.mechanic.misc.ViewerText;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.UUID;

public interface NamedEntity {

    boolean canSee(Player viewer);

    void timer();

    boolean canShow();

    boolean isShownTo(Player viewer);

    void spawn(Player viewer, Pose pose);

    void spawn(Pose pose);

    void destroy();

    void destroy(Player viewer);

    void teleport(double x, double y, double z, boolean onGround);

    void teleport(Player viewer, double x, double y, double z, boolean onGround);

    void setSneak(boolean sneaking, boolean respawn);

    void removePlayerFromViewers(Player player);

    void addPlayerToViewers(Player player);

    double getOffset();

    void setOffset(double v);

    ViewerText getViewerText();

    int getEntityId();

    void move(short x, short y, short z, boolean onGround);

    void move(Player viewer, short x, short y, short z, boolean onGround);

    void respawn(Player viewer, Pose pose);

    void respawn(Pose pose);

    void updateText();

    void updateText(Player viewer);

    UUID getUuid();

    void handlePose(Pose previous, Pose pose);
}
