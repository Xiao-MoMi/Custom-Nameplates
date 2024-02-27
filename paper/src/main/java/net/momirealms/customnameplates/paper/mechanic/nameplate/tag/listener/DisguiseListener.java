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

package net.momirealms.customnameplates.paper.mechanic.nameplate.tag.listener;

import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.paper.mechanic.nameplate.tag.unlimited.UnlimitedPlayer;
import net.momirealms.customnameplates.paper.mechanic.nameplate.tag.unlimited.UnlimitedTagManagerImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

public class DisguiseListener implements Listener {

    private final UnlimitedTagManagerImpl unlimitedTagManager;

    public DisguiseListener(UnlimitedTagManagerImpl unlimitedTagManager) {
        this.unlimitedTagManager = unlimitedTagManager;
    }

    @EventHandler (ignoreCancelled = true)
    public void onDisguise(DisguiseEvent event) {
        if (this.unlimitedTagManager.getUnlimitedObject(event.getEntity().getUniqueId()) instanceof UnlimitedPlayer player) {
            CustomNameplatesPlugin.get().getScheduler().runTaskAsyncLater(player::updateVisibility, 50, TimeUnit.MILLISECONDS);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onUnDisguise(UndisguiseEvent event) {
        if (this.unlimitedTagManager.getUnlimitedObject(event.getEntity().getUniqueId()) instanceof UnlimitedPlayer player) {
            CustomNameplatesPlugin.get().getScheduler().runTaskAsyncLater(player::updateVisibility, 50, TimeUnit.MILLISECONDS);
        }
    }
}