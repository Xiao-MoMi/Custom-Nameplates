package net.momirealms.customnameplates.nameplates.bubbles;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.Function;
import net.momirealms.customnameplates.data.PlayerData;
import net.momirealms.customnameplates.nameplates.ArmorStandManager;
import net.momirealms.customnameplates.nameplates.FakeArmorStand;
import net.momirealms.customnameplates.nameplates.NameplateInstance;
import net.momirealms.customnameplates.nameplates.NameplateUtil;
import net.momirealms.customnameplates.nameplates.bubbles.lis.*;
import net.momirealms.customnameplates.resource.ResourceManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BBPacketsHandle extends Function {

    protected Map<Integer, Player> entityIdMap = new ConcurrentHashMap<>();

    private final ChatBubblesManager chatBubblesManager;

    private ChatListener chatListener;

    private EntityDestroy entityDestroy;
    private EntityMove entityMove;
    private EntitySpawn entitySpawn;
    private EntityLook entityLook;
    private EntityTeleport entityTeleport;

    protected BBPacketsHandle(String name, ChatBubblesManager chatBubblesManager) {
        super(name);
        this.chatBubblesManager = chatBubblesManager;
    }

    @Override
    public void load() {
        super.load();

        this.chatListener = new ChatListener(this);
        Bukkit.getPluginManager().registerEvents(chatListener, CustomNameplates.instance);

        this.entityDestroy = new EntityDestroy(this);
        this.entityMove = new EntityMove(this);
        this.entitySpawn = new EntitySpawn(this);
        this.entityLook = new EntityLook(this);
        this.entityTeleport = new EntityTeleport(this);
        CustomNameplates.protocolManager.addPacketListener(entityDestroy);
        CustomNameplates.protocolManager.addPacketListener(entityMove);
        CustomNameplates.protocolManager.addPacketListener(entitySpawn);
        CustomNameplates.protocolManager.addPacketListener(entityLook);
        CustomNameplates.protocolManager.addPacketListener(entityTeleport);
    }

    @Override
    public void unload() {
        super.unload();
        HandlerList.unregisterAll(chatListener);
        CustomNameplates.protocolManager.removePacketListener(entityDestroy);
        CustomNameplates.protocolManager.removePacketListener(entitySpawn);
        CustomNameplates.protocolManager.removePacketListener(entityMove);
        CustomNameplates.protocolManager.removePacketListener(entityLook);
        CustomNameplates.protocolManager.removePacketListener(entityTeleport);
    }

    public void onEntityMove(Player receiver, int entityId) {
        Player mover = getPlayerFromMap(entityId);
        if (mover != null) {
            chatBubblesManager.getArmorStandManager(mover).teleport(receiver);
        }
    }

    public void onEntitySpawn(Player receiver, int entityId) {
        Player spawnedPlayer = getPlayerFromMap(entityId);
        if (spawnedPlayer != null) {
            chatBubblesManager.getArmorStandManager(spawnedPlayer).spawn(receiver);
        }
    }

    public void onEntityDestroy(Player receiver, List<Integer> entities) {
        for (int entity : entities) {
            onEntityDestroy(receiver, entity);
        }
    }

    public void onEntityDestroy(Player receiver, int entity) {
        Player deSpawnedPlayer = getPlayerFromMap(entity);
        if (deSpawnedPlayer != null) {
            chatBubblesManager.getArmorStandManager(deSpawnedPlayer).destroy(receiver);
        }
    }

    public Player getPlayerFromMap(int entityID) {
        return entityIdMap.get(entityID);
    }

    public void onChat(Player player, String text) {
        PlayerData playerData = CustomNameplates.instance.getDataManager().getOrEmpty(player);
        String nameplateName = playerData.getEquippedNameplate();
        NameplateInstance nameplateInstance = ResourceManager.NAMEPLATES.get(nameplateName);
        String nameplate = NameplateUtil.makeCustomNameplate("", text, "", nameplateInstance);
        String suffix = NameplateUtil.getSuffixChar(text);
        Component armorStand_Name = Component.text(nameplate).font(ConfigManager.Main.key).append(Component.text(text).font(Key.key("minecraft:default"))).append(Component.text(suffix).font(ConfigManager.Main.key));
        WrappedChatComponent wrappedChatComponent = WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(armorStand_Name));

        ArmorStandManager asm = chatBubblesManager.getArmorStandManager(player);
        asm.ascent();
        String name = UUID.randomUUID().toString();
        FakeArmorStand fakeArmorStand = new FakeArmorStand(asm, player, wrappedChatComponent);
        asm.addArmorStand(name, fakeArmorStand);
        asm.countdown(name, fakeArmorStand);
    }

    public void onJoin(Player player) {
        entityIdMap.put(player.getEntityId(), player);
        chatBubblesManager.onJoin(player);
    }

    public void onQuit(Player player) {
        entityIdMap.remove(player.getEntityId());
        chatBubblesManager.onQuit(player);
    }
}
