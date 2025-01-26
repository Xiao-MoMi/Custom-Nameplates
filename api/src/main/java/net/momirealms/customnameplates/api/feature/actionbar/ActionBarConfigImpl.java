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

package net.momirealms.customnameplates.api.feature.actionbar;

import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of ActionBarConfig
 */
public class ActionBarConfigImpl implements ActionBarConfig {
    private final String id;
    private final Requirement[] requirements;
    private final CarouselText[] carouselTexts;

    private ActionBarConfigImpl(String id, Requirement[] requirements, CarouselText[] carouselTexts) {
        this.id = requireNonNull(id);
        this.requirements = requireNonNull(requirements);
        this.carouselTexts = requireNonNull(carouselTexts);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Requirement[] requirements() {
        return requirements;
    }

    @Override
    public CarouselText[] carouselTexts() {
        return carouselTexts;
    }

    /**
     * Builder Implementation
     */
    public static class BuilderImpl implements Builder {
        private String id;
        private Requirement[] requirements;
        private CarouselText[] carouselTexts;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder requirement(Requirement[] requirements) {
            this.requirements = requirements;
            return this;
        }

        @Override
        public Builder carouselText(CarouselText[] carouselTexts) {
            this.carouselTexts = carouselTexts;
            return this;
        }

        @Override
        public ActionBarConfig build() {
            return new ActionBarConfigImpl(id, requirements, carouselTexts);
        }
    }
}
