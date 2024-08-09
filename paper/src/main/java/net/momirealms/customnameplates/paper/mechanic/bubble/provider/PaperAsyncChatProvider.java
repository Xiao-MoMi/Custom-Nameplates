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

import io.papermc.paper.event.player.AsyncChatEvent;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.BubbleManager;
import net.momirealms.customnameplates.api.mechanic.bubble.provider.AbstractChatProvider;
import net.momirealms.customnameplates.paper.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PaperAsyncChatProvider extends AbstractChatProvider {

    private Method messageMethod;

    public PaperAsyncChatProvider(BubbleManager chatBubblesManager) {
        super(chatBubblesManager);
        try {
            this.messageMethod = AsyncChatEvent.class.getMethod("message");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public boolean isIgnoring(Player sender, Player receiver) {
        return false;
    }

    @EventHandler (ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Object component = getComponentFromEvent(event);
        String message = ReflectionUtils.getMiniMessageTextFromNonShadedComponent(component);
        CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() -> chatBubblesManager.onChat(event.getPlayer(), message));
    }

    private Object getComponentFromEvent(AsyncChatEvent event) {
        try {
            return this.messageMethod.invoke(event);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ReflectionUtils.getEmptyComponent();
    }
}