package net.momirealms.customnameplates.bukkit;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.Platform;
import net.momirealms.customnameplates.api.feature.actionbar.ActionBarManagerImpl;
import net.momirealms.customnameplates.api.feature.bossbar.BossBar;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.api.placeholder.DummyPlaceholder;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.bukkit.util.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Ref;
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
    public String minecraftComponentToJson(Object component) {
        if (VersionHelper.isVersionNewerThan1_20_5()) {
            try {
                return (String) Reflections.method$Component$Serializer$toJson.invoke(null, component, Reflections.instance$MinecraftRegistry);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return (String) Reflections.method$CraftChatMessage$toJSON.invoke(null, component);
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

    @SuppressWarnings("unchecked")
    @Override
    public void onPacketSend(CNPlayer<?> player, Object packet) {
        try {
            if (Reflections.clazz$ClientboundBundlePacket.isInstance(packet)) {
                Iterable<Object> packets = (Iterable<Object>) Reflections.field$BundlePacket$packets.get(packet);
                for (Object p : packets) {
                    handlePacket(player, p);
                }
            } else {
                handlePacket(player, packet);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void handlePacket(CNPlayer<?> player, Object packet) throws ReflectiveOperationException {
        switch (packet.getClass().getSimpleName()) {
            case "ClientboundSetActionBarTextPacket" -> {
                if (!ConfigManager.actionbarModule()) return;
                Object component = Reflections.field$ClientboundSetActionBarTextPacket$text.get(packet);
                Object contents = Reflections.method$Component$getContents.invoke(component);
                if (Reflections.clazz$ScoreContents.isAssignableFrom(contents.getClass())) {
                    String name = (String) Reflections.field$ScoreContents$name.get(contents);
                    String objective = (String) Reflections.field$ScoreContents$objective.get(contents);
                    if (name.equals("np") && objective.equals("ab")) return;
                }
                ((ActionBarManagerImpl) plugin.getActionBarManager()).handleActionBarPacket(player, AdventureHelper.minecraftComponentToMiniMessage(component));
            }
            case "ClientboundSystemChatPacket" -> {
                if (!ConfigManager.actionbarModule()) return;
                boolean actionBar = (boolean) Reflections.field$ClientboundSystemChatPacket$overlay.get(packet);
                if (actionBar) {
                    if (VersionHelper.isVersionNewerThan1_20_4()) {
                        Object component = Reflections.field$ClientboundSystemChatPacket$component.get(packet);
                        if (component == null) return;
                        ((ActionBarManagerImpl) plugin.getActionBarManager()).handleActionBarPacket(player, AdventureHelper.minecraftComponentToMiniMessage(component));
                    } else {
                        String json = (String) Reflections.field$ClientboundSystemChatPacket$text.get(packet);
                        if (json == null) return;
                        ((ActionBarManagerImpl) plugin.getActionBarManager()).handleActionBarPacket(player, AdventureHelper.jsonToMiniMessage(json));
                    }
                }
            }
        }
    }
}
