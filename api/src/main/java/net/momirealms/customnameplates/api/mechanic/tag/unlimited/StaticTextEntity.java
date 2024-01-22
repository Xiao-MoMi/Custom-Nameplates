/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
