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

import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.cache.cosmetics.Hat;
import com.francobm.magicosmetics.events.*;
import net.momirealms.customnameplates.paper.mechanic.nameplate.tag.unlimited.UnlimitedPlayer;
import net.momirealms.customnameplates.paper.mechanic.nameplate.tag.unlimited.UnlimitedTagManagerImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MagicCosmeticsListener implements Listener {

    private final UnlimitedTagManagerImpl unlimitedTagManager;

    public MagicCosmeticsListener(UnlimitedTagManagerImpl unlimitedTagManager) {
        this.unlimitedTagManager = unlimitedTagManager;
    }

    @EventHandler
    public void onChangeCos(CosmeticChangeEquipEvent event) {
        final Cosmetic cosmetic = event.getNewCosmetic();
        final Player player = event.getPlayer();
        if (cosmetic instanceof Hat hat) {
            if (unlimitedTagManager.getUnlimitedObject(player.getUniqueId()) instanceof UnlimitedPlayer unlimitedPlayer) {
                unlimitedPlayer.setHatOffset(hat.isHideCosmetic() ? 0 : hat.getOffSetY());
            }
        }
    }

    @EventHandler
    public void onEnterBlackListWorld(PlayerChangeBlacklistEvent event) {
        var player = event.getPlayer();
        if (unlimitedTagManager.getUnlimitedObject(player.getUniqueId()) instanceof UnlimitedPlayer unlimitedPlayer) {
            if (event.isInWorldBlacklist()) {
                unlimitedPlayer.setHatOffset(0);
            } else {
                PlayerData playerData = PlayerData.getPlayer(player);
                if (playerData != null) {
                    final Cosmetic cosmetic = playerData.getHat();
                    if (cosmetic instanceof Hat hat) {
                        unlimitedPlayer.setHatOffset(hat.isHideCosmetic() ? 0 : hat.getOffSetY());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEquip(CosmeticEquipEvent event) {
        final Cosmetic cosmetic = event.getCosmetic();
        final Player player = event.getPlayer();
        if (cosmetic instanceof Hat hat) {
            if (unlimitedTagManager.getUnlimitedObject(player.getUniqueId()) instanceof UnlimitedPlayer unlimitedPlayer) {
                unlimitedPlayer.setHatOffset(hat.isHideCosmetic() ? 0 : hat.getOffSetY());
            }
        }
    }

    @EventHandler
    public void onUnEquip(CosmeticUnEquipEvent event) {
        final Player player = event.getPlayer();
        if (event.getCosmeticType() == CosmeticType.HAT) {
            if (unlimitedTagManager.getUnlimitedObject(player.getUniqueId()) instanceof UnlimitedPlayer unlimitedPlayer) {
                unlimitedPlayer.setHatOffset(0);
            }
        }
    }

    @EventHandler
    public void onDataLoaded(PlayerDataLoadEvent event) {
        for (Cosmetic cosmetic : event.getEquippedCosmetics()) {
            if (cosmetic instanceof Hat hat) {
                if (unlimitedTagManager.getUnlimitedObject(event.getPlayerData().getUniqueId()) instanceof UnlimitedPlayer unlimitedPlayer) {
                    unlimitedPlayer.setHatOffset(hat.isHideCosmetic() ? 0 : hat.getOffSetY());
                }
            }
        }
    }
}
