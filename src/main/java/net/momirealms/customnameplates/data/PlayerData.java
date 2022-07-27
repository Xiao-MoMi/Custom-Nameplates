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

public class PlayerData {

    private String equipped;
    private int accepted;

    public PlayerData(String equipped, int accepted) {
        this.equipped = equipped;
        this.accepted = accepted;
    }

    public int getAccepted(){
        return this.accepted;
    }

    public void setAccepted(int accepted){
        this.accepted = accepted;
    }

    public String getEquippedNameplate() {
        return this.equipped;
    }

    public void equipNameplate(String nameplate) {
        this.equipped = nameplate.toLowerCase();
    }
}
