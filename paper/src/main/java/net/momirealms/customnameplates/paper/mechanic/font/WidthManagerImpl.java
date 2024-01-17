package net.momirealms.customnameplates.paper.mechanic.font;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.WidthManager;

import java.util.HashMap;

public class WidthManagerImpl implements WidthManager {

    private final CustomNameplatesPlugin plugin;
    private final HashMap<String, FontData> fontDataMap;

    private char TAG_START = '<';
    private char TAG_END = '>';
    private char TAG_CLOSE = '/';
    private char TAG_ESCAPE = '\\';

    public WidthManagerImpl(CustomNameplatesPlugin plugin) {
        this.plugin = plugin;
        this.fontDataMap = new HashMap<>();
    }

    public void reload() {

    }

    public void load() {

    }

    public void unload() {
        fontDataMap.clear();
    }

    public int getTextWidth(String textWithTags) {
        return 0;
    }
}
