package net.momirealms.customnameplates.bukkit.compatibility.cosmetic;

import com.hibiscusmc.hmccosmetics.api.events.PlayerCosmeticPostEquipEvent;
import com.hibiscusmc.hmccosmetics.api.events.PlayerCosmeticRemoveEvent;
import com.hibiscusmc.hmccosmetics.cosmetic.CosmeticSlot;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.tag.TagRenderer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HMCCosmeticsHook implements Listener {

    private final CustomNameplates plugin;

    public HMCCosmeticsHook(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChangeCos(PlayerCosmeticPostEquipEvent event) {
        updateOffset(event.getUser().getPlayer(), event.getCosmetic().getSlot());
    }

    @EventHandler(ignoreCancelled = true)
    public void onRemoveCos(PlayerCosmeticRemoveEvent event) {
        updateOffset(event.getUser().getPlayer(), event.getCosmetic().getSlot());
    }

    private void updateOffset(Player player, CosmeticSlot slot) {
        double offset = ConfigManager.hmcCosmeticsOffsetY();

        if (offset <= 0 || slot != CosmeticSlot.HELMET || player == null) return;

        CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
        if (cnPlayer != null) {
            TagRenderer renderer = plugin.getUnlimitedTagManager().getTagRender(cnPlayer);
            if (renderer != null) renderer.hatOffset(offset);
        }
    }

}
