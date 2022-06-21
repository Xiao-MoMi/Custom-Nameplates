package net.momirealms.customnameplates;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AdventureManager {
    //发送控制台消息
    public static void consoleMessage(String s) {
        Audience au = CustomNameplates.adventure.sender(Bukkit.getConsoleSender());
        MiniMessage mm = MiniMessage.miniMessage();
        Component parsed = mm.deserialize(s);
        au.sendMessage(parsed);
    }
    //发送玩家消息
    public static void playerMessage(Player player, String s){
        Audience au = CustomNameplates.adventure.player(player);
        MiniMessage mm = MiniMessage.miniMessage();
        Component parsed = mm.deserialize(s);
        au.sendMessage(parsed);
    }
}
