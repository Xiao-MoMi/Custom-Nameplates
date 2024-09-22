package net.momirealms.customnameplates.bukkit;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.packet.WrappedActionBarPacket;
import net.momirealms.customnameplates.api.placeholder.DummyPlaceholder;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.bukkit.util.Reflections;
import net.momirealms.customnameplates.api.Platform;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BukkitPlatform implements Platform {

    private final CustomNameplates plugin;
    private final boolean placeholderAPI;

    public BukkitPlatform(CustomNameplates plugin) {
        this.plugin = plugin;
        this.placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    @Override
    public Object jsonToMinecraftComponent(String json) {
        try {
            return Reflections.method$fromJSON.invoke(null, json);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Placeholder registerPlatformPlaceholder(String id) {
        if (!placeholderAPI) {
            return new DummyPlaceholder(id);
        }
        int refreshInterval = plugin.getPlaceholderManager().getRefreshInterval(id);
        Placeholder placeholder;
        if (id.startsWith("%rel_")) {
            placeholder = plugin.getPlaceholderManager().registerRelationalPlaceholder(id, refreshInterval,
                    (p1, p2) -> PlaceholderAPI.setRelationalPlaceholders((Player) p1.player(), (Player) p2.player(), id));
        } else if (id.startsWith("%shared_")) {
            String sub = "%" + id.substring("%shared_".length());
            placeholder =plugin.getPlaceholderManager().registerSharedPlaceholder(id, refreshInterval,
                    () -> PlaceholderAPI.setPlaceholders(null, sub));
        } else {
            placeholder = plugin.getPlaceholderManager().registerPlayerPlaceholder(id, refreshInterval,
                    (p) -> PlaceholderAPI.setPlaceholders((OfflinePlayer) p.player(), id));
        }
        return placeholder;
    }

    @Override
    public void sendActionBar(CNPlayer<?> player, WrappedActionBarPacket packet) {
        try {
            plugin.getPacketSender().sendPacket(player, Reflections.constructor$ClientboundSetActionBarTextPacket.newInstance(packet.component()));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
