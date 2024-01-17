package net.momirealms.customnameplates.api.manager;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ActionBarManager {

    /**
     * Get the actionbar sent by other plugins in MiniMessage format
     * Return "" if no other actionbar received
     *
     * @param player receiver
     * @return text
     */
    @NotNull
    String getOtherPluginActionBar(Player player);
}
