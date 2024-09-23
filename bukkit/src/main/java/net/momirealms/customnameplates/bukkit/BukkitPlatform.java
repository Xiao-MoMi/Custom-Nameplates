package net.momirealms.customnameplates.bukkit;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.Platform;
import net.momirealms.customnameplates.api.feature.bossbar.BossBar;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.api.placeholder.DummyPlaceholder;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.bukkit.util.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitPlatform implements Platform {

    private final CustomNameplates plugin;
    private final boolean placeholderAPI;

    public BukkitPlatform(CustomNameplates plugin) {
        this.plugin = plugin;
        this.placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    @Override
    public Object jsonToMinecraftComponent(String json) {
        if (VersionHelper.isVersionNewerThan1_20_5()) {
            try {
                return Reflections.method$Component$Serializer$fromJson.invoke(null, json, Reflections.instance$MinecraftRegistry);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return Reflections.method$CraftChatMessage$fromJSON.invoke(null, json);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
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
    public void sendActionBar(CNPlayer<?> player, Object component) {
        try {
            plugin.getPacketSender().sendPacket(player, Reflections.constructor$ClientboundSetActionBarTextPacket.newInstance(component));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createBossBar(CNPlayer<?> player, UUID uuid, Object component, float progress, BossBar.Overlay overlay, BossBar.Color color) {
        try {
            Object barColor = Reflections.method$BossEvent$BossBarColor$valueOf.invoke(null, color.name());
            Object barOverlay = Reflections.method$BossEvent$BossBarOverlay$valueOf.invoke(null, overlay.name());
            Object operationInstance = Reflections.allocateAddOperationInstance();
            Reflections.field$ClientboundBossEventPacket$AddOperation$name.set(operationInstance, component);
            Reflections.field$ClientboundBossEventPacket$AddOperation$progress.set(operationInstance, progress);
            Reflections.field$ClientboundBossEventPacket$AddOperation$color.set(operationInstance, barColor);
            Reflections.field$ClientboundBossEventPacket$AddOperation$overlay.set(operationInstance, barOverlay);
            Reflections.field$ClientboundBossEventPacket$AddOperation$darkenScreen.set(operationInstance, false);
            Reflections.field$ClientboundBossEventPacket$AddOperation$playMusic.set(operationInstance, false);
            Reflections.field$ClientboundBossEventPacket$AddOperation$createWorldFog.set(operationInstance, false);
            Object packet = Reflections.constructor$ClientboundBossEventPacket.newInstance(uuid, operationInstance);
            plugin.getPacketSender().sendPacket(player, packet);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeBossBar(CNPlayer<?> player, UUID uuid) {
        try {
            plugin.getPacketSender().sendPacket(player, Reflections.constructor$ClientboundBossEventPacket.newInstance(uuid, Reflections.instance$ClientboundBossEventPacket$REMOVE_OPERATION));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateBossBarName(CNPlayer<?> player, UUID uuid, Object component) {
        try {
            Object operation = Reflections.constructor$ClientboundBossEventPacket$UpdateNameOperation.newInstance(component);
            Object packet = Reflections.constructor$ClientboundBossEventPacket.newInstance(uuid, operation);
            plugin.getPacketSender().sendPacket(player, packet);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
