package net.momirealms.customnameplates.listener.compatibility;

import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.cosmetics.Hat;
import com.francobm.magicosmetics.events.CosmeticChangeEquipEvent;
import com.francobm.magicosmetics.events.CosmeticEquipEvent;
import com.francobm.magicosmetics.events.CosmeticUnEquipEvent;
import com.francobm.magicosmetics.events.PlayerDataLoadEvent;
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
        final Cosmetic cosmetic = event.getNewCosmetic();
        final Player player = event.getPlayer();
        if (cosmetic instanceof Hat hat) {
            NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
            if (asm != null) {
                asm.setHatOffset(hat.getOffSetY());
            }
        }
    }

    @EventHandler
    public void onEquip(CosmeticEquipEvent event) {
        final Cosmetic cosmetic = event.getCosmetic();
        final Player player = event.getPlayer();
        if (cosmetic instanceof Hat hat) {
            NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
            if (asm != null) {
                asm.setHatOffset(hat.getOffSetY());
            }
        }
    }

    @EventHandler
    public void onUnEquip(CosmeticUnEquipEvent event) {
        final Player player = event.getPlayer();
        if (event.getCosmeticType() == CosmeticType.HAT) {
            NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
            if (asm != null) {
                asm.setHatOffset(0);
            }
        }
    }

    @EventHandler
    public void onDataLoaded(PlayerDataLoadEvent event) {
        for (Cosmetic cosmetic : event.getEquippedCosmetics()) {
            if (cosmetic instanceof Hat hat) {
                NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(event.getPlayerData().getOfflinePlayer().getPlayer());
                if (asm != null) {
                    asm.setHatOffset(hat.getOffSetY());
                }
            }
        }
    }
}
