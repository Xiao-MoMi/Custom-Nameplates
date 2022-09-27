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

package net.momirealms.customnameplates.actionbar;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.Function;

import java.util.HashSet;

public class ActionBarManager extends Function {

    private HashSet<ActionBarTask> tasks = new HashSet<>();

    public ActionBarManager(String name) {
        super(name);
    }

    @Override
    public void load() {
        for (ActionBarConfig config : ConfigManager.actionBars.values()) {
            tasks.add(new ActionBarTask(config));
        }
    }

    @Override
    public void unload() {
        for (ActionBarTask task : tasks) {
            task.stop();
        }
        tasks.clear();
    }
}
