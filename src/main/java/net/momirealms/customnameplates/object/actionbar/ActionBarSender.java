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

package net.momirealms.customnameplates.object.actionbar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ScoreComponent;
import net.momirealms.customnameplates.object.DynamicText;
import net.momirealms.customnameplates.object.requirements.Requirement;
import net.momirealms.customnameplates.utils.AdventureUtils;
import org.bukkit.entity.Player;

public class ActionBarSender {

    private final Player player;
    private final int switch_interval;
    private int timer;
    private int current_text_id;
    private final DynamicText[] dynamicTexts;
    private final Requirement[] requirements;

    public ActionBarSender(int switch_interval, String[] texts, Requirement[] requirements, Player player) {
        this.player = player;
        this.switch_interval = switch_interval;
        this.requirements = requirements;
        this.dynamicTexts = new DynamicText[texts.length];
        for (int i = 0; i < texts.length; i++) {
            dynamicTexts[i] = new DynamicText(player, texts[i]);
        }
        this.current_text_id = 0;
    }

    public boolean canSend() {
        for (Requirement requirement : requirements) {
            if (!requirement.isConditionMet(player)) {
                return false;
            }
        }
        return true;
    }

    public void send() {
        timer++;
        if (timer >= switch_interval) {
            timer = 0;
            current_text_id++;
            if (current_text_id >= dynamicTexts.length) {
                current_text_id = 0;
            }
        }
        dynamicTexts[current_text_id].update();
        ScoreComponent.Builder builder = Component.score().name("nameplates").objective("actionbar");
        builder.append(AdventureUtils.getComponentFromMiniMessage(dynamicTexts[current_text_id].getLatestValue()));
        AdventureUtils.playerActionbar(player, builder.build());
    }
}