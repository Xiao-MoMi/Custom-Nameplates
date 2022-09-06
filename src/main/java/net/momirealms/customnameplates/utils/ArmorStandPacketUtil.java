package net.momirealms.customnameplates.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.data.DataManager;
import net.momirealms.customnameplates.data.PlayerData;
import net.momirealms.customnameplates.hook.PapiHook;
import net.momirealms.customnameplates.objects.ASInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ArmorStandPacketUtil {

    public static ConcurrentHashMap<Integer, List<ASInfo>> cache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Player, int[]> player2ids = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, int[]> id2ids = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, HashMap<Integer, BukkitTask>> taskCache = new ConcurrentHashMap<>();

    public static void preparePackets(Player player) {

        int size = ConfigManager.Nameplate.texts.size();
        int[] ids = new int[size];
        List<ASInfo> asInfos = new ArrayList<>();

        for (int i = 0; i < size; i++) {

            PacketContainer entityPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);

            int id = new Random().nextInt(1000000000);
            ids[i] = id;

            String text = ConfigManager.Nameplate.texts.get(i);
            entityPacket.getModifier().write(0, id);
            entityPacket.getModifier().write(1, UUID.randomUUID());
            entityPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
            Location location = player.getLocation();
            entityPacket.getDoubles().write(0, location.getX());
            entityPacket.getDoubles().write(1, location.getY());
            entityPacket.getDoubles().write(2, location.getZ());

            PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

            ASInfo asInfo = new ASInfo(id, text, entityPacket, metaPacket);

            if (ConfigManager.MainConfig.placeholderAPI) text = PapiHook.parsePlaceholders(player, text);

            metaPacket.getModifier().write(0, id);
            WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
            WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
            WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
            wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(text))).getHandle()));
            wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
            wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), (byte) 0x20);
            if (ConfigManager.Nameplate.smallSize) {
                wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, serializer2), (byte) 0x01);
            }
            metaPacket.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());

            asInfos.add(asInfo);
        }

        cache.put(player.getEntityId(), asInfos);
        player2ids.put(player, ids);
        id2ids.put(player.getEntityId(), ids);
    }

    public static void sendPreviewToOne(Player player) {
        List<ASInfo> asInfos = cache.get(player.getEntityId());
        if (asInfos != null) {
            PacketContainer ridingPacket = new PacketContainer(PacketType.Play.Server.MOUNT);
            ridingPacket.getModifier().write(0, player.getEntityId());
            ridingPacket.getModifier().write(1, player2ids.get(player));
            Location location = player.getLocation();
            for (ASInfo asInfo : asInfos) {
                PacketContainer entityPacket = asInfo.getEntityPacket();
                entityPacket.getDoubles().write(0, location.getX());
                entityPacket.getDoubles().write(1, location.getY() + 1.45);
                entityPacket.getDoubles().write(2, location.getZ());
                PacketContainer metaPacket = asInfo.getMetaPacket().deepClone();
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, entityPacket);
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, metaPacket);
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, ridingPacket);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                sendUpdateOneToOne(player, player);
                Bukkit.getScheduler().runTaskLaterAsynchronously(CustomNameplates.instance, () -> {
                    sendDestroyToOne(player, player2ids.get(player));
                }, ConfigManager.Nameplate.preview * 20);
            }
        }
    }

    public static void sendSummonOneToOne(Player player, Player otherPlayer) {
        List<ASInfo> asInfos = cache.get(otherPlayer.getEntityId());
        if (asInfos != null) {
            PacketContainer ridingPacket = new PacketContainer(PacketType.Play.Server.MOUNT);
            ridingPacket.getModifier().write(0, otherPlayer.getEntityId());
            ridingPacket.getModifier().write(1, player2ids.get(otherPlayer));
            Location location = otherPlayer.getLocation();

            boolean canSee = false;
            if (ConfigManager.Nameplate.show_after) {
                PlayerData playerData = DataManager.cache.get(player.getUniqueId());
                if (playerData == null) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(CustomNameplates.instance, () -> {
                        sendSummonOneToOne(player, otherPlayer);
                    }, 20);
                    return;
                } else if (playerData.getAccepted() == 1) {
                    canSee = true;
                }
            } else {
                canSee = true;
            }

            for (ASInfo asInfo : asInfos) {
                PacketContainer entityPacket = asInfo.getEntityPacket();
                entityPacket.getDoubles().write(0, location.getX());
                entityPacket.getDoubles().write(1, location.getY() + 1.45);
                entityPacket.getDoubles().write(2, location.getZ());
                PacketContainer metaPacket = asInfo.getMetaPacket().deepClone();
                if (!canSee) {
                    WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
                    WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
                    WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
                    //名称是否可见
                    wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), false);
                    wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), (byte) 0x20);
                    metaPacket.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
                }
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, entityPacket);
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, metaPacket);
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, ridingPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(CustomNameplates.instance, () -> {
                sendSummonOneToOne(player, otherPlayer);
            }, 20);
        }
    }

    //将左侧玩家的信息发送给右侧的
    public static void sendUpdateOneToOne(Player player, Player otherPlayer) {
        List<ASInfo> asInfos = cache.get(player.getEntityId());
        if (asInfos == null) return;
        for (ASInfo asInfo : asInfos) {
            PacketContainer metaPacket = asInfo.getMetaPacket().deepClone();
            String origin = asInfo.getText();
            if (ConfigManager.MainConfig.placeholderAPI) origin = PapiHook.parsePlaceholders(player, origin);
            WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
            WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
            wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), (byte) 0x20);
            wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(origin))).getHandle()));
            if (ConfigManager.Nameplate.smallSize) {
                wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, serializer2), (byte) 0x01);
            }
            metaPacket.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(otherPlayer, metaPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void forceUpdateOneToOne(int id, Player player) {
        List<ASInfo> asInfos = cache.get(id);
        if (asInfos == null) return;
        for (ASInfo asInfo : asInfos) {
            PacketContainer metaPacket = asInfo.getMetaPacket().deepClone();
            boolean canSee = false;
            if (ConfigManager.Nameplate.show_after) {
                PlayerData playerData = DataManager.cache.get(player.getUniqueId());
                if (playerData == null) {
                    return;
                } else if (playerData.getAccepted() == 1) {
                    canSee = true;
                }
            } else {
                canSee = true;
            }
            WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
            WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
            WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
            wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), canSee);
            wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), (byte) 0x20);
            if (ConfigManager.Nameplate.smallSize) {
                wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, serializer2), (byte) 0x01);
            }
            metaPacket.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, metaPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendDestroyToOne(Player player, int[] entities) {
        List<Integer> idList = new ArrayList<>();
        for (int id : entities) {
            idList.add(id);
        }
        try {
            PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntLists().write(0, idList);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, destroyPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}