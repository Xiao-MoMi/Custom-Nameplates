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

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.api.events.BubblesEvent;
import net.momirealms.customnameplates.listener.*;
import net.momirealms.customnameplates.object.DisplayMode;
import net.momirealms.customnameplates.object.Function;
import net.momirealms.customnameplates.object.SimpleChar;
import net.momirealms.customnameplates.object.bubble.BubbleConfig;
import net.momirealms.customnameplates.object.carrier.NamedEntityCarrier;
import net.momirealms.customnameplates.object.carrier.NamedEntityImpl;
import net.momirealms.customnameplates.object.carrier.NamedEntityManager;
import net.momirealms.customnameplates.object.carrier.TextDisplayMeta;
import net.momirealms.customnameplates.object.font.OffsetFont;
import net.momirealms.customnameplates.object.img.ImageParser;
import net.momirealms.customnameplates.object.img.ItemsAdderImageImpl;
import net.momirealms.customnameplates.object.img.OraxenImageImpl;
import net.momirealms.customnameplates.utils.AdventureUtils;
import net.momirealms.customnameplates.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatBubblesManager extends Function {

    private AbstractChatListener chatListener;
    private ImageParser imageParser;
    private final ConcurrentHashMap<Player, Long> coolDownMap;
    private final HashMap<String, BubbleConfig> bubbleConfigMap;
    private final CustomNameplates plugin;
    private NamedEntityCarrier namedEntityCarrier;
    private final JoinQuitListener joinQuitListener;
    private String defaultBubble;
    private String defaultStartFormat;
    private String defaultEndFormat;
    private String prefix;
    private String suffix;
    private double lineSpace;
    private double yOffset;
    private int stayTime;
    private int coolDown;
    private int maxCharLength;
    private int lengthPerLine;
    private String[] channels;
    private TextDisplayMeta textDisplayMeta;

    public ChatBubblesManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.bubbleConfigMap = new HashMap<>();
        this.coolDownMap = new ConcurrentHashMap<>();
        this.joinQuitListener = new JoinQuitListener(this);
    }

    @Override
    public void load() {
        if (!ConfigManager.enableBubbles) return;
        super.load();
        this.loadConfig();
        this.loadBubbles();
        this.registerListener();
        this.registerImageParser();
        this.namedEntityCarrier.load();
    }

    @Override
    public void unload() {
        super.unload();
        this.imageParser = null;
        this.bubbleConfigMap.clear();
        this.coolDownMap.clear();
        if (chatListener != null) HandlerList.unregisterAll(chatListener);
        if (joinQuitListener != null) HandlerList.unregisterAll(joinQuitListener);
        if (namedEntityCarrier != null) namedEntityCarrier.unload();
    }

    private void loadConfig() {
        YamlConfiguration config = ConfigUtils.getConfig("configs"  + File.separator + "bubble.yml");
        DisplayMode displayMode = DisplayMode.valueOf(config.getString("mode", "ARMOR_STAND").toUpperCase(Locale.ENGLISH));
        defaultBubble = config.getString("default-bubbles", "chat");
        prefix = config.getString("text-prefix", "");
        suffix = config.getString("text-suffix", "");
        lineSpace = config.getDouble("line-spacing");
        defaultStartFormat = config.getString("default-format.start", "<gradient:#F5F5F5:#E1FFFF:#F5F5F5><u>");
        defaultEndFormat = config.getString("default-format.end", "<!u></gradient>");
        yOffset = config.getDouble("bottom-line-Y-offset") + (displayMode == DisplayMode.TEXT_DISPLAY ? 1.2 : 0);
        stayTime = config.getInt("stay-time", 5);
        coolDown = (int) (config.getDouble("cool-down", 1) * 1000);
        maxCharLength = config.getInt("max-character-length", 100);
        channels = config.getStringList("blacklist-channels").toArray(new String[0]);
        lengthPerLine = config.getInt("characters-per-line", 30);
        textDisplayMeta = ConfigUtils.getTextDisplayMeta(config.getConfigurationSection("text-display-options"));
        namedEntityCarrier = new NamedEntityCarrier(plugin, displayMode, new HashMap<>());
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
        if (ConfigManager.trChat_Hook) {
            this.chatListener = new TrChatListener(this);
        } else if (ConfigManager.ventureChat_Hook) {
            this.chatListener = new VentureChatListener(this);
        } else {
            this.chatListener = new AsyncChatListener(this);
        }
        pluginManager.registerEvents(chatListener, plugin);
        pluginManager.registerEvents(joinQuitListener, plugin);
    }

    private void loadBubbles() {
        File bb_file = new File(plugin.getDataFolder(), "contents" + File.separator + "bubbles");
        if (!bb_file.exists() && bb_file.mkdirs()) {
            saveDefaultBubbles();
        }
        File[] bb_config_files = bb_file.listFiles(file -> file.getName().endsWith(".yml"));
        if (bb_config_files == null) return;
        for (File bb_config_file : bb_config_files) {
            char left = ConfigManager.start_char;
            char middle;
            char right;
            char tail;
            ConfigManager.start_char = (char) ((tail = (char)((right = (char)((middle = (char)(ConfigManager.start_char + '\u0001')) + '\u0001')) + '\u0001')) + '\u0001');
            String key = bb_config_file.getName().substring(0, bb_config_file.getName().length() - 4);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(bb_config_file);
            if (!config.contains("display-name")) config.set("display-name", key);
            if (!config.contains("text-format.start")) config.set("text-format.start", "<white>");
            if (!config.contains("text-format.end")) config.set("text-format.end", "");
            if (!config.contains("left.image")) config.set("left.image", key + "_left");
            if (!config.contains("left.height")) config.set("left.height", 16);
            if (!config.contains("left.width")) config.set("left.width", 16);
            if (!config.contains("left.ascent")) config.set("left.ascent", 12);
            if (!config.contains("middle.image")) config.set("middle.image", key + "_middle");
            if (!config.contains("middle.height")) config.set("middle.height", 16);
            if (!config.contains("middle.width")) config.set("middle.width", 16);
            if (!config.contains("middle.ascent")) config.set("middle.ascent", 12);
            if (!config.contains("right.image")) config.set("right.image", key + "_right");
            if (!config.contains("right.height")) config.set("right.height", 16);
            if (!config.contains("right.width")) config.set("right.width", 16);
            if (!config.contains("right.ascent")) config.set("right.ascent", 12);
            if (!config.contains("tail.image")) config.set("tail.image", key + "_tail");
            if (!config.contains("tail.height")) config.set("tail.height", 16);
            if (!config.contains("tail.width")) config.set("tail.width", 16);
            if (!config.contains("tail.ascent")) config.set("tail.ascent", 12);
            try {
                config.save(bb_config_file);
            }
            catch (IOException ignored) {
            }
            SimpleChar leftChar = new SimpleChar(config.getInt("left.height"), config.getInt("left.ascent"), config.getInt("left.width"), left, config.getString("left.image") + ".png");
            SimpleChar middleChar = new SimpleChar(config.getInt("middle.height"), config.getInt("middle.ascent"), config.getInt("middle.width"), middle, config.getString("middle.image") + ".png");
            SimpleChar rightChar = new SimpleChar(config.getInt("right.height"), config.getInt("right.ascent"), config.getInt("right.width"), right, config.getString("right.image") + ".png");
            SimpleChar tailChar = new SimpleChar(config.getInt("tail.height"), config.getInt("tail.ascent"), config.getInt("tail.width"), tail, config.getString("tail.image") + ".png");
            bubbleConfigMap.put(key,
                    new BubbleConfig(
                            config.getString("text-format.start"), config.getString("text-format.end"),
                            config.getString("display-name"),
                            leftChar, middleChar,
                            rightChar, tailChar
                    )
            );
        }
    }

    @Override
    public void onQuit(Player player) {
        coolDownMap.remove(player);
    }

    private boolean isCoolDown(Player player, int lines) {
        long time = System.currentTimeMillis();
        if (time - (coolDownMap.getOrDefault(player, time - (long) coolDown * lines)) < (long) coolDown * lines) return true;
        coolDownMap.put(player, time);
        return false;
    }

    public String getBubblePrefix(String text, BubbleConfig bubble) {
        int totalWidth = plugin.getFontManager().getTotalWidth(text);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(plugin.getFontManager().getShortestNegChars(totalWidth % 2 == 0 ? totalWidth + bubble.left().getWidth() : totalWidth + bubble.left().getWidth() + 1))
                .append(bubble.left().getChars()).append(OffsetFont.NEG_1.getCharacter());
        int mid_amount;
        if (totalWidth - 1 <= bubble.tail().getWidth() ) {
            mid_amount = -1;
        } else {
            mid_amount = (totalWidth - 1 - bubble.tail().getWidth()) / (bubble.middle().getWidth());
        }
        if (mid_amount == -1) {
            stringBuilder.append(bubble.tail().getChars()).append(OffsetFont.NEG_1.getCharacter());
        } else if (mid_amount == 0) {
            stringBuilder.append(bubble.tail().getChars()).append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(
                    plugin.getFontManager().getShortestNegChars(
                            bubble.middle().getWidth() - (totalWidth - 1 - bubble.tail().getWidth()) % bubble.middle().getWidth()
                    )
            );
            stringBuilder.append(bubble.middle().getChars()).append(OffsetFont.NEG_1.getCharacter());
        } else {
            stringBuilder.append(bubble.middle().getChars()).append(OffsetFont.NEG_1.getCharacter());
            for (int i = 0; i < mid_amount; i++) {
                if (i == mid_amount / 2) stringBuilder.append(bubble.tail().getChars()).append(OffsetFont.NEG_1.getCharacter());
                else stringBuilder.append(bubble.middle().getChars()).append(OffsetFont.NEG_1.getCharacter());
            }
            stringBuilder.append(
                    plugin.getFontManager().getShortestNegChars(
                            bubble.middle().getWidth() - (totalWidth - 1 - bubble.tail().getWidth()) % bubble.middle().getWidth()
                    )
            );
            stringBuilder.append(bubble.middle().getChars()).append(OffsetFont.NEG_1.getCharacter());
        }
        stringBuilder.append(bubble.right().getChars());
        stringBuilder.append(plugin.getFontManager().getShortestNegChars(totalWidth + bubble.right().getWidth()));
        return stringBuilder.toString();
    }

    public void onChat(Player player, String text) {
        if (player.getGameMode() == GameMode.SPECTATOR || !player.hasPermission("bubbles.use")) return;

        String bubble = plugin.getDataManager().getEquippedBubble(player);
        BubblesEvent bubblesEvent = new BubblesEvent(player, bubble, text);
        Bukkit.getPluginManager().callEvent(bubblesEvent);
        if (bubblesEvent.isCancelled()) {
            return;
        }

        bubble = bubblesEvent.getBubble();
        BubbleConfig bubbleConfig = getBubble(bubble);
        String miniText = AdventureUtils.replaceLegacy(bubblesEvent.getText());
        String stripped_text = MiniMessage.miniMessage().stripTags(miniText);
        if (stripped_text.length() > maxCharLength) return;
        String[] split = splitString(stripped_text, lengthPerLine);
        if (isCoolDown(player, split.length)) return;
        for (int i = 0; i < split.length; i++) {
            int finalI = i;
            String finalBubble = bubble;
            plugin.getScheduler().runTaskAsyncLater(() -> sendBubble(player, split[finalI], bubbleConfig, finalBubble), (long) i * (coolDown / 50) + 1);
        }
    }

    private void sendBubble(Player player, String text, BubbleConfig bubbleConfig, String key) {
        String json;
        if (bubbleConfig == null || key.equals("none")) {
            text = defaultStartFormat + PlaceholderAPI.setPlaceholders(player, prefix) + text + PlaceholderAPI.setPlaceholders(player, suffix) + defaultEndFormat;
            json = GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(imageParser != null ? imageParser.parse(player, text) : text));
        } else {
            String parsedPrefix = PlaceholderAPI.setPlaceholders(player, prefix);
            String parsedSuffix = PlaceholderAPI.setPlaceholders(player, suffix);
            String strippedPrefix = AdventureUtils.stripAllTags(parsedPrefix);
            String strippedSuffix = AdventureUtils.stripAllTags(parsedSuffix);
            String all = strippedPrefix + text + strippedSuffix;
            String bubbleImage = getBubblePrefix(all, bubbleConfig);
            String suffixImage = plugin.getFontManager().getSuffixStringWithFont(all);
            String finalStr = ConfigManager.surroundWithFont(bubbleImage) + bubbleConfig.startFormat() + parsedPrefix + text + parsedSuffix + bubbleConfig.endFormat() + ConfigManager.surroundWithFont(suffixImage);
            json = GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(finalStr));
        }
        NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);

        if (asm != null) {
            double offset = yOffset;
            DisplayMode nameplateMode = plugin.getNameplateManager().getMode();
            if (nameplateMode == DisplayMode.ARMOR_STAND || nameplateMode == DisplayMode.TEXT_DISPLAY) {
                NamedEntityCarrier carrier = (NamedEntityCarrier) plugin.getNameplateManager().getTextCarrier();
                NamedEntityManager nem = carrier.getNamedEntityManager(player);
                if (nem != null) {
                    offset += nem.getHighestTextHeight();
                }
            }
            UUID uuid = UUID.randomUUID();
            asm.ascent(lineSpace);
            asm.addNamedEntity(uuid, new NamedEntityImpl(asm, player, json, offset, textDisplayMeta));
            plugin.getScheduler().runTaskAsyncLater(() -> asm.removeArmorStand(uuid), stayTime * 20L);
        }
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

    @Nullable
    public BubbleConfig getBubble(String bubble) {
        return bubbleConfigMap.get(bubble);
    }

    public ArrayList<String> getAvailableBubbles(Player player) {
        ArrayList<String> availableBubbles = new ArrayList<>();
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String permission = info.getPermission().toLowerCase();
            if (permission.startsWith("bubbles.equip.")) {
                permission = permission.substring(14);
                if (bubbleConfigMap.get(permission) != null) {
                    availableBubbles.add(permission);
                }
            }
        }
        return availableBubbles;
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

    public String getDefaultBubble() {
        return defaultBubble;
    }

    public String[] getChannels() {
        return channels;
    }

    public HashMap<String, BubbleConfig> getBubbleConfigMap() {
        return bubbleConfigMap;
    }

    public boolean existBubble(String bubble) {
        return bubbleConfigMap.containsKey(bubble);
    }

    public BubbleConfig getBubbleConfig(String bubble) {
        return bubbleConfigMap.get(bubble);
    }
}
