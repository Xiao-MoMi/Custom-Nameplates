package net.momirealms.customnameplates.listener.compatibility;

import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.cosmetics.Hat;
import com.francobm.magicosmetics.events.*;
import net.momirealms.customnameplates.object.carrier.NamedEntityCarrier;
import net.momirealms.customnameplates.object.carrier.NamedEntityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MagicCosmeticsListener implements Listener {

    private final NamedEntityCarrier namedEntityCarrier;

    public MagicCosmeticsListener(NamedEntityCarrier namedEntityCarrier) {
        this.namedEntityCarrier = namedEntityCarrier;
    }

    @EventHandler
    public void onChangeCos(CosmeticChangeEquipEvent event) {
        setOffset(event.getNewCosmetic(), event.getPlayer());
    }

    @EventHandler
    public void onEquip(CosmeticEquipEvent event) {
        setOffset(event.getCosmetic(), event.getPlayer());
    }

    @EventHandler
    public void onUnEquip(CosmeticUnEquipEvent event) {
        final Player player = event.getPlayer();
        if (event.getCosmeticType() == CosmeticType.HAT) {
            NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
            if (asm != null) {
                asm.setHatOffset(0);
                if (asm.isInWardrobe()) {
                    asm.teleport(player);
                }
            }
        }
    }

    @EventHandler
    public void onDataLoaded(PlayerDataLoadEvent event) {
        for (Cosmetic cosmetic : event.getEquippedCosmetics()) {
            setOffset(cosmetic, event.getPlayerData().getOfflinePlayer().getPlayer());
        }
    }

    @EventHandler
    public void zoneEnter(ZoneEnterEvent event) {
        final Player player = event.getPlayer();
        NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
        if (asm != null) {
            asm.setInWardrobe(true);
            asm.destroy();
            asm.setWardrobeNPC(event.getZone().getNpc());
            asm.spawn(player);
        }
    }

    @EventHandler
    public void zoneExit(ZoneExitEvent event) {
        final Player player = event.getPlayer();
        NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
        if (asm != null) {
            asm.setInWardrobe(false);
            asm.destroy(player);
        }
    }

    private void setOffset(Cosmetic cosmetic, Player player) {
        if (cosmetic instanceof Hat hat) {
            NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
            if (asm != null) {
                asm.setHatOffset(hat.getOffSetY());
                if (asm.isInWardrobe()) {
                    asm.teleport(player);
                }
            }
        }
    }
}
