package net.momirealms.customnameplates.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customnameplates.font.FontOffset;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class OffsetPlaceholders extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "offset";
    }

    @Override
    public @NotNull String getAuthor() {
        return "XiaoMoMi";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params == null) return null;
        try {
            int offset = Integer.parseInt(params);
            if (offset > 0) {
                return FontOffset.getShortestPosChars(offset);
            }
            else {
                return FontOffset.getShortestNegChars(-offset);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
