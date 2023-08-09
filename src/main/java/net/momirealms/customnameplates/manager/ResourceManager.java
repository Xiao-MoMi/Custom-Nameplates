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
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
                FileUtils.copyFile(new File(plugin.getDataFolder(), "contents" + File.separator + "images" + File.separator + simpleChar.getFile()), new File(textures_file.getPath() + File.separatorChar + ConfigManager.images_folder_path.replace("\\", File.separator) + simpleChar.getFile()));
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
                    FileUtils.copyFile(new File(plugin.getDataFolder(), "contents" + File.separator + "nameplates" + File.separator + simpleChar.getFile()), new File(textures_file.getPath() + File.separatorChar + ConfigManager.nameplates_folder_path.replace("\\", File.separator) + simpleChar.getFile()));
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
                    FileUtils.copyFile(new File(plugin.getDataFolder(),"contents" + File.separator + "bubbles" + File.separator + simpleChar.getFile()), new File(textures_file.getPath() + File.separator + ConfigManager.bubbles_folder_path.replace("\\", File.separator) + simpleChar.getFile()));
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
                    FileUtils.copyFile(new File(plugin.getDataFolder(), "contents" + File.separator + "backgrounds" + File.separator + simpleChar.getFile()), new File(textures_file.getPath() + File.separatorChar + ConfigManager.backgrounds_folder_path.replace("\\", File.separator) + simpleChar.getFile()));
                } catch (IOException e){
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
            String line;
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(plugin.getDataFolder(), path + "rendertype_text.vsh")
                            ), StandardCharsets.UTF_8
                    )
            )) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            File outPut = new File(plugin.getDataFolder(), path + "rendertype_text.vsh");
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outPut), StandardCharsets.UTF_8))) {
                writer.write(sb.toString()
                        .replace("{hide}", !ConfigManager.hideScoreboardNumber ? "" :
                        "if (Position.z == 0.0\n" +
                        "      && gl_Position.x >= 0.94\n" +
                        "      && gl_Position.y >= -0.35\n" +
                        "      && vertexColor.g == 84.0/255.0\n" +
                        "      && vertexColor.g == 84.0/255.0\n" +
                        "      && vertexColor.r == 252.0/255.0\n" +
                        "      && gl_VertexID <= 7\n" +
                        "    ) {\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vec4(ScreenSize + 100.0, 0.0, ScreenSize + 100.0);\n" +
                        "    }"
                        )
                        .replace("{IA}", !ConfigManager.iaShaderSupport ? "" :
                        "if (Color.xyz == vec3(255., 255., 254.) / 255.) {\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "        vertexColor = ((.6 + .6 * cos(6. * (gl_Position.x + GameTime * 1000.) + vec4(0, 23, 21, 1))) + vec4(0., 0., 0., 1.)) * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "    } else if(Color.xyz == vec3(255., 255., 253.) / 255.) {\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "        vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        gl_Position.y = gl_Position.y + sin(GameTime * 12000. + (gl_Position.x * 6)) / 150.;\n" +
                        "    } else if (Color.xyz == vec3(255., 255., 252.) / 255.) {\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "        vertexColor = ((.6 + .6 * cos(6. * (gl_Position.x + GameTime * 1000.) + vec4(0, 23, 21, 1))) + vec4(0., 0., 0., 1.)) * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        gl_Position.y = gl_Position.y + sin(GameTime*12000. + (gl_Position.x*6)) / 150.;\n" +
                        "    } else if (Color.xyz == vec3(255., 255., 251.) / 255.) {\n" +
                        "        vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        float vertexId = mod(gl_VertexID, 4.0);\n" +
                        "        if (vertex.z <= 0.) {\n" +
                        "            if (vertexId == 3. || vertexId == 0.) vertex.y += cos(GameTime * 12000. / 4) * 0.1;\n" +
                        "            vertex.y += max(cos(GameTime*12000. / 4) * 0.1, 0.);\n" +
                        "        } else {\n" +
                        "            if (vertexId == 3. || vertexId == 0.) vertex.y -= cos(GameTime * 12000. / 4) * 3;\n" +
                        "            vertex.y -= max(cos(GameTime*12000. / 4) * 4, 0.);\n" +
                        "        }\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    } else if (Color.xyz == vec3(255., 254., 254.) / 255.) {\n" +
                        "        float vertexId = mod(gl_VertexID, 4.0);\n" +
                        "        if (vertex.z <= 0.) {\n" +
                        "            if (vertexId == 3. || vertexId == 0.) vertex.y += cos(GameTime * 12000. / 4) * 0.1;\n" +
                        "            vertex.y += max(cos(GameTime*12000. / 4) * 0.1, 0.);\n" +
                        "        } else {\n" +
                        "            if (vertexId == 3. || vertexId == 0.) vertex.y -= cos(GameTime * 12000. / 4) * 3;\n" +
                        "            vertex.y -= max(cos(GameTime*12000. / 4) * 4, 0.);\n" +
                        "        }\n" +
                        "        vertexColor = ((.6 + .6 * cos(6. * (gl_Position.x + GameTime * 1000.) + vec4(0, 23, 21, 1))) + vec4(0., 0., 0., 1.)) * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    } else ")
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ConfigManager.extractBars) {
            String path = "ResourcePack" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "gui" + File.separator;
            plugin.saveResource(path + "bars.png", true);
        }
        setPackFormat();
    }

    private void setPackFormat() {
        plugin.saveResource("ResourcePack" + File.separator + "pack.mcmeta", false);
        File format_file = new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "pack.mcmeta");
        String line;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(format_file), StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(new File(plugin.getDataFolder(),
                        "ResourcePack" + File.separator + "pack.mcmeta")), StandardCharsets.UTF_8))) {
            writer.write(sb.toString().replace("%version%", String.valueOf(plugin.getVersionHelper().getPack_format())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveOffsets() {
        for (int ascent : plugin.getPlaceholderManager().getDescent_fonts()) {
            String line;
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(plugin.getDataFolder(), "templates" + File.separator + (plugin.getVersionHelper().isVersionNewerThan1_20() ? "default1_20.json" : "default.json"))), StandardCharsets.UTF_8))) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            File outPut = new File(plugin.getDataFolder(),
                    "ResourcePack" +
                            File.separator + "assets" +
                            File.separator + ConfigManager.namespace +
                            File.separator + "font" +
                            File.separator + "ascent_" + ascent + ".json");
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outPut), StandardCharsets.UTF_8))) {
                writer.write(sb.toString().replace("\\\\", "\\").replace("%ascent%", String.valueOf(ascent)).replace("%ASCENT%", String.valueOf(ascent+3)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!ConfigManager.enable1_20_Unicode && plugin.getVersionHelper().isVersionNewerThan1_20()) {
            AdventureUtils.consoleMessage("<white>[CustomNameplates] For the moment decent unicode is not available on 1.20. You can enable support-1_20-unicodes in config.yml to ignore the limit.");
            return;
        }
        for (int ascent : plugin.getPlaceholderManager().getDescent_unicode_fonts()) {
            String line;
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(plugin.getDataFolder(), "templates" + File.separator + "unicode.json")), StandardCharsets.UTF_8))) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(new File(plugin.getDataFolder(),
                            "ResourcePack" +
                                    File.separator + "assets" +
                                    File.separator + ConfigManager.namespace +
                                    File.separator + "font" +
                                    File.separator + "unicode_ascent_" + ascent + ".json")), StandardCharsets.UTF_8))) {
                writer.write(sb.toString().replace("\\\\", "\\").replace("%ascent%", String.valueOf(ascent)).replace("%ASCENT%", String.valueOf(ascent+3)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ConfigManager.enable1_20_Unicode) {
            try {
                FileUtils.copyDirectory(new File(plugin.getDataFolder(), "unicodes"), new File(plugin.getDataFolder(),
                        "ResourcePack" +
                                File.separator + "assets" +
                                File.separator + "minecraft" +
                                File.separator + "textures" +
                                File.separator + "font"));
            } catch (IOException e) {
                e.printStackTrace();
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveSplit(String texture_folder_path) {
        try {
            plugin.saveResource("space_split.png", false);
            FileUtils.copyFile(new File(plugin.getDataFolder(),"space_split.png"), new File(texture_folder_path + File.separator + ConfigManager.space_split_folder_path.replace("\\", File.separator) + "space_split.png"));
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
                FileUtils.copyDirectory(new File(resourcePack_folder, "assets"), new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ItemsAdder")).getDataFolder() + File.separator + "contents" + File.separator + "nameplates" + File.separator + "resourcepack" + File.separator + "assets") );
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