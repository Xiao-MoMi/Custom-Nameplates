package net.momirealms.customnameplates.nameplates.mode.bubbles;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.data.PlayerData;
import net.momirealms.customnameplates.nameplates.ArmorStandManager;
import net.momirealms.customnameplates.nameplates.BubbleConfig;
import net.momirealms.customnameplates.nameplates.FakeArmorStand;
import net.momirealms.customnameplates.nameplates.NameplateUtil;
import net.momirealms.customnameplates.nameplates.mode.EntityTag;
import net.momirealms.customnameplates.resource.ResourceManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.UUID;

public class ChatBubblesManager extends EntityTag {

    private BBPacketsHandle packetsHandle;

    private ChatListener chatListener;
    private TrChatListener trChatListener;

    private final HashMap<Player, Long> coolDown = new HashMap<>();

    public ChatBubblesManager(String name) {
        super(name);
    }

    @Override
    public void load() {

        this.packetsHandle = new BBPacketsHandle("BUBBLES", this);
        this.packetsHandle.load();

        if (ConfigManager.Main.trChat) {
            this.trChatListener = new TrChatListener(this);
            Bukkit.getPluginManager().registerEvents(trChatListener, CustomNameplates.instance);
        }
        else {
            this.chatListener = new ChatListener(this);
            Bukkit.getPluginManager().registerEvents(chatListener, CustomNameplates.instance);
        }
        for (Player all : Bukkit.getOnlinePlayers()) {
            armorStandManagerMap.put(all, new ArmorStandManager(all));
            for (Player player : Bukkit.getOnlinePlayers())
                spawnArmorStands(player, all);
        }
    }

    @Override
    public void unload() {
        this.packetsHandle.unload();
        if (chatListener != null) HandlerList.unregisterAll(chatListener);
        if (trChatListener != null) HandlerList.unregisterAll(trChatListener);
        super.unload();
    }

    private void spawnArmorStands(Player viewer, Player target) {
        if (target == viewer) return;
        if (viewer.getWorld() != target.getWorld()) return;
        if (getDistance(target, viewer) < 48 && viewer.canSee(target))
            getArmorStandManager(target).spawn(viewer);
    }

    private double getDistance(Player player1, Player player2) {
        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();
        return Math.sqrt(Math.pow(loc1.getX()-loc2.getX(), 2) + Math.pow(loc1.getZ()-loc2.getZ(), 2));
    }

    public ArmorStandManager getArmorStandManager(Player player) {
        return armorStandManagerMap.get(player);
    }

    @Override
    public void onJoin(Player player) {
        armorStandManagerMap.put(player, new ArmorStandManager(player));
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            spawnArmorStands(viewer, player);
            spawnArmorStands(player, viewer);
        }
    }

    @Override
    public void onQuit(Player player) {
        ArmorStandManager asm = armorStandManagerMap.remove(player);
        if (asm != null) {
            asm.destroy();
        }
        coolDown.remove(player);
    }

    public void onChat(Player player, String text) {

        long time = System.currentTimeMillis();
        if (time - (coolDown.getOrDefault(player, time - ConfigManager.Bubbles.coolDown)) < ConfigManager.Bubbles.coolDown) return;
        coolDown.put(player, time);

        PlayerData playerData = CustomNameplates.instance.getDataManager().getOrEmpty(player);
        String bubbles = playerData.getBubbles();
        BubbleConfig bubbleConfig = ResourceManager.BUBBLES.get(bubbles);
        WrappedChatComponent wrappedChatComponent;
        if (CustomNameplates.instance.getImageParser() != null) {
            text = CustomNameplates.instance.getImageParser().parse(player, text);
        }
        if (bubbleConfig == null || bubbles.equals("none")) {
            text = ConfigManager.Main.placeholderAPI ?
                    CustomNameplates.instance.getPlaceholderManager().parsePlaceholders(player, ConfigManager.Bubbles.prefix) + ConfigManager.Bubbles.defaultFormat + text + CustomNameplates.instance.getPlaceholderManager().parsePlaceholders(player, ConfigManager.Bubbles.suffix)
                    :
                    ConfigManager.Bubbles.prefix + text + ConfigManager.Bubbles.suffix;
            if (text.length() > ConfigManager.Bubbles.maxChar) return;
            wrappedChatComponent = WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(text)));
        }
        else {
            text = ConfigManager.Main.placeholderAPI ?
                    CustomNameplates.instance.getPlaceholderManager().parsePlaceholders(player, ConfigManager.Bubbles.prefix) + bubbleConfig.format() + text + CustomNameplates.instance.getPlaceholderManager().parsePlaceholders(player, ConfigManager.Bubbles.suffix)
                    :
                    ConfigManager.Bubbles.prefix + text + ConfigManager.Bubbles.suffix;
            String stripped = MiniMessage.miniMessage().stripTags(text);
            if (stripped.length() > ConfigManager.Bubbles.maxChar) return;
            String bubble = NameplateUtil.makeCustomBubble("", stripped, "", bubbleConfig);
            String suffix = NameplateUtil.getSuffixChar(stripped);
            Component armorStand_Name = Component.text(bubble).font(ConfigManager.Main.key)
                                        .append(MiniMessage.miniMessage().deserialize(text).font(Key.key("minecraft:default")))
                                        .append(Component.text(suffix).font(ConfigManager.Main.key));
            wrappedChatComponent = WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(armorStand_Name));
        }
        ArmorStandManager asm = getArmorStandManager(player);
        asm.ascent();
        String name = UUID.randomUUID().toString();
        FakeArmorStand fakeArmorStand = new FakeArmorStand(asm, player, wrappedChatComponent);
        asm.addArmorStand(name, fakeArmorStand);
        asm.countdown(name, fakeArmorStand);
    }
}
