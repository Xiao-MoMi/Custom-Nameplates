package net.momirealms.customnameplates.api.mechanic.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.util.FontUtils;
import org.bukkit.OfflinePlayer;

public class DescentText {

    private int ascent;
    private String text;
    private boolean isUnicode;

    public DescentText(int ascent, String text, boolean isUnicode) {
        this.ascent = ascent;
        this.text = text;
        this.isUnicode = isUnicode;
    }

    private DescentText() {

    }

    public static Builder builder() {
        return new Builder();
    }

    public String getValue(OfflinePlayer player) {
        var parsed = PlaceholderAPI.setPlaceholders(player, text);
        return isUnicode ? FontUtils.surroundAscentUnicodeFont(parsed, ascent) : FontUtils.surroundAscentFont(parsed, ascent);
    }

    public static class Builder {

        private final DescentText descentText;

        public static Builder of() {
            return new Builder();
        }

        public Builder() {
            this.descentText = new DescentText();
        }

        public Builder ascent(int ascent) {
            descentText.ascent = ascent;
            return this;
        }

        public Builder descent(int descent) {
            descentText.ascent = 8 - descent;
            return this;
        }

        public Builder text(String text) {
            descentText.text = text;
            return this;
        }

        public Builder unicode(boolean unicode) {
            descentText.isUnicode = unicode;
            return this;
        }

        public DescentText build() {
            return descentText;
        }
    }
}
