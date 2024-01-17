package net.momirealms.customnameplates.paper.mechanic.font;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.common.Key;
import net.momirealms.customnameplates.api.manager.WidthManager;
import net.momirealms.customnameplates.api.mechanic.font.FontData;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WidthManagerImpl implements WidthManager {

    private final CustomNameplatesPlugin plugin;
    private final HashMap<Key, FontData> fontDataMap;

    private char TAG_START = '<';
    private char TAG_END = '>';
    private char TAG_CLOSE = '/';
    private char TAG_ESCAPE = '\\';

    public WidthManagerImpl(CustomNameplatesPlugin plugin) {
        this.plugin = plugin;
        this.fontDataMap = new HashMap<>();
    }

    public void reload() {
        unload();
        load();
    }

    public void load() {
        YamlConfiguration config = plugin.getConfig("configs" + File.separator + "image-width.yml");
        for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
            if (entry.getValue() instanceof ConfigurationSection innerSection) {
                FontData fontData = new FontData(8);
                for (Map.Entry<String, Object> innerEntry : innerSection.getValues(false).entrySet()) {
                    String key = innerEntry.getKey();
                    if (key.contains("%") && !key.equals("%")) {
                        String stripped = AdventureManagerImpl.getInstance().stripTags(AdventureManagerImpl.getInstance().legacyToMiniMessage(PlaceholderAPI.setPlaceholders(null, key)));
                        if (stripped.length() != 1) {
                            LogUtils.warn(key + " is not a supported placeholder");
                            continue;
                        }
                        fontData.registerCharWidth(stripped.charAt(0), (Integer) entry.getValue());
                    } else if (key.length() == 1) {
                        fontData.registerCharWidth(key.charAt(0), (Integer) entry.getValue());
                    } else {
                        LogUtils.warn("Illegal image format: " + key);
                    }
                }
            }
        }
    }

    public void unload() {
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

    @Override
    public void registerImageWidth(Key key, char c, int width) {
        FontData fontData = fontDataMap.get(key);
        if (fontData == null) {
            FontData newData = new FontData(8);
            fontDataMap.put(key, newData);
            newData.registerCharWidth(c, width);
        } else {
            fontData.registerCharWidth(c, width);
        }
    }

    @Nullable
    @Override
    public FontData getFontData(Key key) {
        return fontDataMap.get(key);
    }

    @Override
    public int getTextWidth(String textWithTags) {
        return 0;
    }
}
