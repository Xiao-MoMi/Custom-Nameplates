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

package net.momirealms.customnameplates.backend.feature.bossbar;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.feature.bossbar.BossBarConfig;
import net.momirealms.customnameplates.api.feature.bossbar.BossBarManager;

import java.util.ArrayList;
import java.util.List;

public class BossBarDisplayController {

    private final CNPlayer owner;
    private final BossBarManager manager;
    private final BossBarSender[] senders;

    public BossBarDisplayController(BossBarManager manager, CNPlayer owner) {
        this.owner = owner;
        this.manager = manager;
        List<BossBarSender> senderList = new ArrayList<>();
        for (BossBarConfig config : manager.bossBarConfigs()) {
            BossBarSender sender = new BossBarSender(owner, config);
            senderList.add(sender);
            this.owner.addFeature(sender);
        }
        this.senders = senderList.toArray(new BossBarSender[0]);
    }

    public void onTick() {
        int size = senders.length;
        int[] states = new int[size];
        int index = size;
        for (int i = 0; i < size; i++) {
            BossBarSender sender = senders[i];
            boolean canShow = sender.checkConditions();
            if (canShow) {
                if (!sender.isShown()) {
                    states[i] = 1;
                    sender.init();
                    sender.tick();
                    if (index == size) {
                        index = i;
                    }
                } else {
                    states[i] = 2;
                    if (i > index) {
                        sender.hide();
                    }
                    sender.tick();
                }
            } else {
                if (sender.isShown()) {
                    sender.hide();
                }
                states[i] = 0;
            }
        }
        if (index != size) {
            for (int i = index; i < size; i++) {
                if (states[i] != 0) {
                    senders[i].show();
                }
            }
        }
    }

    public void destroy() {
        for (BossBarSender sender : this.senders) {
            sender.hide();
            this.owner.removeFeature(sender);
        }
    }
}
