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
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Generic event listener that tracks events for the event-type requirement.
 * This listener can be extended or used as a reference to track specific events from plugins like zJobs.
 *
 * To use this with zJobs JobExpGainEvent or any other custom event:
 * 1. Create a listener for the specific event
 * 2. Call markEventActive with the event type name when the event fires
 * 3. Schedule a task to call markEventInactive after the desired duration
 *
 * Example usage in config:
 * requirements:
 *   event-type: "JobExpGainEvent"
 */
public class GenericEventListener implements Listener {

    private final CustomNameplates plugin;
    private final String eventTypeName;
    private final int displayDurationTicks;

    /**
     * Creates a generic event listener
     *
     * @param plugin CustomNameplates plugin instance
     * @param eventTypeName The name of the event type to track
     * @param displayDurationTicks How long the event should remain active (in ticks)
     */
    public GenericEventListener(CustomNameplates plugin, String eventTypeName, int displayDurationTicks) {
        this.plugin = plugin;
        this.eventTypeName = eventTypeName;
        this.displayDurationTicks = displayDurationTicks;
    }

    /**
     * Helper method to mark an event as active for a player
     *
     * @param player The player who triggered the event
     * @param eventType The event type name
     * @param durationTicks How long the event should remain active
     */
    protected void markEventForPlayer(Player player, String eventType, int durationTicks) {
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
