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
