package net.momirealms.customnameplates.listener.compatibility;

import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.api.MagicAPI;
import com.francobm.magicosmetics.cache.cosmetics.Hat;
import com.francobm.magicosmetics.events.CosmeticChangeEquipEvent;
import com.francobm.magicosmetics.events.CosmeticEquipEvent;
import com.francobm.magicosmetics.events.CosmeticUnEquipEvent;
import net.momirealms.customnameplates.object.armorstand.ArmorStandManager;
import net.momirealms.customnameplates.object.nameplate.mode.EntityTag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MagicCosmeticsListener implements Listener {

    private final EntityTag entityTag;

    public MagicCosmeticsListener(EntityTag entityTag) {
        this.entityTag = entityTag;
    }

    @EventHandler
    public void onChangeCos(CosmeticChangeEquipEvent event) {
        final Cosmetic cosmetic = event.getNewCosmetic();
        final Player player = event.getPlayer();
        if (cosmetic instanceof Hat hat) {
            ArmorStandManager asm = entityTag.getArmorStandManager(player);
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
            ArmorStandManager asm = entityTag.getArmorStandManager(player);
            if (asm != null) {
                asm.setHatOffset(hat.getOffSetY());
            }
        }
    }

    @EventHandler
    public void onUnEquip(CosmeticUnEquipEvent event) {
        final Player player = event.getPlayer();
        if (event.getCosmeticType() == CosmeticType.HAT) {
            ArmorStandManager asm = entityTag.getArmorStandManager(player);
            if (asm != null) {
                asm.setHatOffset(0);
            }
        }
    }

    //TODO Lack an event
    public void onJoin(Player player) {

    }
}
