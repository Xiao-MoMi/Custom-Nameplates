package net.momirealms.customnameplates.api.feature.pack.width;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.pack.font.CharacterFontWidthData;
import net.momirealms.customnameplates.api.feature.pack.font.ConfigurableFontWidthData;
import net.momirealms.customnameplates.api.util.CharacterUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class WidthManagerImpl implements WidthManager {

    private final HashMap<String, BiConsumer<String, String>> templateConfigConsumersMap = new HashMap<>();

    private final CustomNameplates plugin;
    private final HashMap<String, CharacterFontWidthData> charFontWidthDataMap = new HashMap<>();
    private final HashMap<String, ConfigurableFontWidthData> configFontWidthDataMap = new HashMap<>();

    public WidthManagerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
        this.init();

        plugin.getConfigManager().saveResource("font" + File.separator + "ascii.png");
        plugin.getConfigManager().saveResource("font" + File.separator + "ascii_sga.png");
        plugin.getConfigManager().saveResource("font" + File.separator + "asciillager.png");
        plugin.getConfigManager().saveResource("font" + File.separator + "accented.png");
        plugin.getConfigManager().saveResource("font" + File.separator + "nonlatin_european.png");
        for (int a = 0; a < 256; a++) {
            plugin.getConfigManager().saveResource("font" + File.separator + "unicode_page_" + String.format("%02x", a) + ".png");
        }
    }

    private void init() {
        this.templateConfigConsumersMap.put("unifont", (id, path) -> {
            File unifontCache = new File(plugin.getDataDirectory().toFile(), "font" + File.separator + "cache" + File.separator + id + ".yml");
            if (!unifontCache.exists()) {
                File unihex = new File(plugin.getDataDirectory().toFile(), "font" + File.separator + path);
                if (path.equals("unifont.zip")) {
                    plugin.getConfigManager().saveResource("font" + File.separator + "unifont.zip");
                }
                if (!unihex.exists()) {
                    return;
                }
                try (ZipFile zipFile = new ZipFile(unihex)) {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (entry.getName().endsWith(".hex") && !entry.isDirectory()) {
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)))) {
                                if (!unifontCache.exists()) {
                                    try {
                                        unifontCache.createNewFile();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                YamlDocument yml = plugin.getConfigManager().loadData(unifontCache);
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    String[] parts = line.split(":");
                                    if (parts.length > 1) {
                                        int codePoint = Integer.parseInt(parts[0], 16);
                                        char[] surrogatePair = Character.toChars(codePoint);
                                        StringBuilder s = new StringBuilder();
                                        for (char c : surrogatePair) {
                                            s.append(CharacterUtils.native2ascii(c));
                                        }
                                        String unicode = s.toString();

                                        // width in pixels
                                        String hexData = parts[1];
                                        int width = hexData.length() / 4;
                                        int high = 4;
                                        int low = 4;
                                        int splitInterval = width / 4;

                                        int x;
                                        int n;
                                        outer:
                                        {
                                            for (x = 0; x < splitInterval; x++) {
                                                inner:
                                                for (int y = 0; y < 16; y++) {
                                                    int pos = y * splitInterval + x;
                                                    char selectedHex = hexData.charAt(pos);
                                                    int decimal = Character.digit(selectedHex, 16);
                                                    for (int k = 0; k < low; k++) {
                                                        if ((decimal & (1 << (3 - k))) != 0) {
                                                            low = k;
                                                            if (low == 0) {
                                                                break outer;
                                                            } else {
                                                                continue inner;
                                                            }
                                                        }
                                                    }
                                                }
                                                if (low != 4) {
                                                    break outer;
                                                }
                                            }
                                            yml.set(unicode, -1);
                                            continue;
                                        }

                                        outer:
                                        for (n = splitInterval - 1; n >= x; n--) {
                                            inner:
                                            for (int y = 0; y < 16; y++) {
                                                int pos = y * splitInterval + n;
                                                char selectedHex = hexData.charAt(pos);
                                                int decimal = Character.digit(selectedHex, 16);
                                                for (int k = 0; k < high; k++) {
                                                    if ((decimal & (1 << k)) != 0) {
                                                        high = k;
                                                        if (high == 0) {
                                                            break outer;
                                                        } else {
                                                            continue inner;
                                                        }
                                                    }
                                                }
                                            }
                                            if (high != 4) {
                                                break;
                                            }
                                        }
                                        yml.set(unicode, ((n - x + 1) * 4 - high - low) / 2);
                                    }
                                }
                                for (int i = 0x3001; i <= 0x30FF; i++) {
                                    char c = (char) i;
                                    yml.set(CharacterUtils.native2ascii(c), 8);
                                }
                                for (int i = 0x3200; i <= 0x9FFF; i++) {
                                    char c = (char) i;
                                    yml.set(CharacterUtils.native2ascii(c), 8);
                                }
                                for (int i = 0xAC00; i <= 0xD7AF; i++) {
                                    char c = (char) i;
                                    yml.set(CharacterUtils.native2ascii(c), 7);
                                }
                                for (int i = 0xF900; i <= 0xFAFF; i++) {
                                    char c = (char) i;
                                    yml.set(CharacterUtils.native2ascii(c), 8);
                                }
                                for (int i = 0xFF01; i <= 0xFF5E; i++) {
                                    char c = (char) i;
                                    yml.set(CharacterUtils.native2ascii(c), 8);
                                }
                                yml.set("\\u0020", 3);
                                yml.save(unifontCache);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            YamlDocument data = plugin.getConfigManager().loadData(unifontCache);
            HashMap<Integer, Integer> dataMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : data.getStringRouteMappedValues(false).entrySet()) {
                char[] chars = CharacterUtils.unicodeToChars(entry.getKey());
                int codePoint = chars.length == 2 ? Character.toCodePoint(chars[0], chars[1]) : chars[0];
                dataMap.put(codePoint, (int) entry.getValue());
            }

            CharacterFontWidthData fontWidthData = CharacterFontWidthData.builder()
                    .id(id)
                    .width(dataMap)
                    .build();
            charFontWidthDataMap.put(id, fontWidthData);
        });

        this.templateConfigConsumersMap.put("ascii", (id, path) -> {
            File asciiCache = new File(plugin.getDataDirectory().toFile(), "font" + File.separator + "cache" + File.separator + id + ".yml");
            if (!asciiCache.exists()) {

            }
        });

        this.templateConfigConsumersMap.put("nonlatin-european", (id, path) -> {

        });

        this.templateConfigConsumersMap.put("accented", (id, path) -> {

        });



        this.templateConfigConsumersMap.put("ascii-sga", (id, path) -> {

        });

        this.templateConfigConsumersMap.put("asciillager", (id, path) -> {

        });

        this.templateConfigConsumersMap.put("unicode", (id, path) -> {

        });

        this.templateConfigConsumersMap.put("ttf", (id, path) -> {

        });
    }

    @Override
    public void load() {
        this.loadConfig();
    }

    @Override
    public void unload() {
        this.charFontWidthDataMap.clear();
        this.configFontWidthDataMap.clear();
    }

    private void loadConfig() {
        YamlDocument config = ConfigManager.getMainConfig();
        Section section = config.getSection("other-settings.font-templates");
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                if (entry.getValue() instanceof Section inner) {
                    String template = entry.getKey();
                    BiConsumer<String, String> consumer = templateConfigConsumersMap.get(template);
                    if (consumer != null) {
                        for (Map.Entry<String, Object> innerEntry : inner.getStringRouteMappedValues(false).entrySet()) {
                            if (innerEntry.getValue() instanceof String path) {
                                consumer.accept(innerEntry.getKey(), path);
                            }
                        }
                    } else {
                        plugin.getPluginLogger().warn("Unsupported template: " + template);
                    }
                }
            }
        }
    }
}
