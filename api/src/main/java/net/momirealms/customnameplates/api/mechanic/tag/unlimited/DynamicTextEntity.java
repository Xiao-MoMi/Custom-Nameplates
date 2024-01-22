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

import net.momirealms.customnameplates.api.mechanic.misc.ViewerText;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.UUID;

public interface DynamicTextEntity {

    boolean canSee(Player viewer);

    void timer();

    boolean canShow();

    boolean isShownTo(Player viewer);

    void spawn(Player viewer, Pose pose);

    void spawn(Pose pose);

    void destroy();

    void destroy(Player viewer);

    void teleport(double x, double y, double z, boolean onGround);

    void teleport();

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

    UUID getUUID();

    void handlePose(Pose previous, Pose pose);
}
