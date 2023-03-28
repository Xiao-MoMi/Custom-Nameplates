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

package net.momirealms.customnameplates.data;

import java.util.UUID;

public class PlayerData {

    public static PlayerData EMPTY = new PlayerData(UUID.randomUUID(), "none", "none");
    private final UUID uuid;
    private String nameplate;
    private String bubble;

    public PlayerData(UUID uuid, String nameplate, String bubble) {
        this.nameplate = nameplate;
        this.bubble = bubble;
        this.uuid = uuid;
    }

    public String getNameplate() {
        return this.nameplate;
    }

    public void setNameplate(String nameplate) {
        this.nameplate = nameplate;
    }

    public String getBubble() {
        return bubble;
    }

    public void setBubble(String bubble) {
        this.bubble = bubble;
    }

    public UUID getUuid() {
        return uuid;
    }
}
