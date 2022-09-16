package net.momirealms.customnameplates.hook;

import org.bukkit.entity.Player;

public interface ImageParser {

    String parse(Player player, String text);
}
