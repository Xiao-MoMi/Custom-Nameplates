package net.momirealms.customnameplates.manager;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.Function;
import net.momirealms.customnameplates.object.SimpleChar;
import net.momirealms.customnameplates.object.font.ASCIIWidth;
import net.momirealms.customnameplates.object.font.OffsetFont;
import net.momirealms.customnameplates.object.font.UnicodeWidth;
import net.momirealms.customnameplates.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class FontManager extends Function {

    private final CustomNameplates plugin;
    private final HashMap<Character, Integer> customImageWidth;

    public FontManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.customImageWidth = new HashMap<>();
    }

    @Override
    public void load() {
        this.loadASCIIWidth();
        this.loadCustomWidth();
    }

    @Override
    public void unload() {
        customImageWidth.clear();
    }

    private void loadASCIIWidth() {
        if (ConfigManager.thin_font)
            for (int i = 0; i < UnicodeWidth.values().length; i++)
                customImageWidth.put(UnicodeWidth.values()[i].getCharacter(), UnicodeWidth.values()[i].getWidth());
        else
            for (int i = 0; i < ASCIIWidth.values().length; i++)
                customImageWidth.put(ASCIIWidth.values()[i].getCharacter(), ASCIIWidth.values()[i].getWidth());
    }

    private void loadCustomWidth() {
        for (SimpleChar simpleChar : plugin.getImageManager().getCharacterMap().values()) {
            customImageWidth.put(simpleChar.getChars(), simpleChar.getWidth());
        }
        YamlConfiguration config = ConfigUtils.getConfig("configs" + File.separator + "image-width.yml");
        for (String image : config.getKeys(false)) {
            customImageWidth.put(image.charAt(0), config.getInt(image, 8));
        }
    }

    public int getCharWidth(char c) {
        return Objects.requireNonNullElse(customImageWidth.get(c), 8);
    }

    public int getTotalWidth(String text) {
        int length = text.length();
        int n = 0;
        for (int i = 0; i < length; i++) {
            n += getCharWidth(text.charAt(i));
        }
        return n + length;
    }

    public String getOffset(int offset) {
        if (offset >= 0) {
            return getShortestPosChars(offset);
        }
        else {
            return getShortestNegChars(-offset);
        }
    }

    public String getShortestNegChars(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        while (n >= 128) {
            stringBuilder.append(OffsetFont.NEG_128.getCharacter());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(OffsetFont.NEG_64.getCharacter());
            n -= 64;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(OffsetFont.NEG_32.getCharacter());
            n -= 32;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(OffsetFont.NEG_16.getCharacter());
            n -= 16;
        }
        if (n - 8 >= 0) {
            stringBuilder.append(OffsetFont.NEG_8.getCharacter());
            n -= 8;
        }
        if (n - 7 >= 0) {
            stringBuilder.append(OffsetFont.NEG_7.getCharacter());
            n -= 7;
        }
        if (n - 6 >= 0) {
            stringBuilder.append(OffsetFont.NEG_6.getCharacter());
            n -= 6;
        }
        if (n - 5 >= 0) {
            stringBuilder.append(OffsetFont.NEG_5.getCharacter());
            n -= 5;
        }
        if (n - 4 >= 0) {
            stringBuilder.append(OffsetFont.NEG_4.getCharacter());
            n -= 4;
        }
        if (n - 3 >= 0) {
            stringBuilder.append(OffsetFont.NEG_3.getCharacter());
            n -= 3;
        }
        if (n - 2 >= 0) {
            stringBuilder.append(OffsetFont.NEG_2.getCharacter());
            n -= 2;
        }
        if (n - 1 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
        }
        return stringBuilder.toString();
    }

    public String getShortestPosChars(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        while (n >= 128) {
            stringBuilder.append(OffsetFont.POS_128.getCharacter());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(OffsetFont.POS_64.getCharacter());
            n -= 64;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(OffsetFont.POS_32.getCharacter());
            n -= 32;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(OffsetFont.POS_16.getCharacter());
            n -= 16;
        }
        if (n - 8 >= 0) {
            stringBuilder.append(OffsetFont.POS_8.getCharacter());
            n -= 8;
        }
        if (n - 7 >= 0) {
            stringBuilder.append(OffsetFont.POS_7.getCharacter());
            n -= 7;
        }
        if (n - 6 >= 0) {
            stringBuilder.append(OffsetFont.POS_6.getCharacter());
            n -= 6;
        }
        if (n - 5 >= 0) {
            stringBuilder.append(OffsetFont.POS_5.getCharacter());
            n -= 5;
        }
        if (n - 4 >= 0) {
            stringBuilder.append(OffsetFont.POS_4.getCharacter());
            n -= 4;
        }
        if (n - 3 >= 0) {
            stringBuilder.append(OffsetFont.POS_3.getCharacter());
            n -= 3;
        }
        if (n - 2 >= 0) {
            stringBuilder.append(OffsetFont.POS_2.getCharacter());
            n -= 2;
        }
        if (n - 1 >= 0) {
            stringBuilder.append(OffsetFont.POS_1.getCharacter());
        }
        return stringBuilder.toString();
    }
}
