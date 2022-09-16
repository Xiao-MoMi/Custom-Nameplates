package net.momirealms.customnameplates.hook;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.entity.Player;

public class IAImageHook implements ImageParser{

    @Override
    public String parse(Player player, String text) {
        return FontImageWrapper.replaceFontImages(player, text).replace("§f","<white>").replace("§r","</white>");
    }
}
