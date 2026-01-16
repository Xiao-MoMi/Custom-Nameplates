package net.momirealms.customnameplates.bukkit.compatibility.cosmetic;

import com.hibiscusmc.hmccosmetics.api.events.*;
import com.hibiscusmc.hmccosmetics.cosmetic.Cosmetic;
import com.hibiscusmc.hmccosmetics.cosmetic.CosmeticSlot;
import me.lojosho.shaded.configurate.ConfigurationNode;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.tag.TagRenderer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnstableApiUsage")
public class HMCCosmeticsHook implements Listener {

    private final CustomNameplates plugin;

    public HMCCosmeticsHook(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCosmeticEquip(PlayerCosmeticEquipEvent event) {
        Cosmetic cosmetic = event.getCosmetic();
        if (cosmetic.getSlot() != CosmeticSlot.HELMET) return;
        ConfigurationNode config = cosmetic.getConfig();
        if (config == null) return;
        updateHatOffset(event.getUniqueId(), config.node("hat-height").getInt(0));
    }

    @EventHandler
    public void onPlayerCosmeticRemove(PlayerCosmeticRemoveEvent event) {
        Cosmetic cosmetic = event.getCosmetic();
        if (cosmetic.getSlot() != CosmeticSlot.HELMET) return;
        updateHatOffset(event.getUniqueId(), 0);
    }

    @EventHandler
    public void onPlayerCosmeticHide(PlayerCosmeticHideEvent event) {
        updateHatOffset(event.getUniqueId(), 0);
    }

    @EventHandler
    public void onPlayerCosmeticShow(PlayerCosmeticShowEvent event) {
        Cosmetic cosmetic = event.getUser().getCosmetic(CosmeticSlot.HELMET);
        if (cosmetic == null) return;
        ConfigurationNode config = cosmetic.getConfig();
        if (config == null) return;
        updateHatOffset(event.getUniqueId(), config.node("hat-height").getInt(0));
    }

    @EventHandler
    public void onPlayerLoad(PlayerLoadEvent event) {
        Cosmetic cosmetic = event.getUser().getCosmetic(CosmeticSlot.HELMET);
        if (cosmetic == null) return;
        ConfigurationNode config = cosmetic.getConfig();
        if (config == null) return;
        this.plugin.getScheduler().asyncLater(
                () -> updateHatOffset(event.getUniqueId(), config.node("hat-height").getInt(0)),
                100, TimeUnit.MILLISECONDS
        );
    }

    private void updateHatOffset(UUID uid, int hatHeight) {
        CNPlayer player = this.plugin.getPlayer(uid);
        if (player == null) return;
        TagRenderer renderer = this.plugin.getUnlimitedTagManager().getTagRender(player);
        if (renderer == null) return;
        renderer.hatOffset(hatHeight);
    }
}
