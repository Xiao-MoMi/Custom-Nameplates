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

package net.momirealms.customnameplates.bukkit.compatibility.cosmetic;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.tag.TagRenderer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.sculmix.ecosmetics.api.Cosmetic;
import ru.sculmix.ecosmetics.api.CosmeticType;
import ru.sculmix.ecosmetics.cache.PlayerData;
import ru.sculmix.ecosmetics.cache.cosmetics.Hat;
import ru.sculmix.ecosmetics.events.*;

import java.util.concurrent.TimeUnit;

public class ECosmeticsHook implements Listener {

    private final CustomNameplates plugin;

    public ECosmeticsHook(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChangeCos(CosmeticChangeEquipEvent event) {
        final Cosmetic cosmetic = event.getNewCosmetic();
        final Player player = event.getPlayer();
        if (cosmetic instanceof Hat hat) {
            CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
            if (cnPlayer != null) {
                TagRenderer renderer = plugin.getUnlimitedTagManager().getTagRender(cnPlayer);
                if (renderer != null) {
                    renderer.hatOffset(hat.isHideCosmetic() ? 0 : hat.getOffSetY());
                }
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onEnterBlackListWorld(PlayerChangeBlacklistEvent event) {
        var player = event.getPlayer();
        CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
        if (cnPlayer != null) {
            TagRenderer renderer = plugin.getUnlimitedTagManager().getTagRender(cnPlayer);
            if (renderer != null) {
                if (event.isInWorldBlacklist()) {
                    renderer.hatOffset(0);
                } else {
                    PlayerData playerData = PlayerData.getPlayer(player);
                    if (playerData != null) {
                        final Cosmetic cosmetic = playerData.getHat();
                        if (cosmetic instanceof Hat hat) {
                            renderer.hatOffset(hat.isHideCosmetic() ? 0 : hat.getOffSetY());
                        }
                    }
                }
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onEquip(CosmeticEquipEvent event) {
        final Cosmetic cosmetic = event.getCosmetic();
        final Player player = event.getPlayer();
        if (cosmetic instanceof Hat hat) {
            CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
            if (cnPlayer != null) {
                TagRenderer renderer = plugin.getUnlimitedTagManager().getTagRender(cnPlayer);
                if (renderer != null) {
                    renderer.hatOffset(hat.isHideCosmetic() ? 0 : hat.getOffSetY());
                }
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onUnEquip(CosmeticUnEquipEvent event) {
        final Player player = event.getPlayer();
        if (event.getCosmeticType() == CosmeticType.HAT) {
            CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
            if (cnPlayer != null) {
                TagRenderer renderer = plugin.getUnlimitedTagManager().getTagRender(cnPlayer);
                if (renderer != null) {
                    renderer.hatOffset(0);
                }
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onDataLoaded(PlayerDataLoadEvent event) {
        for (Cosmetic cosmetic : event.getEquippedCosmetics()) {
            if (cosmetic instanceof Hat hat) {
                PlayerData playerData = event.getPlayerData();
                plugin.getScheduler().asyncLater(() -> {
                    CNPlayer cnPlayer = plugin.getPlayer(playerData.getUniqueId());
                    if (cnPlayer != null) {
                        TagRenderer renderer = plugin.getUnlimitedTagManager().getTagRender(cnPlayer);
                        if (renderer != null) {
                            renderer.hatOffset(hat.isHideCosmetic() ? 0 : hat.getOffSetY());
                        }
                    }
                }, 100, TimeUnit.MILLISECONDS);
            }
        }
    }
}
