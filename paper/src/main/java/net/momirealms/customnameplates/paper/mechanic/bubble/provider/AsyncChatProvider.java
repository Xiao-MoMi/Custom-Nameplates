/*
 *  Copyright (C) <2022> <XiaoMoMi>
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

package net.momirealms.customnameplates.paper.mechanic.bubble.provider;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.BubbleManager;
import net.momirealms.customnameplates.api.mechanic.bubble.provider.AbstractChatProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncChatProvider extends AbstractChatProvider {

    public AsyncChatProvider(BubbleManager chatBubblesManager) {
        super(chatBubblesManager);
    }

    @Override
    public void register() {
        Bukkit.getPluginManager().registerEvents(this, CustomNameplatesPlugin.get());
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean hasJoinedChannel(Player player, String channelID) {
        return true;
    }

    @Override
    public boolean canJoinChannel(Player player, String channelID) {
        return true;
    }

    // This event is not async sometimes
    @EventHandler (ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() -> chatBubblesManager.onChat(event.getPlayer(), event.getMessage()));
    }
}