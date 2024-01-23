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

package net.momirealms.customnameplates.paper.mechanic.bubble;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.customnameplates.api.data.OnlineUser;
import net.momirealms.customnameplates.api.event.BubblesSpawnEvent;
import net.momirealms.customnameplates.api.event.NameplateDataLoadEvent;
import net.momirealms.customnameplates.api.manager.BubbleManager;
import net.momirealms.customnameplates.api.mechanic.bubble.Bubble;
import net.momirealms.customnameplates.api.mechanic.character.CharacterArranger;
import net.momirealms.customnameplates.api.mechanic.character.ConfiguredChar;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.EntityTagPlayer;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.StaticTextEntity;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.StaticTextTagSetting;
import net.momirealms.customnameplates.api.util.FontUtils;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.CustomNameplatesPluginImpl;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.bubble.image.ImageParser;
import net.momirealms.customnameplates.paper.mechanic.bubble.image.ItemsAdderImageImpl;
import net.momirealms.customnameplates.paper.mechanic.bubble.image.OraxenImageImpl;
import net.momirealms.customnameplates.paper.mechanic.bubble.listener.AbstractChatListener;
import net.momirealms.customnameplates.paper.mechanic.bubble.listener.AsyncChatListener;
import net.momirealms.customnameplates.paper.mechanic.bubble.listener.TrChatListener;
import net.momirealms.customnameplates.paper.mechanic.bubble.listener.VentureChatListener;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BubbleManagerImpl implements BubbleManager {

    private AbstractChatListener chatListener;
    private ImageParser imageParser;
    private final HashMap<String, Bubble> bubbleMap;
    private final CustomNameplatesPluginImpl plugin;
    private String defaultBubble;
    private String prefix;
    private String suffix;
    private String startFormat;
    private String endFormat;
    private double lineSpace;
    private double yOffset;
    private int stayTime;
    private int coolDown;
    private int maxCharLength;
    private int lengthPerLine;
    private int subStringIndex;
    private String[] blacklistChannels;

    public BubbleManagerImpl(CustomNameplatesPluginImpl plugin) {
        this.plugin = plugin;
        this.bubbleMap = new HashMap<>();
    }

    public void reload() {
        unload();
        load();
    }

    public void load() {
        if (!CNConfig.bubbleModule) return;
        this.loadConfig();
        this.loadBubbles();
        this.registerListener();
        this.registerImageParser();
    }

    public void unload() {
        this.imageParser = null;
        this.bubbleMap.clear();
        if (chatListener != null) HandlerList.unregisterAll(chatListener);
    }

    private void loadConfig() {
        YamlConfiguration config = plugin.getConfig("configs"  + File.separator + "bubble.yml");
        defaultBubble = config.getString("default-bubbles", "chat");
        prefix = config.getString("text-prefix", "");
        suffix = config.getString("text-suffix", "");
        lineSpace = config.getDouble("line-spacing");
        yOffset = config.getDouble("bottom-line-Y-offset");
        stayTime = config.getInt("stay-time", 5);
        coolDown = (int) (config.getDouble("cool-down", 1) * 1000);
        maxCharLength = config.getInt("max-character-length", 100);
        blacklistChannels = config.getStringList("blacklist-channels").toArray(new String[0]);
        subStringIndex = config.getInt("sub-string-index", 0);
        lengthPerLine = config.getInt("characters-per-line", 30);
        startFormat = config.getString("default-format.start", "<gradient:#F5F5F5:#E1FFFF:#F5F5F5><u>");
        endFormat = config.getString("default-format.end", "<!u></gradient>");
    }

    private void registerImageParser() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("Oraxen")) {
            this.imageParser = new OraxenImageImpl();
        } else if (pluginManager.isPluginEnabled("ItemsAdder")) {
            this.imageParser = new ItemsAdderImageImpl();
        }
    }

    private void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (CNConfig.trChatChannel) {
            this.chatListener = new TrChatListener(this);
        } else if (CNConfig.ventureChatChannel) {
            this.chatListener = new VentureChatListener(this);
        } else {
            this.chatListener = new AsyncChatListener(this);
        }
        pluginManager.registerEvents(chatListener, plugin);
    }

    private void loadBubbles() {
        File bubbleFolder = new File(plugin.getDataFolder(), "contents" + File.separator + "bubbles");
        if (!bubbleFolder.exists() && bubbleFolder.mkdirs()) {
            saveDefaultBubbles();
        }
        File[] bbConfigFiles = bubbleFolder.listFiles(file -> file.getName().endsWith(".yml"));
        if (bbConfigFiles == null) return;
        Arrays.sort(bbConfigFiles, Comparator.comparing(File::getName));
        for (File bbConfigFile : bbConfigFiles) {

            String key = bbConfigFile.getName().substring(0, bbConfigFile.getName().length() - 4);
            if (key.equals("none")) {
                LogUtils.severe("You can't use 'none' as bubble's key");
                continue;
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(bbConfigFile);
            if (!registerBubble(
                    key,
                    Bubble.builder()
                            .displayName(config.getString("display-name", key))
                            .startFormat(config.getString("text-format.start", ""))
                            .endFormat(config.getString("text-format.end", ""))
                            .left(ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("left.image", key + "_left"))
                                    .height(config.getInt("left.height", 16))
                                    .ascent(config.getInt("left.ascent", 12))
                                    .width(config.getInt("left.width", 16))
                                    .build())
                            .right(ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("right.image", key + "_right"))
                                    .height(config.getInt("right.height", 16))
                                    .ascent(config.getInt("right.ascent", 12))
                                    .width(config.getInt("right.width", 16))
                                    .build())
                            .middle(ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("middle.image", key + "_middle"))
                                    .height(config.getInt("middle.height", 16))
                                    .ascent(config.getInt("middle.ascent", 12))
                                    .width(config.getInt("middle.width", 16))
                                    .build())
                            .tail(ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("tail.image", key + "_tail"))
                                    .height(config.getInt("tail.height", 16))
                                    .ascent(config.getInt("tail.ascent", 12))
                                    .width(config.getInt("tail.width", 16))
                                    .build())
                            .build())
            ) {
                LogUtils.warn("Found duplicated bubble: " + key);
            }
        }
    }

    @Override
    public boolean registerBubble(String key, Bubble bubble) {
        if (this.bubbleMap.containsKey(key)) return false;
        this.bubbleMap.put(key, bubble);
        return true;
    }

    @Override
    public boolean unregisterBubble(String key) {
        return this.bubbleMap.remove(key) != null;
    }

    public void onChat(Player player, String text) {
        if (        player.getGameMode() == GameMode.SPECTATOR
                || player.hasPotionEffect(PotionEffectType.INVISIBILITY)
                || !player.hasPermission("bubbles.use")
        ) return;

        var optionalUser = plugin.getStorageManager().getOnlineUser(player.getUniqueId());
        if (optionalUser.isEmpty()) {
            return;
        }

        String bubble = optionalUser.get().getBubbleKey();

        if (text.length() >= subStringIndex) {
            text = text.substring(subStringIndex);
        }

        text = imageParser != null ? imageParser.parse(player, text) : text;

        BubblesSpawnEvent bubblesEvent = new BubblesSpawnEvent(player, bubble, text);
        Bukkit.getPluginManager().callEvent(bubblesEvent);
        if (bubblesEvent.isCancelled()) {
            return;
        }

        bubble = bubblesEvent.getBubble();
        if (bubble.equals("none")) {
            bubble = defaultBubble;
        }

        Bubble bubbleConfig = getBubble(bubble);
        if (bubbleConfig == null) {
            if (!bubble.equals("none")) {
                LogUtils.warn("Bubble " + bubble + " doesn't exist");
                return;
            }
        }

        if (plugin.getCoolDownManager().isCoolDown(player.getUniqueId(), "bubble", coolDown)) {
            return;
        }

        text = AdventureManagerImpl.getInstance().legacyToMiniMessage(bubblesEvent.getText());
        String strippedRawText = MiniMessage.miniMessage().stripTags(text);
        if (strippedRawText.length() > maxCharLength) return;
        String[] split = splitString(strippedRawText, lengthPerLine);
        for (int i = 0; i < split.length; i++) {
            int finalIndex = i;
            String finalBubble = bubble;
            plugin.getScheduler().runTaskAsyncLater(
                    () -> sendBubble(player, split[finalIndex], bubbleConfig, finalBubble),
                    (long) i * 250 + 100,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    private void sendBubble(Player player, String text, Bubble bubbleConfig, String key) {
        if (key.equals("none")) {
            text = startFormat + PlaceholderAPI.setPlaceholders(player, prefix) + text + PlaceholderAPI.setPlaceholders(player, suffix) + endFormat;
        } else {
            text = bubbleConfig.getStartFormat() + PlaceholderAPI.setPlaceholders(player, prefix) + text + PlaceholderAPI.setPlaceholders(player, suffix) + bubbleConfig.getEndFormat();
            int width = FontUtils.getTextWidth(text);
            text = bubbleConfig.getPrefixWithFont(width) + text + bubbleConfig.getSuffixWithFont(width);
        }
        EntityTagPlayer tagPlayer = plugin.getNameplateManager().getUnlimitedTagManager().createOrGetTagForPlayer(player);
        if (tagPlayer == null)
            return;

        StaticTextEntity entity = tagPlayer.addTag(StaticTextTagSetting.builder()
                .leaveRule((p, e) -> true)
                .comeRule((p, e) -> true)
                .verticalOffset(yOffset)
                .defaultText(text)
                .plugin("bubble")
                .build());

        for (StaticTextEntity bubble : tagPlayer.getStaticTags()) {
            if (bubble.getPlugin().equals("bubble")) {
                bubble.setOffset(bubble.getOffset() + lineSpace);
            }
        }

        plugin.getScheduler().runTaskAsyncLater(() -> {
            tagPlayer.removeTag(entity);
        }, stayTime, TimeUnit.SECONDS);
    }

    private String[] splitString(String str, int len) {
        int size = (int) Math.ceil((double) str.length() / (double) len);
        String[] result = new String[size];
        int index = 0;
        for (int i = 0; i < str.length(); i += len) {
            if (i + len > str.length()) {
                result[index++] = str.substring(i);
            } else {
                result[index++] = str.substring(i, i + len);
            }
        }
        return result;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDataLoaded(NameplateDataLoadEvent event) {
        OnlineUser data = event.getOnlineUser();
        String bubble = data.getBubbleKey();
        if (!bubble.equals("none") && !containsBubble(bubble)) {
            if (bubble.equals(defaultBubble)) {
                LogUtils.severe("Default nameplate doesn't exist");
                return;
            }

            LogUtils.severe("Bubble " + bubble + " doesn't exist. To prevent bugs, player " + event.getUUID() + " 's bubble data is reset");
            data.setBubble("none");
            plugin.getStorageManager().saveOnlinePlayerData(event.getUUID());
        }
    }

    @Override
    public Collection<String> getBubbleKeys() {
        return bubbleMap.keySet();
    }

    @Nullable
    @Override
    public Bubble getBubble(String bubble) {
        return bubbleMap.get(bubble);
    }

    @Override
    public boolean hasBubble(Player player, String bubble) {
        return player.hasPermission("bubbles.equip." + bubble);
    }

    @Override
    public List<String> getAvailableBubbles(Player player) {
        List<String> bubbles = new ArrayList<>();
        for (String nameplate : bubbleMap.keySet()) {
            if (hasBubble(player, nameplate)) {
                bubbles.add(nameplate);
            }
        }
        return bubbles;
    }

    @Override
    public List<String> getAvailableBubblesDisplayNames(Player player) {
        List<String> bubbles = new ArrayList<>();
        for (Map.Entry<String, Bubble> entry : bubbleMap.entrySet()) {
            if (hasBubble(player, entry.getKey())) {
                bubbles.add(entry.getValue().getDisplayName());
            }
        }
        return bubbles;
    }

    @Override
    public String[] getBlacklistChannels() {
        return blacklistChannels;
    }

    @Override
    public Collection<Bubble> getBubbles() {
        return bubbleMap.values();
    }

    @Override
    public boolean containsBubble(String key) {
        return bubbleMap.containsKey(key);
    }

    @Override
    public boolean equipBubble(Player player, String bubbleKey) {
        Bubble bubble = getBubble(bubbleKey);
        if (bubble == null && bubbleKey.equals("none")) {
            return false;
        }
        plugin.getStorageManager().getOnlineUser(player.getUniqueId()).ifPresentOrElse(it -> {
            if (it.getBubbleKey().equals(bubbleKey)) {
                return;
            }
            it.setBubble(bubbleKey);
            plugin.getStorageManager().saveOnlinePlayerData(player.getUniqueId());
        }, () -> {
            LogUtils.severe("Player " + player.getName() + "'s data is not loaded.");
        });
        return true;
    }

    @Override
    public void unEquipBubble(Player player) {
        plugin.getStorageManager().getOnlineUser(player.getUniqueId()).ifPresentOrElse(it -> {
            if (it.getBubbleKey().equals("none")) {
                return;
            }
            it.setNameplate("none");
            plugin.getStorageManager().saveOnlinePlayerData(player.getUniqueId());
        }, () -> {
            LogUtils.severe("Player " + player.getName() + "'s data is not loaded.");
        });
    }

    @Override
    public String getDefaultBubble() {
        return defaultBubble;
    }

    private void saveDefaultBubbles() {
        String[] png_list = new String[]{"chat"};
        String[] part_list = new String[]{"_left.png", "_middle.png", "_right.png", "_tail.png", ".yml"};
        for (String name : png_list) {
            for (String part : part_list) {
                plugin.saveResource("contents" + File.separator + "bubbles" + File.separatorChar + name + part, false);
            }
        }
    }
}
