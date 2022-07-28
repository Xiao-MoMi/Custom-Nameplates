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

package net.momirealms.customnameplates.bossbar.adventure;

import net.kyori.adventure.bossbar.BossBar;

public class BossBarConfigA {

    private String text;
    private BossBar.Overlay overlay;
    private BossBar.Color color;
    private int rate;

    public BossBarConfigA(String text, BossBar.Overlay overlay,
                          BossBar.Color color, int rate){
        this.text = text;
        this.rate = rate;
        this.overlay = overlay;
        this.color = color;
    }

    public BossBar.Color getColor() {
        return color;
    }
    public int getRate() {
        return rate;
    }
    public BossBar.Overlay getOverlay() {
        return overlay;
    }
    public String getText() {
        return text;
    }

    public void setColor(BossBar.Color color) {
        this.color = color;
    }

    public void setOverlay(BossBar.Overlay overlay) {
        this.overlay = overlay;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setText(String text) {
        this.text = text;
    }
}
