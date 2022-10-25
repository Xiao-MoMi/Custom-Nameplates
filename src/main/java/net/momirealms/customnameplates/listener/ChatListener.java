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

package net.momirealms.customnameplates.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.ChatBubblesManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final ChatBubblesManager chatBubblesManager;

    public ChatListener(ChatBubblesManager chatBubblesManager) {
        this.chatBubblesManager = chatBubblesManager;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            Bukkit.getScheduler().runTask(CustomNameplates.plugin, () -> {
                chatBubblesManager.onChat(event.getPlayer(), MiniMessage.miniMessage().stripTags(ChatColor.stripColor(event.getMessage())));
            });
        }
    }
}
