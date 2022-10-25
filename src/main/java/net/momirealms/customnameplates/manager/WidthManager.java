package net.momirealms.customnameplates.manager;

import net.momirealms.customnameplates.objects.Function;
import net.momirealms.customnameplates.objects.font.FontOffset;
import net.momirealms.customnameplates.objects.font.FontWidthNormal;
import net.momirealms.customnameplates.objects.font.FontWidthThin;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class WidthManager extends Function {

    public static HashMap<Character, Integer> fontWidth = new HashMap<>();

    @Override
    public void load() {
        loadWidth();
    }

    @Override
    public void unload() {
        super.unload();
    }

    public void loadWidth() {
        fontWidth.clear();
        YamlConfiguration config = ConfigUtil.getConfig("char-width.yml");
        for (String key : config.getKeys(false)) {
            if (key.length() == 1) {
                fontWidth.put(key.charAt(0), config.getInt(key));
            }
            else {
                AdventureUtil.consoleMessage("<red>[CustomNameplates] " + key + " in custom char-width.yml is in wrong format or not supported");
            }
        }
        AdventureUtil.consoleMessage("[CustomNameplates] Loaded <green>" + fontWidth.size() + " <gray>custom char-width");
        if (ConfigManager.thin_font)
            for (int i = 0; i < FontWidthThin.values().length; i++)
                fontWidth.put(FontWidthThin.values()[i].getCharacter(), FontWidthThin.values()[i].getLength());
        else
            for (int i = 0; i < FontWidthNormal.values().length; i++)
                fontWidth.put(FontWidthNormal.values()[i].getCharacter(), FontWidthNormal.values()[i].getLength());
        for (int i = 0; i < FontOffset.values().length; i++)
            fontWidth.put(FontOffset.values()[i].getCharacter(), FontOffset.values()[i].getSpace() - 1);
    }
}
