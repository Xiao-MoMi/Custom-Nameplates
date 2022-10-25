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

package net.momirealms.customnameplates.objects.actionbar;

import net.momirealms.customnameplates.objects.requirements.Requirement;

import java.util.List;

public class ActionBarConfig {

    private final int rate;
    private final int interval;
    private final String[] text;
    private final List<Requirement> conditions;

    public ActionBarConfig(int rate, int interval, String[] text, List<Requirement> conditions) {
        this.rate = rate;
        this.text = text;
        this.conditions = conditions;
        this.interval = interval;
    }

    public int getRate() {
        return rate;
    }

    public String[] getText() {
        return text;
    }

    public List<Requirement> getConditions() {
        return conditions;
    }

    public int getInterval() {
        return interval;
    }
}
