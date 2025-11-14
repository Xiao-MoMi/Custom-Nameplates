package net.momirealms.customnameplates.bukkit.tag;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.backend.feature.tag.AbstractUnlimitedTagManager;
import net.momirealms.customnameplates.backend.feature.tag.TagRendererImpl;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import org.bukkit.entity.Player;

public class BukkitUnlimitedTagManager extends AbstractUnlimitedTagManager {

    public BukkitUnlimitedTagManager(CustomNameplates plugin) {
        super(plugin);
    }

    @Override
    public void onTick() {
        if (!VersionHelper.isFolia()) {
            super.onTick();
        }
    }

    @Override
    public void onPlayerQuit(CNPlayer player) {
        super.onPlayerQuit(player);
    }

    @Override
    public void onPlayerJoin(CNPlayer player) {
        super.plugin.debug(() -> player.name() + " joined the server");
        TagRendererImpl renderer = new TagRendererImpl(this, player);
        TagRendererImpl previous = super.tagRenderers.put(player.entityID(), renderer);
        if (previous != null) {
            previous.destroy();
        }
        if (isAlwaysShow()) {
            setTempPreviewing(player, isAlwaysShow());
        } else if (player.isLoaded() && player.isTempPreviewing()) {
            setTempPreviewing(player, false);
        }
        if (VersionHelper.isFolia()) {
            Player bukkitPlayer = (Player) player.player();
            bukkitPlayer.getScheduler().runAtFixedRate(BukkitCustomNameplates.getInstance().getBootstrap(), (t) -> renderer.onTick(), null, 1, 1);
        }
    }
}
