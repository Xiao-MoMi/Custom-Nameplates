package net.momirealms.customnameplates.paper.mechanic.team.provider;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.tablist.SortingManager;
import net.momirealms.customnameplates.api.util.LogUtils;
import org.bukkit.entity.Player;

public class TABProvider implements TeamProvider {

    private final TabAPI api;
    private final SortingManager sortingManager;

    public TABProvider() {
        this.api = TabAPI.getInstance();
        this.sortingManager = api.getSortingManager();
        if (sortingManager == null) {
            LogUtils.warn("Detected that team management is disabled in TAB. Using player name as team name.");
        }
    }

    @Override
    public String getTeam(Player player) {
        TabPlayer tabPlayer = api.getPlayer(player.getUniqueId());
        if (tabPlayer == null) {
            return null;
        }
        String forced = sortingManager.getForcedTeamName(tabPlayer);
        if (forced != null) {
            return forced;
        }
        return sortingManager.getOriginalTeamName(tabPlayer);
    }
}
