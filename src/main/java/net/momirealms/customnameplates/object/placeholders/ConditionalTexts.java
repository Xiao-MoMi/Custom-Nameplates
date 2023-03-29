package net.momirealms.customnameplates.object.placeholders;

import net.momirealms.customnameplates.object.ConditionalText;
import net.momirealms.customnameplates.object.requirements.PlayerCondition;
import net.momirealms.customnameplates.object.requirements.Requirement;
import org.bukkit.entity.Player;

public class ConditionalTexts {

    private final ConditionalText[] conditionalTexts;

    public ConditionalTexts(ConditionalText[] conditionalTexts) {
        this.conditionalTexts = conditionalTexts;
    }

    public String getValue(Player player) {
        PlayerCondition playerCondition = new PlayerCondition(player);
        outer:
            for (ConditionalText conditionalText : conditionalTexts) {
                for (Requirement requirement : conditionalText.getRequirements()) {
                    if (!requirement.isConditionMet(playerCondition)) {
                        continue outer;
                    }
                }
                return conditionalText.getText();
            }
            return "";
    }
}
