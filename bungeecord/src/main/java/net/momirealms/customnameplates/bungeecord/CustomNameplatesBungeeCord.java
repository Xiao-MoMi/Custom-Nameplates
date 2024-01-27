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

package net.momirealms.customnameplates.bungeecord;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class CustomNameplatesBungeeCord extends Plugin implements Listener {

    private static final String CHANNEL = "customnameplates:cnp";
    private static CustomNameplatesBungeeCord instance;

    @Override
    public void onEnable() {
        instance = this;
        this.getProxy().registerChannel(CHANNEL);
        this.getProxy().getPluginManager().registerListener(this, this);
    }

    @Override
    public void onDisable() {
        this.getProxy().unregisterChannel(CHANNEL);
        this.getProxy().getPluginManager().unregisterListener(this);
    }

    public static CustomNameplatesBungeeCord getInstance() {
        return instance;
    }

    public static CustomNameplatesBungeeCord get() {
        return instance;
    }
}
