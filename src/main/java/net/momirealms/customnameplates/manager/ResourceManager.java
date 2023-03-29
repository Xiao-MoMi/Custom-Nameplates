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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.SimpleChar;
import net.momirealms.customnameplates.object.background.BackGroundConfig;
import net.momirealms.customnameplates.object.bubble.BubbleConfig;
import net.momirealms.customnameplates.object.font.OffsetFont;
import net.momirealms.customnameplates.object.nameplate.NameplateConfig;
import net.momirealms.customnameplates.utils.AdventureUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResourceManager {

    private final CustomNameplates plugin;

    public ResourceManager(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    public void generateResourcePack() {
        File resourcePack_folder = new File(plugin.getDataFolder() + File.separator + "ResourcePack");
        this.deleteDirectory(resourcePack_folder);
        File font_folder = new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "assets" + File.separator + ConfigManager.namespace + File.separatorChar + "font");
        File textures_folder = new File(plugin.getDataFolder(), "ResourcePack" + File.separator+ "assets" + File.separator + ConfigManager.namespace + File.separatorChar + "textures");
        if (!font_folder.mkdirs() || !textures_folder.mkdirs()) {
            AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to generate resource pack folders...</red>");
            return;
        }
        JsonObject fontJsonObject = new JsonObject();
        JsonArray fontJsonArray = new JsonArray();
        fontJsonObject.add("providers", fontJsonArray);
        this.getOffsetFontEnums().forEach(fontJsonArray::add);
        this.loadNameplates(fontJsonArray, textures_folder);
        this.loadBubbles(fontJsonArray, textures_folder);
        this.loadBackgrounds(fontJsonArray, textures_folder);
        this.loadImages(fontJsonArray, textures_folder);
        this.extractDefault();
        this.saveOffsets();
        this.saveSplit(textures_folder.getPath());
        try (FileWriter fileWriter = new FileWriter(
                plugin.getDataFolder() +
                        File.separator + "ResourcePack" +
                        File.separator + "assets" +
                        File.separator + ConfigManager.namespace +
                        File.separator + "font" +
                        File.separator + ConfigManager.font + ".json")
        )
        {
            fileWriter.write(fontJsonObject.toString().replace("\\\\", "\\"));
        }
        catch (IOException e) {
            AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to generate font json file.</red>");
        }
        this.hookCopy(resourcePack_folder);
    }

    private void loadImages(JsonArray jsonArray, File textures_file) {
        if (!ConfigManager.enableImages) return;
        for (SimpleChar simpleChar : plugin.getImageManager().getCharacterMap().values()) {
            JsonObject jo_np = new JsonObject();
            jo_np.add("type", new JsonPrimitive("bitmap"));
            jo_np.add("file", new JsonPrimitive(ConfigManager.namespace + ":" + ConfigManager.images_folder_path.replaceAll("\\\\", "/") + simpleChar.getFile()));
            addCharToArray(jsonArray, simpleChar, jo_np);
            try {
                FileUtils.copyFile(new File(plugin.getDataFolder(), "contents" + File.separator + "images" + File.separator + simpleChar.getFile()), new File(textures_file.getPath() + File.separatorChar + StringUtils.replace(ConfigManager.images_folder_path, "\\", File.separator) + simpleChar.getFile()));
            } catch (IOException e) {
                AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to copy images to resource pack.</red>");
            }
        }
    }

    private void loadNameplates(JsonArray jsonArray, File textures_file) {
        if (!ConfigManager.enableNameplates) return;
        for (NameplateConfig nameplateConfig : plugin.getNameplateManager().getNameplateConfigMap().values()) {
            for (SimpleChar simpleChar : new SimpleChar[]{nameplateConfig.left(), nameplateConfig.middle(), nameplateConfig.right()}) {
                JsonObject jo_np = new JsonObject();
                jo_np.add("type", new JsonPrimitive("bitmap"));
                jo_np.add("file", new JsonPrimitive(ConfigManager.namespace + ":" + ConfigManager.nameplates_folder_path.replaceAll("\\\\", "/") + simpleChar.getFile()));
                addCharToArray(jsonArray, simpleChar, jo_np);
                try {
                    FileUtils.copyFile(new File(plugin.getDataFolder(), "contents" + File.separator + "nameplates" + File.separator + simpleChar.getFile()), new File(textures_file.getPath() + File.separatorChar + StringUtils.replace(ConfigManager.nameplates_folder_path, "\\", File.separator) + simpleChar.getFile()));
                } catch (IOException e) {
                    AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to copy nameplates to resource pack.</red>");
                }
            }
        }
    }

    private void loadBubbles(JsonArray jsonArray, File textures_file) {
        if (!ConfigManager.enableBubbles) return;
        for (BubbleConfig bubbleConfig : plugin.getChatBubblesManager().getBubbleConfigMap().values()) {
            SimpleChar[] simpleChars = new SimpleChar[]{bubbleConfig.left(), bubbleConfig.middle(), bubbleConfig.right(), bubbleConfig.tail()};
            for (SimpleChar simpleChar : simpleChars) {
                JsonObject jo_bb = new JsonObject();
                jo_bb.add("type", new JsonPrimitive("bitmap"));
                jo_bb.add("file", new JsonPrimitive(ConfigManager.namespace + ":" + ConfigManager.bubbles_folder_path.replaceAll("\\\\","/") + simpleChar.getFile()));
                addCharToArray(jsonArray, simpleChar, jo_bb);
                try {
                    FileUtils.copyFile(new File(plugin.getDataFolder(),"contents" + File.separator + "bubbles" + File.separator + simpleChar.getFile()), new File(textures_file.getPath() + File.separator + StringUtils.replace(ConfigManager.bubbles_folder_path, "\\", File.separator) + simpleChar.getFile()));
                }
                catch (IOException e){
                    AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to copy bubbles to resource pack.</red>");
                }
            }
        }
    }

    private void loadBackgrounds(JsonArray jsonArray, File textures_file) {
        if (!ConfigManager.enableBackground) return;
        for (BackGroundConfig backGroundConfig : plugin.getBackgroundManager().getBackGroundConfigMap().values()) {
            for (SimpleChar simpleChar : new SimpleChar[]{
                    backGroundConfig.left(), backGroundConfig.offset_1(),
                    backGroundConfig.offset_2(), backGroundConfig.offset_4(),
                    backGroundConfig.offset_8(), backGroundConfig.offset_16(),
                    backGroundConfig.offset_32(), backGroundConfig.offset_64(),
                    backGroundConfig.offset_128(), backGroundConfig.right()}) {
                JsonObject jo_bg = new JsonObject();
                jo_bg.add("type", new JsonPrimitive("bitmap"));
                jo_bg.add("file", new JsonPrimitive(ConfigManager.namespace + ":" + ConfigManager.backgrounds_folder_path.replaceAll("\\\\","/") + simpleChar.getFile()));
                addCharToArray(jsonArray, simpleChar, jo_bg);
                try {
                    FileUtils.copyFile(new File(plugin.getDataFolder(), "contents" + File.separator + "backgrounds" + File.separator + simpleChar.getFile()), new File(textures_file.getPath() + File.separatorChar + StringUtils.replace(ConfigManager.backgrounds_folder_path, "\\", File.separator) + simpleChar.getFile()));
                }
                catch (IOException e){
                    AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to copy backgrounds to resource pack.</red>");
                }
            }
        }
    }

    private void addCharToArray(JsonArray jsonArray, SimpleChar simpleChar, JsonObject jsonObject) {
        jsonObject.add("ascent", new JsonPrimitive(simpleChar.getAscent()));
        jsonObject.add("height", new JsonPrimitive(simpleChar.getHeight()));
        JsonArray ja_simple = new JsonArray();
        ja_simple.add(native2ascii(simpleChar.getChars()));
        jsonObject.add("chars", ja_simple);
        jsonArray.add(jsonObject);
    }

    private void extractDefault() {
        if (ConfigManager.extractShader) {
            String path = "ResourcePack" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "shaders" + File.separator + "core" + File.separator;
            plugin.saveResource(path + "rendertype_text.fsh", true);
            plugin.saveResource(path + "rendertype_text.json", true);
            plugin.saveResource(path + "rendertype_text.vsh", true);
        }
        if (ConfigManager.extractBars) {
            String path = "ResourcePack" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "gui" + File.separator;
            plugin.saveResource(path + "bars.png", true);
        }
    }

    private void saveOffsets() {
        for (int ascent : plugin.getPlaceholderManager().getDescent_fonts()) {
            JsonObject jsonObject_offset = new JsonObject();
            JsonArray jsonArray_offset = new JsonArray();
            jsonObject_offset.add("providers", jsonArray_offset);
            JsonObject jo_providers = new JsonObject();

            jo_providers.add("type", new JsonPrimitive("space"));
            JsonObject jo_space = new JsonObject();
            jo_space.add(" ", new JsonPrimitive(4));
            jo_space.add("\\u200c", new JsonPrimitive(0));
            jo_providers.add("advances", jo_space);
            jsonArray_offset.add(jo_providers);

            JsonObject jo_ascii = new JsonObject();
            jo_ascii.add("type", new JsonPrimitive("bitmap"));
            jo_ascii.add("file", new JsonPrimitive("minecraft:font/ascii.png"));
            jo_ascii.add("ascent", new JsonPrimitive(ascent));
            jo_ascii.add("height", new JsonPrimitive(8));
            JsonArray ja_ascii = new JsonArray();
            ja_ascii.add("\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000");
            ja_ascii.add("\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000");
            ja_ascii.add("\\u0020\\u0021\\u0022\\u0023\\u0024\\u0025\\u0026\\u0027\\u0028\\u0029\\u002a\\u002b\\u002c\\u002d\\u002e\\u002f");
            ja_ascii.add("\\u0030\\u0031\\u0032\\u0033\\u0034\\u0035\\u0036\\u0037\\u0038\\u0039\\u003a\\u003b\\u003c\\u003d\\u003e\\u003f");
            ja_ascii.add("\\u0040\\u0041\\u0042\\u0043\\u0044\\u0045\\u0046\\u0047\\u0048\\u0049\\u004a\\u004b\\u004c\\u004d\\u004e\\u004f");
            ja_ascii.add("\\u0050\\u0051\\u0052\\u0053\\u0054\\u0055\\u0056\\u0057\\u0058\\u0059\\u005a\\u005b\\u005c\\u005d\\u005e\\u005f");
            ja_ascii.add("\\u0060\\u0061\\u0062\\u0063\\u0064\\u0065\\u0066\\u0067\\u0068\\u0069\\u006a\\u006b\\u006c\\u006d\\u006e\\u006f");
            ja_ascii.add("\\u0070\\u0071\\u0072\\u0073\\u0074\\u0075\\u0076\\u0077\\u0078\\u0079\\u007a\\u007b\\u007c\\u007d\\u007e\\u0000");
            ja_ascii.add("\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000");
            ja_ascii.add("\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u00a3\\u0000\\u0000\\u0192");
            ja_ascii.add("\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u00aa\\u00ba\\u0000\\u0000\\u00ac\\u0000\\u0000\\u0000\\u00ab\\u00bb");
            ja_ascii.add("\\u2591\\u2592\\u2593\\u2502\\u2524\\u2561\\u2562\\u2556\\u2555\\u2563\\u2551\\u2557\\u255d\\u255c\\u255b\\u2510");
            ja_ascii.add("\\u2514\\u2534\\u252c\\u251c\\u2500\\u253c\\u255e\\u255f\\u255a\\u2554\\u2569\\u2566\\u2560\\u2550\\u256c\\u2567");
            ja_ascii.add("\\u2568\\u2564\\u2565\\u2559\\u2558\\u2552\\u2553\\u256b\\u256a\\u2518\\u250c\\u2588\\u2584\\u258c\\u2590\\u2580");
            ja_ascii.add("\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u2205\\u2208\\u0000");
            ja_ascii.add("\\u2261\\u00b1\\u2265\\u2264\\u2320\\u2321\\u00f7\\u2248\\u00b0\\u2219\\u0000\\u221a\\u207f\\u00b2\\u25a0\\u0000");
            jo_ascii.add("chars", ja_ascii);
            jsonArray_offset.add(jo_ascii);

            JsonObject legacy_unicode = new JsonObject();
            legacy_unicode.add("type", new JsonPrimitive("legacy_unicode"));
            legacy_unicode.add("sizes", new JsonPrimitive("minecraft:font/glyph_sizes.bin"));
            legacy_unicode.add("template", new JsonPrimitive("minecraft:font/unicode_page_%s.png"));
            jsonArray_offset.add(legacy_unicode);

            try (FileWriter fileWriter = new FileWriter(
                    plugin.getDataFolder() +
                            File.separator + "ResourcePack" +
                            File.separator + "assets" +
                            File.separator + ConfigManager.namespace +
                            File.separator + "font" +
                            File.separator + "ascent_" + ascent + ".json")) {
                fileWriter.write(jsonObject_offset.toString().replace("\\\\", "\\"));
            }
            catch (IOException e) {
                AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to generate offset font json.</red>");
            }
        }
    }

    private void deleteDirectory(File file){
        if (file.exists()) {
            try{
                FileUtils.deleteDirectory(file);
            }
            catch (IOException e){
                e.printStackTrace();
                AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to delete the generated folder...</red>" );
            }
        }
    }

    private List<JsonObject> getOffsetFontEnums() {
        ArrayList<JsonObject> list = new ArrayList<>();
        for (OffsetFont offsetFont : OffsetFont.values()) {
            list.add(this.getOffsetFontChar(offsetFont.getHeight(), offsetFont.getCharacter()));
        }
        return list;
    }

    private JsonObject getOffsetFontChar(int height, char character) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("type", new JsonPrimitive("bitmap"));
        jsonObject.add("file", new JsonPrimitive(ConfigManager.namespace + ":" + ConfigManager.space_split_folder_path.replaceAll("\\\\","/") + "space_split.png"));
        jsonObject.add("ascent", new JsonPrimitive(-5000));
        jsonObject.add("height", new JsonPrimitive(height));
        final JsonArray jsonArray = new JsonArray();
        jsonArray.add(native2ascii(character));
        jsonObject.add("chars", jsonArray);
        return jsonObject;
    }

    private String native2ascii(char ch) {
        if (ch > '\u007f') {
            StringBuilder stringBuilder_1 = new StringBuilder("\\u");
            StringBuilder stringBuilder_2 = new StringBuilder(Integer.toHexString(ch));
            stringBuilder_2.reverse();
            for (int n = 4 - stringBuilder_2.length(), i = 0; i < n; i++) stringBuilder_2.append('0');
            for (int j = 0; j < 4; j++) stringBuilder_1.append(stringBuilder_2.charAt(3 - j));
            return stringBuilder_1.toString();
        }
        return Character.toString(ch);
    }

    private void saveSplit(String texture_folder_path) {
        try {
            plugin.saveResource("space_split.png", false);
            FileUtils.copyFile(new File(plugin.getDataFolder(),"space_split.png"), new File(texture_folder_path + File.separator + StringUtils.replace(ConfigManager.space_split_folder_path, "\\", File.separator) + "space_split.png"));
            File file = new File(plugin.getDataFolder(),"space_split.png");
            if (file.exists()) {
                file.delete();
            }
        } catch (IOException e){
            AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to copy space_split.png to resource pack...</red>");
        }
    }

    private void hookCopy(File resourcePack_folder) {
        if (ConfigManager.itemsAdderHook){
            try {
                FileUtils.copyDirectory(resourcePack_folder, new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ItemsAdder")).getDataFolder() + File.separator + "contents" + File.separator + "contents/nameplates" + File.separator + "resourcepack") );
            }
            catch (IOException e){
                e.printStackTrace();
                AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to copy files to ItemsAdder...</red>");
            }
        }
        if (ConfigManager.oraxenHook){
            try {
                FileUtils.copyDirectory(new File(resourcePack_folder, "assets"), new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Oraxen")).getDataFolder() + File.separator + "pack" + File.separator + "assets"));
            }
            catch (IOException e){
                e.printStackTrace();
                AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to copy files to Oraxen...</red>");
            }
        }
    }
}