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

package net.momirealms.customnameplates.objects.data;

import org.bukkit.OfflinePlayer;

public class PlayerData {
    private String equipped;
    private String bubbles;
    private final OfflinePlayer player;

    public PlayerData(OfflinePlayer player, String equipped, String bubbles) {
        this.equipped = equipped;
        this.bubbles = bubbles;
        this.player = player;
    }

    public String getEquippedNameplate() {
        return this.equipped;
    }

    public void equipNameplate(String nameplate) {
        this.equipped = nameplate.toLowerCase();
    }

    public String getBubbles() {
        return bubbles;
    }

    public void setBubbles(String bubbles) {
        this.bubbles = bubbles;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }
}
