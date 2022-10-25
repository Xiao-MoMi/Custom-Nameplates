/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.manager;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.api.events.BubblesEvent;
import net.momirealms.customnameplates.hook.IAImageHook;
import net.momirealms.customnameplates.hook.ImageParser;
import net.momirealms.customnameplates.hook.OXImageHook;
import net.momirealms.customnameplates.listener.ChatListener;
import net.momirealms.customnameplates.listener.TrChatListener;
import net.momirealms.customnameplates.objects.nameplates.ArmorStandManager;
import net.momirealms.customnameplates.objects.nameplates.BubbleConfig;
import net.momirealms.customnameplates.objects.nameplates.FakeArmorStand;
import net.momirealms.customnameplates.objects.nameplates.mode.EntityTag;
import net.momirealms.customnameplates.objects.nameplates.mode.bubbles.BBPacketsHandle;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.UUID;

public class ChatBubblesManager extends EntityTag {

    private final BBPacketsHandle packetsHandle;
    private ChatListener chatListener;
    private TrChatListener trChatListener;
    private ImageParser imageParser;
    public static String defaultBubble;
    public static String defaultFormat;
    public static String prefix;
    public static String suffix;
    public static double lineSpace;
    public static double yOffset;
    public static int stayTime;
    public static int coolDown;
    public static int maxChar;
    public static String[] channels;

    private final HashMap<Player, Long> coolDownCache = new HashMap<>();

    public ChatBubblesManager() {
        super(null);
        this.packetsHandle = new BBPacketsHandle(this);
    }

    @Override
    public void load() {
        ConfigUtil.update("bubble.yml");
        YamlConfiguration config = ConfigUtil.getConfig("bubble.yml");
        defaultBubble = config.getString("bubble.default-bubbles", "none");

        if (!ConfigUtil.isModuleEnabled("bubbles")) return;

        prefix = config.getString("bubble.text-prefix", "");
        suffix = config.getString("bubble.text-suffix", "");
        lineSpace = config.getDouble("bubble.line-spacing");
        defaultFormat = config.getString("bubble.default-format", "<white><underlined>");
        yOffset = config.getDouble("bubble.bottom-line-Y-offset");
        stayTime = config.getInt("bubble.stay-time", 5);
        coolDown = config.getInt("bubble.cool-down", 1) * 1000;
        maxChar = config.getInt("bubble.max-char-length", 35);
        channels = config.getStringList("blacklist-channels").toArray(new String[0]);

        this.packetsHandle.load();

        if (ConfigManager.trChat_Hook) {
            this.trChatListener = new TrChatListener(this);
            Bukkit.getPluginManager().registerEvents(trChatListener, CustomNameplates.plugin);
        }
        else {
            this.chatListener = new ChatListener(this);
            Bukkit.getPluginManager().registerEvents(chatListener, CustomNameplates.plugin);
        }

        if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            this.imageParser = new OXImageHook();
        }
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            this.imageParser = new IAImageHook();
        }

        for (Player all : Bukkit.getOnlinePlayers()) {
            armorStandManagerMap.put(all, new ArmorStandManager(all));
            for (Player player : Bukkit.getOnlinePlayers())
                spawnArmorStands(player, all, false);
        }
    }

    @Override
    public void unload() {
        super.unload();
        this.packetsHandle.unload();
        this.imageParser = null;
        if (chatListener != null) {
            HandlerList.unregisterAll(chatListener);
            chatListener = null;
        }
        if (trChatListener != null) {
            HandlerList.unregisterAll(trChatListener);
            trChatListener = null;
        }
    }

    public ArmorStandManager getArmorStandManager(Player player) {
        return armorStandManagerMap.get(player);
    }

    @Override
    public void onJoin(Player player) {
        super.onJoin(player);
    }

    @Override
    public void addDefaultText(ArmorStandManager asm) {
        //empty
    }

    @Override
    public void onQuit(Player player) {
        super.onQuit(player);
        coolDownCache.remove(player);
    }

    public void onChat(Player player, String text) {

        if (player.getGameMode() == GameMode.SPECTATOR || !player.hasPermission("bubbles.use")) return;

        long time = System.currentTimeMillis();
        if (time - (coolDownCache.getOrDefault(player, time - coolDown)) < coolDown) return;
        coolDownCache.put(player, time);

        String bubbles = CustomNameplates.plugin.getDataManager().getPlayerData(player).getBubbles();
        if (imageParser != null) text = imageParser.parse(player, text);

        BubblesEvent bubblesEvent = new BubblesEvent(player, bubbles, text);
        Bukkit.getPluginManager().callEvent(bubblesEvent);
        if (bubblesEvent.isCancelled()) {
            return;
        }

        BubbleConfig bubbleConfig = ResourceManager.BUBBLES.get(bubblesEvent.getBubble());
        WrappedChatComponent wrappedChatComponent;

        if (bubbleConfig == null || bubblesEvent.getBubble().equals("none")) {
            text = MiniMessage.miniMessage().stripTags(text);
            text = CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(player, ChatBubblesManager.prefix) + ChatBubblesManager.defaultFormat + text + CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(player, ChatBubblesManager.suffix);
            if (text.length() > ChatBubblesManager.maxChar) return;
            wrappedChatComponent = WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(text)));
        }
        else {
            text = CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(player, ChatBubblesManager.prefix) + bubbleConfig.format() + text + CustomNameplates.plugin.getPlaceholderManager().parsePlaceholders(player, ChatBubblesManager.suffix);
            String stripped = MiniMessage.miniMessage().stripTags(text);
            if (stripped.length() > ChatBubblesManager.maxChar) return;

            String bubble = CustomNameplates.plugin.getNameplateManager().makeCustomBubble("", stripped, "", bubbleConfig);
            String suffix = CustomNameplates.plugin.getNameplateManager().getSuffixChar(stripped);
            Component armorStand_Name = Component.text(bubble).font(ConfigManager.key)
                                        .append(MiniMessage.miniMessage().deserialize(text).font(Key.key("minecraft:default")))
                                        .append(Component.text(suffix).font(ConfigManager.key));
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
