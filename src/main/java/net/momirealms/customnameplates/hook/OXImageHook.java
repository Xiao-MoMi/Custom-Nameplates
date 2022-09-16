package net.momirealms.customnameplates.hook;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.font.FontManager;
import io.th0rgal.oraxen.font.Glyph;
import org.bukkit.entity.Player;

import java.util.Map;

public class OXImageHook implements ImageParser{

    private final FontManager fontManager;

    public OXImageHook() {
        this.fontManager = OraxenPlugin.get().getFontManager();
    }

    @Override
    public String parse(Player player, String text) {
        for (Map.Entry<String, Glyph> entry : this.fontManager.getGlyphByPlaceholderMap().entrySet()) {
            if (entry.getValue().hasPermission(player)) {
                text = text.replace(entry.getKey(), "<white>" + entry.getValue().getCharacter() + "</white>");
            }
        }
        return text;
    }
}
