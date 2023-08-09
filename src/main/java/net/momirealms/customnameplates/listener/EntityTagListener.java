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

import net.momirealms.customnameplates.object.carrier.NamedEntityCarrier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public record EntityTagListener(NamedEntityCarrier namedEntityCarrier) implements Listener {

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.isCancelled()) return;
        namedEntityCarrier.onSneak(event.getPlayer(), event.isSneaking());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        namedEntityCarrier.onRespawn(event.getPlayer());
    }

    @EventHandler
    public void onExitVehicle(VehicleExitEvent event) {
        if (event.isCancelled()) return;
        if (event.getExited() instanceof Player player) {
            namedEntityCarrier.onSneak(player, false);
        }
    }
}