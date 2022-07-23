package net.momirealms.customnameplates.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ParsePapi {

    public static String parsePlaceholders(Player player, String papi) {
        String s = StringUtils.replace(StringUtils.replace(papi, "%player_name%", player.getName()), "%player_displayname%", player.getDisplayName());
        s = PlaceholderAPI.setPlaceholders(player, s);
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
