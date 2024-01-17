package net.momirealms.customnameplates.api.mechanic.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.common.Pair;
import net.momirealms.customnameplates.api.manager.RequirementManager;
import net.momirealms.customnameplates.api.requirement.Condition;
import net.momirealms.customnameplates.api.requirement.Requirement;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class ConditionalText {

    private List<Pair<String, Requirement[]>> textList;

    private ConditionalText() {
    }

    public ConditionalText(List<Pair<String, Requirement[]>> textList) {
        this.textList = textList;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getValue(OfflinePlayer player) {
        Condition condition = new Condition(player);
        for (Pair<String, Requirement[]> pair : textList) {
            if (RequirementManager.isRequirementMet(condition, pair.right())) {
                return PlaceholderAPI.setPlaceholders(player, pair.left());
            }
        }
        return "";
    }

    public static class Builder {

        private final ConditionalText conditionalText;

        public static Builder of() {
            return new Builder();
        }

        public Builder() {
            this.conditionalText = new ConditionalText();
        }

        public Builder textList(List<Pair<String, Requirement[]>> textList) {
            conditionalText.textList = textList;
            return this;
        }

        public ConditionalText build() {
            return conditionalText;
        }
    }
}
