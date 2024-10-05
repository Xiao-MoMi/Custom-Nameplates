/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customnameplates.api.feature.tag;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.util.Vector3;

import java.util.UUID;

public interface Tag {

    String id();

    int entityID();

    UUID uuid();

    boolean affectedByCrouching();

    boolean affectedByScaling();

    void hide();

    void hide(CNPlayer viewer);

    void show();

    void show(CNPlayer viewer);

    void respawn();

    void respawn(CNPlayer viewer);

    byte opacity();

    void updateOpacity(byte opacity);

    void updateOpacity(CNPlayer viewer, byte opacity);

    boolean canShow();
    
    boolean canShow(CNPlayer viewer);

    boolean isShown();

    boolean isShown(CNPlayer viewer);

    void tick();

    void init();

    double getTextHeight(CNPlayer viewer);

    void updateScale(double scale);

    void updateScale(CNPlayer viewer, double scale);

    void updateTranslation();

    Vector3 scale(CNPlayer viewer);

    Vector3 translation(CNPlayer viewer);
}
