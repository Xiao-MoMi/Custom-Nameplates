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
        if (requirements.length == 0) return true;
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