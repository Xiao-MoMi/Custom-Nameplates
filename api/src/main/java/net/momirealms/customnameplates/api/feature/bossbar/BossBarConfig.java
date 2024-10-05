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

package net.momirealms.customnameplates.api.feature.bossbar;

import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;

public interface BossBarConfig {
    
    BossBar.Overlay DEFAULT_OVERLAY = BossBar.Overlay.PROGRESS;
    BossBar.Color DEFAULT_COLOR = BossBar.Color.YELLOW;

    String id();

    Requirement[] requirements();

    CarouselText[] carouselTexts();
    
    BossBar.Overlay overlay();
    
    BossBar.Color color();

    float progress();

    static BossBarConfig.Builder builder() {
        return new BossBarConfigImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder requirement(Requirement[] requirements);

        Builder carouselText(CarouselText[] carouselTexts);
        
        Builder overlay(BossBar.Overlay overlay);
        
        Builder color(BossBar.Color color);

        Builder progress(float progress);

        BossBarConfig build();
    }
}
