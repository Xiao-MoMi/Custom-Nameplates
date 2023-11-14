package net.momirealms.customnameplates.listener.compatibility;

import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.api.MagicAPI;
import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.cache.cosmetics.Hat;
import com.francobm.magicosmetics.events.*;
import net.momirealms.customnameplates.object.carrier.NamedEntityCarrier;
import net.momirealms.customnameplates.object.carrier.NamedEntityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class MagicCosmeticsListener implements Listener {

    private final NamedEntityCarrier namedEntityCarrier;

    public MagicCosmeticsListener(NamedEntityCarrier namedEntityCarrier) {
        this.namedEntityCarrier = namedEntityCarrier;
    }

    @EventHandler
    public void onChangeCos(CosmeticChangeEquipEvent event) {
        final Cosmetic cosmetic = event.getNewCosmetic();
        final Player player = event.getPlayer();
        if (cosmetic instanceof Hat hat) {
            NamedEntityManager nem = namedEntityCarrier.getNamedEntityManager(player);
            if (nem != null) {
                nem.setHatOffset(hat.isHideCosmetic() ? 0 : hat.getOffSetY());
            }
        }
    }

    @EventHandler
    public void onEnterBlackListWorld(PlayerChangeBlacklistEvent event) {
        var player = event.getPlayer();
        NamedEntityManager nem = namedEntityCarrier.getNamedEntityManager(player);
        if (nem != null) {
            if (event.isInWorldBlacklist()) {
                nem.setHatOffset(0);
            } else {
                PlayerData playerData = PlayerData.getPlayer(player);
                if (playerData != null) {
                    final Cosmetic cosmetic = playerData.getHat();
                    if (cosmetic instanceof Hat hat) {
                        nem.setHatOffset(hat.isHideCosmetic() ? 0 : hat.getOffSetY());
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
            NamedEntityManager nem = namedEntityCarrier.getNamedEntityManager(player);
            if (nem != null) {
                nem.setHatOffset(hat.isHideCosmetic() ? 0 : hat.getOffSetY());
            }
        }
    }

    @EventHandler
    public void onUnEquip(CosmeticUnEquipEvent event) {
        final Player player = event.getPlayer();
        if (event.getCosmeticType() == CosmeticType.HAT) {
            NamedEntityManager nem = namedEntityCarrier.getNamedEntityManager(player);
            if (nem != null) {
                nem.setHatOffset(0);
            }
        }
    }

    @EventHandler
    public void onDataLoaded(PlayerDataLoadEvent event) {
        for (Cosmetic cosmetic : event.getEquippedCosmetics()) {
            if (cosmetic instanceof Hat hat) {
                NamedEntityManager nem = namedEntityCarrier.getNamedEntityManager(event.getPlayerData().getOfflinePlayer().getPlayer());
                if (nem != null) {
                    nem.setHatOffset(hat.isHideCosmetic() ? 0 : hat.getOffSetY());
                }
            }
        }
    }
}
