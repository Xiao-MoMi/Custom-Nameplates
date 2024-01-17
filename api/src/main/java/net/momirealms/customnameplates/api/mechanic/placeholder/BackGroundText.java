package net.momirealms.customnameplates.api.mechanic.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.mechanic.background.BackGround;
import net.momirealms.customnameplates.api.util.FontUtils;
import org.bukkit.OfflinePlayer;

public class BackGroundText {

    private String text;
    private BackGround backGround;

    private BackGroundText() {
    }

    public BackGroundText(String text, BackGround backGround) {
        this.text = text;
        this.backGround = backGround;
    }

    public String getText() {
        return text;
    }

    public BackGround getBackGround() {
        return backGround;
    }

    public String getValue(OfflinePlayer player) {
        String parsed = PlaceholderAPI.setPlaceholders(player, text);
        int parsedWidth = FontUtils.getTextWidth(parsed);
        return backGround.getBackGroundImage(parsedWidth) + parsed;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final BackGroundText text;

        public Builder() {
            this.text = new BackGroundText();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder text(String value) {
            text.text = value;
            return this;
        }

        public Builder background(BackGround backGround) {
            text.backGround = backGround;
            return this;
        }

        public BackGroundText build() {
            return text;
        }
    }
}
