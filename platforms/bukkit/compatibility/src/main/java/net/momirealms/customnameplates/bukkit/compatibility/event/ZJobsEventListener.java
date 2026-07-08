/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.bukkit.compatibility.event;

import net.momirealms.customnameplates.api.AbstractCNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Example listener for zJobs plugin events.
 * This demonstrates how to track JobExpGainEvent and similar events.
 *
 * To enable this listener, it needs to be registered in the main plugin class.
 *
 * Example configuration usage:
 * requirements:
 *   event-type: "JobExpGainEvent"
 *
 * Note: This is a template. The actual JobExpGainEvent class needs to be imported
 * from the zJobs plugin when it's available as a dependency.
 */
public class ZJobsEventListener implements Listener {

    private final CustomNameplates plugin;
    private static final int DEFAULT_DISPLAY_DURATION = 40; // 2 seconds in ticks

    public ZJobsEventListener(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    /**
     * Uncomment and use this method when zJobs is available as a dependency:
     *
     * @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
     * public void onJobExpGain(fr.maxlego08.zjobs.api.event.JobExpGainEvent event) {
     *     Player player = event.getPlayer();
     *     markEventForPlayer(player, "JobExpGainEvent", DEFAULT_DISPLAY_DURATION);
     * }
     *
     * @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
     * public void onJobLevelUp(fr.maxlego08.zjobs.api.event.JobLevelUpEvent event) {
     *     Player player = event.getPlayer();
     *     markEventForPlayer(player, "JobLevelUpEvent", 60); // 3 seconds
     * }
     */

    /**
     * Helper method to mark an event as active for a player
     *
     * @param player The player who triggered the event
     * @param eventType The event type name
     * @param durationTicks How long the event should remain active
     */
    private void markEventForPlayer(Player player, String eventType, int durationTicks) {
        AbstractCNPlayer cnPlayer = (AbstractCNPlayer) plugin.getPlayer(player.getUniqueId());
        if (cnPlayer == null) return;

        cnPlayer.markEventActive(eventType);

        // Schedule task to remove the event marker after the duration
        Bukkit.getScheduler().runTaskLater(
            (org.bukkit.plugin.Plugin) plugin,
            () -> {
                AbstractCNPlayer player1 = (AbstractCNPlayer) plugin.getPlayer(player.getUniqueId());
                if (player1 != null) {
                    player1.markEventInactive(eventType);
                }
            },
            durationTicks
        );
    }
}
