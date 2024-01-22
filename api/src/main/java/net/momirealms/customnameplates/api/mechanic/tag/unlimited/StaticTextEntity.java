package net.momirealms.customnameplates.api.mechanic.tag.unlimited;

import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.UUID;

public interface StaticTextEntity {

    String getPlugin();

    boolean isShownTo(Player viewer);

    NearbyRule getComeRule();

    NearbyRule getLeaveRule();

    void removePlayerFromViewers(Player player);

    void addPlayerToViewers(Player player);

    double getOffset();

    void setOffset(double v);

    void destroy(Player viewer);

    void teleport(double x, double y, double z, boolean onGround);

    void teleport(Player viewer, double x, double y, double z, boolean onGround);

    void teleport();

    int getEntityId();

    void setText(String text);

    String getText(Player viewer);

    void setText(Player viewer, String text);

    void removeText(Player viewer);

    void spawn(Player viewer, Pose pose);

    void destroy();

    void move(short x, short y, short z, boolean onGround);

    void move(Player viewer, short x, short y, short z, boolean onGround);

    void respawn(Player viewer, Pose pose);

    void respawn(Pose pose);

    UUID getUUID();

    void handlePose(Pose previous, Pose pose);
}
