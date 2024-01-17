package net.momirealms.customnameplates.api.mechanic.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SwitchText {

    private HashMap<String, String> valueMap;
    private String toParse;
    private String defaultValue;

    public static Builder builder() {
        return new Builder();
    }

    private SwitchText() {

    }

    public SwitchText(HashMap<String, String> valueMap, String toParse) {
        this.valueMap = valueMap;
        this.toParse = toParse;
    }

    public String getValue(Player player) {
        String parsed = PlaceholderAPI.setPlaceholders(player, toParse);
        return valueMap.getOrDefault(parsed, defaultValue);
    }

    public static class Builder {

        private final SwitchText switchText;

        public Builder() {
            this.switchText = new SwitchText();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder toParse(String toParse) {
            this.switchText.toParse = toParse;
            return this;
        }

        public Builder defaultValue(String value) {
            this.switchText.defaultValue = value;
            return this;
        }

        public Builder valueMap(HashMap<String, String> valueMap) {
            this.switchText.valueMap = valueMap;
            return this;
        }

        public SwitchText build() {
            return switchText;
        }
    }
}
