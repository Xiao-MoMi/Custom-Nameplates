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
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.helper.Log;
import net.momirealms.customnameplates.object.Function;
import net.momirealms.customnameplates.object.SimpleChar;
import net.momirealms.customnameplates.object.font.ASCIIWidth;
import net.momirealms.customnameplates.object.font.OffsetFont;
import net.momirealms.customnameplates.object.font.ThinASCIIWidth;
import net.momirealms.customnameplates.utils.AdventureUtils;
import net.momirealms.customnameplates.utils.ConfigUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Objects;

public class FontManager extends Function {

    private final CustomNameplates plugin;
    private final HashMap<Character, Integer> customImageWidth;
    private final HashMap<Character, Integer> asciiWidth;

    public FontManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.customImageWidth = new HashMap<>(1024);
        this.asciiWidth = new HashMap<>();
    }

    @Override
    public void load() {
        this.saveFonts();
        this.loadASCIIWidth();
        this.loadUnicodesWidth();
        this.loadCustomWidth();
    }

    @Override
    public void unload() {
        customImageWidth.clear();
        asciiWidth.clear();
    }

    private void loadASCIIWidth() {
        if (ConfigManager.thin_font)
            for (int i = 0; i < ThinASCIIWidth.values().length; i++)
                asciiWidth.put(ThinASCIIWidth.values()[i].getCharacter(), ThinASCIIWidth.values()[i].getWidth());
        else
            for (int i = 0; i < ASCIIWidth.values().length; i++)
                asciiWidth.put(ASCIIWidth.values()[i].getCharacter(), ASCIIWidth.values()[i].getWidth());
    }

    public void loadCustomWidth(char c, int width) {
        customImageWidth.put(c, width);
    }

    private void loadCustomWidth() {
        for (int i = 0; i < ASCIIWidth.values().length; i++)
            customImageWidth.put(ASCIIWidth.values()[i].getCharacter(), ASCIIWidth.values()[i].getWidth());
        for (SimpleChar simpleChar : plugin.getImageManager().getCharacterMap().values())
            customImageWidth.put(simpleChar.getChars(), simpleChar.getWidth());
        YamlConfiguration config = ConfigUtils.getConfig("configs" + File.separator + "image-width.yml");
        for (String image : config.getKeys(false)) {
            String character = AdventureUtils.stripAllTags(PlaceholderAPI.setPlaceholders(null, image));
            if (character.length() == 1) customImageWidth.put(character.charAt(0), config.getInt(image, 8));
        }
    }

    private void saveFonts() {
        File font_file = new File(plugin.getDataFolder(), "unicodes");
        if (!font_file.exists()) {
            for (int i = 0; i < 256; i++) {
                saveResource("unicodes" + File.separator + "unicode_page_" + String.format("%02x", i) + ".png");
            }
        }
        plugin.saveResource("templates" + File.separator + "default.json", true);
        plugin.saveResource("templates" + File.separator + "default1_20.json", true);
        plugin.saveResource("templates" + File.separator + "unicode.json", true);
    }

    public String getSuffixStringWithFont(String text) {
        return ConfigManager.surroundWithFont(getSuffixString(text));
    }

    public String getSuffixString(String text) {
        int totalWidth = plugin.getFontManager().getTotalWidth(text);
        return plugin.getFontManager().getShortestNegChars(totalWidth + totalWidth % 2 + 1);
    }

    // Player name
    public int getNameCharWidth(char c) {
        Integer width = asciiWidth.get(c);
        if (width != null) return width;
        return Objects.requireNonNullElse(customImageWidth.get(c), ConfigManager.default_width);
    }

    public int getTotalWidth(String text) {
        int length = text.length();
        int n = 0;
        for (int i = 0; i < length; i++) {
            char current = text.charAt(i);
            if (current != '\\' || i == length - 1 || text.charAt(i + 1) != '<') {
                n += Objects.requireNonNullElse(customImageWidth.get(current), ConfigManager.default_width);
            }
            else {
                n -= 1;
            }
        }
        return n + length;
    }

    public int getTotalPlayerNameWidth(String text) {
        int length = text.length();
        int n = 0;
        for (int i = 0; i < length; i++) {
            n += getNameCharWidth(text.charAt(i));
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

    private void loadUnicodesWidth() {
        File[] files = new File(CustomNameplates.getInstance().getDataFolder(), "unicodes").listFiles();
        if (files == null) return;
        for (File file : files) {
            if (!file.getName().endsWith(".png")) continue;
            String unicodeStr = file.getName().substring(file.getName().length() - 6, file.getName().length() - 4);
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();
                int single = width / 16;
                int max_y = height / single;
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < max_y; j++) {
                        int x_final = i * single;
                        outer:
                        for (int x = i * single; x < (i+1) * single; x++) {
                            for (int y = j * single; y < (j+1) * single; y++) {
                                int rgb = bufferedImage.getRGB(x, y);
                                int alpha = (rgb >> 24) & 0xff;
                                if (alpha != 0) {
                                    x_final = x;
                                    continue outer;
                                }
                            }
                        }
                        int single_real_width = (int) (((double) (x_final - i * single) / single * 8) + 1);
                        String unicode = "\\u" + unicodeStr + String.format("%02x", i + j * 16);
                        String unicodeFinalStr = StringEscapeUtils.unescapeJava(unicode);
                        customImageWidth.put(unicodeFinalStr.charAt(0), single_real_width);
                    }
                }
            }
            catch (IOException ignored) {
                Log.warn("Error occurred when reading png files");
            }
        }
    }

    @Nullable
    public InputStream getResource(@NotNull String filename) {
        try {
            URL url = plugin.getClass().getClassLoader().getResource(filename);
            if (url == null) {
                return null;
            }
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public void saveResource(@NotNull String resourcePath) {
        if (resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            return;
        }

        File outFile = new File(plugin.getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(plugin.getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists()) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ex) {
            Log.warn("Could not save " + outFile.getName() + " to " + outFile);
        }
    }
}
