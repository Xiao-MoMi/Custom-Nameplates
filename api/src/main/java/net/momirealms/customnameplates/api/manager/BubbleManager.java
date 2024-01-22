package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.mechanic.bubble.Bubble;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface BubbleManager {

    boolean registerBubble(String key, Bubble bubble);

    boolean unregisterBubble(String key);

    @Nullable Bubble getBubble(String bubble);

    boolean hasBubble(Player player, String bubble);

    List<String> getAvailableBubbles(Player player);

    List<String> getAvailableBubblesDisplayNames(Player player);

    String[] getBlacklistChannels();

    Collection<Bubble> getBubbles();

    boolean equipBubble(Player player, String bubble);

    void unEquipBubble(Player player);

    String getDefaultBubble();

    Collection<String> getBubbleKeys();
}
