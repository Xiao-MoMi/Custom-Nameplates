package net.momirealms.customnameplates.paper.mechanic.font;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.internal.parser.node.ElementNode;
import net.kyori.adventure.text.minimessage.internal.parser.node.TagNode;
import net.kyori.adventure.text.minimessage.internal.parser.node.ValueNode;
import net.kyori.adventure.text.minimessage.tag.Inserting;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.common.Key;
import net.momirealms.customnameplates.api.common.Tuple;
import net.momirealms.customnameplates.api.manager.WidthManager;
import net.momirealms.customnameplates.api.mechanic.font.FontData;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class WidthManagerImpl implements WidthManager {

    private final CustomNameplatesPlugin plugin;
    private final HashMap<Key, FontData> fontDataMap;
    private CacheSystem cacheSystem;

    public WidthManagerImpl(CustomNameplatesPlugin plugin) {
        this.plugin = plugin;
        this.fontDataMap = new HashMap<>();
    }

    public void reload() {
        unload();
        load();
    }

    public void load() {
        YamlConfiguration config = plugin.getConfig("configs" + File.separator + "font-width-data.yml");
        for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
            if (entry.getValue() instanceof ConfigurationSection innerSection) {
                FontData fontData = new FontData(8);
                int defaultWidth = innerSection.getInt("default", 8);

                ConfigurationSection customSection = innerSection.getConfigurationSection("values");
                if (customSection != null)
                    for (Map.Entry<String, Object> innerEntry : customSection.getValues(false).entrySet()) {
                        String key = innerEntry.getKey();
                        if (key.contains("%") && !key.equals("%")) {
                            String stripped = AdventureManagerImpl.getInstance().stripTags(AdventureManagerImpl.getInstance().legacyToMiniMessage(PlaceholderAPI.setPlaceholders(null, key)));
                            if (stripped.length() != 1) {
                                LogUtils.warn(key + " is not a supported placeholder");
                                continue;
                            }
                            fontData.registerCharWidth(stripped.charAt(0), (Integer) innerEntry.getValue());
                        } else if (key.length() == 1) {
                            fontData.registerCharWidth(key.charAt(0), (Integer) innerEntry.getValue());
                        } else {
                            LogUtils.warn("Illegal image format: " + key);
                        }
                    }
            }
        }
        this.cacheSystem = new CacheSystem(CNConfig.cacheSize);
    }

    public void unload() {
        if (this.cacheSystem != null)
            this.cacheSystem.destroy();
        fontDataMap.clear();
    }

    @Override
    public boolean registerFontData(Key key, FontData fontData) {
        if (fontDataMap.containsKey(key)) {
            return false;
        }
        fontDataMap.put(key, fontData);
        return true;
    }

    @Override
    public boolean unregisterFontData(Key key) {
        return fontDataMap.remove(key) != null;
    }

    @Nullable
    @Override
    public FontData getFontData(Key key) {
        return fontDataMap.get(key);
    }

    @Override
    public int getTextWidth(String textWithTags) {
        return cacheSystem.getWidthFromCache(textWithTags);
    }

    public class CacheSystem {

        private final LoadingCache<String, Integer> textWidthCache;

        public CacheSystem(int size) {
            textWidthCache = CacheBuilder.newBuilder()
                    .maximumSize(size)
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .build(
                            new CacheLoader<>() {
                                @NotNull
                                @Override
                                public Integer load(@NotNull String text) {
                                    return fetchData(text);
                                }
                            });
        }

        private int fetchData(String text) {
            if (CNConfig.legacyColorSupport)
                text = AdventureManagerImpl.getInstance().legacyToMiniMessage(text);
            ElementNode node = (ElementNode) MiniMessage.miniMessage().deserializeToTree(text);
            ArrayList<Tuple<String, Key, Boolean>> list = new ArrayList<>();
            nodeToStringInfo(node, list, Key.of("minecraft", "default"), false);
            int totalLength = 0;
            for (Tuple<String, Key, Boolean> element : list) {
                FontData data = getFontData(element.getMid());
                if (data == null) {
                    LogUtils.warn("Unknown font: " + element.getMid() + " Please register it in font-width-data.yml");
                    continue;
                }
                for (char c : element.getLeft().toCharArray()) {
                    totalLength += data.getWidth(c);
                }
                totalLength += element.getRight() ? element.getLeft().length() * 2 : element.getLeft().length();
            }
            return totalLength;
        }

        public Component nodeToStringInfo(ElementNode node, List<Tuple<String, Key, Boolean>> list, Key font, boolean isBold) {
            if (node instanceof ValueNode valueNode) {
                String text = valueNode.value();
                if (!text.equals(""))
                    list.add(Tuple.of(text, font, isBold));
            } else if (node instanceof TagNode tagNode) {
                if (tagNode.tag() instanceof Inserting inserting) {
                    Component component = inserting.value();
                    if (component instanceof TextComponent textComponent) {
                        isBold = component.hasDecoration(TextDecoration.BOLD);
                        var key = component.font();
                        if (key != null) {
                            font = AdventureManagerImpl.getInstance().keyToKey(key);
                        }
                        String text = textComponent.content();
                        if (!text.equals(""))
                            list.add(Tuple.of(text, font, isBold));
                    }
                }
            }
            if (!node.unsafeChildren().isEmpty()) {
                for (ElementNode child : node.unsafeChildren()) {
                    this.nodeToStringInfo(child, list, font, isBold);
                }
            }
            return null;
        }

        public int getWidthFromCache(String text) {
            try {
                return textWidthCache.get(text);
            } catch (ExecutionException e) {
                e.printStackTrace();
                return 0;
            }
        }

        public void destroy() {
            textWidthCache.cleanUp();
        }
    }
}
