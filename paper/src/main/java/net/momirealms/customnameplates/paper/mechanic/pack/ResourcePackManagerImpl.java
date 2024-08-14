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

package net.momirealms.customnameplates.paper.mechanic.pack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.ResourcePackManager;
import net.momirealms.customnameplates.api.mechanic.background.BackGround;
import net.momirealms.customnameplates.api.mechanic.bubble.Bubble;
import net.momirealms.customnameplates.api.mechanic.character.ConfiguredChar;
import net.momirealms.customnameplates.api.mechanic.font.OffsetFont;
import net.momirealms.customnameplates.api.mechanic.nameplate.Nameplate;
import net.momirealms.customnameplates.api.mechanic.placeholder.DescentText;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import net.momirealms.customnameplates.paper.util.ConfigUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ResourcePackManagerImpl implements ResourcePackManager {

    private final CustomNameplatesPlugin plugin;

    public ResourcePackManagerImpl(CustomNameplatesPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        unload();
        load();
    }

    public void load() {

    }

    public void unload() {

    }

    @Override
    public void generateResourcePack() {
        // delete the old one
        File resourcePackFolder = new File(plugin.getDataFolder() + File.separator + "ResourcePack");
        this.deleteDirectory(resourcePackFolder);

        // create folders
        File fontFolder = new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "assets" + File.separator + CNConfig.namespace + File.separatorChar + "font");
        File texturesFolder = new File(plugin.getDataFolder(), "ResourcePack" + File.separator+ "assets" + File.separator + CNConfig.namespace + File.separatorChar + "textures");
        if (!fontFolder.mkdirs() || !texturesFolder.mkdirs()) {
            LogUtils.severe("Failed to generate resource pack folders");
            return;
        }

        // create json object
        JsonObject fontJson = new JsonObject();
        JsonArray providers = new JsonArray();
        fontJson.add("providers", providers);

        // save BossBars
        this.saveBossBar();
        // save unicodes
        this.saveLegacyUnicodes();
        // generate shaders
        if (!plugin.getVersionManager().isVersionNewerThan1_20_5()) {
            this.generateShaders("ResourcePack" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "shaders" + File.separator + "core" + File.separator, false);
            this.generateShaders("ResourcePack" + File.separator + "overlay_1_20_5" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "shaders" + File.separator + "core" + File.separator, true);
        } else {
            this.generateShaders("ResourcePack" + File.separator + "overlay_1_20_5" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "shaders" + File.separator + "core" + File.separator, true);
            try {
                FileUtils.copyDirectory(
                        new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "overlay_1_20_5"),
                        new File(plugin.getDataFolder(), "ResourcePack")
                );
                FileUtils.deleteDirectory(new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "overlay_1_20_5"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // add offset characters
        this.getOffsets(texturesFolder).forEach(providers::add);
        // add nameplate characters
        this.getNameplates(texturesFolder).forEach(providers::add);
        // add bubble characters
        this.getBubbles(texturesFolder).forEach(providers::add);
        // add background characters
        this.getBackgrounds(texturesFolder).forEach(providers::add);
        // add image characters
        this.getImages(texturesFolder).forEach(providers::add);
        // set pack.mcmeta
        this.setPackFormat();
        // save json object to file
        this.saveFont(fontJson);
        // copy the resource pack to hooked plugins
        this.copyResourcePackToHookedPlugins(resourcePackFolder);
    }

    private void generateShaders(String path, boolean v1_20_5) {
        if (!CNConfig.enableShader) return;
        plugin.saveResource(path + "rendertype_text.fsh", true);
        plugin.saveResource(path + "rendertype_text.json", true);
        plugin.saveResource(path + "rendertype_text.vsh", true);
        String line;
        StringBuilder sb1 = new StringBuilder();
        File shader1 = new File(plugin.getDataFolder(), path + "rendertype_text.vsh");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(shader1), StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                sb1.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String mainShader = v1_20_5 ? ShaderConstants.Nameplates_Shader_1_20_5 : ShaderConstants.Nameplates_Shader_1_20_4;
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(shader1), StandardCharsets.UTF_8))) {
            writer.write(sb1.toString()
                    .replace("%SHADER_0%", !CNConfig.animatedImage ? "" : ShaderConstants.Animated_Text_Out)
                    .replace("%SHADER_1%", !CNConfig.textEffects ? mainShader : ShaderConstants.ItemsAdder_Text_Effects + mainShader)
                    .replace("%SHADER_2%", !CNConfig.animatedImage ? "" : ShaderConstants.Animated_Text_VSH)
                    .replace("%SHADER_3%", !CNConfig.hideScoreboardNumber ? "" : ShaderConstants.Hide_ScoreBoard_Numbers)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        File shader2 = new File(plugin.getDataFolder(), path + "rendertype_text.fsh");
        StringBuilder sb2 = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(shader2), StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                sb2.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(shader2), StandardCharsets.UTF_8))) {
            writer.write(sb2.toString()
                    .replace("%SHADER_0%", !CNConfig.animatedImage ? "" : ShaderConstants.Animated_Text_In)
                    .replace("%SHADER_1%", !CNConfig.animatedImage ? "" : ShaderConstants.Animated_Text_FSH)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveLegacyUnicodes() {
        if (CNConfig.legacyUnicodes) {
            for (int i = 0; i < 256; i++) {
                var path = "font" + File.separator + "unicode_page_" + String.format("%02x", i) + ".png";
                var destination = "ResourcePack" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "font" + File.separator + "unicode_page_" + String.format("%02x", i) + ".png";
                File imageFile = new File(plugin.getDataFolder(), path);
                File destinationFile = new File(plugin.getDataFolder(), destination);
                if (imageFile.exists()) {
                    try {
                        FileUtils.copyFile(imageFile, destinationFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void saveBossBar() {
        if (CNConfig.newBossBarImage) {
            String color = CNConfig.barColorToRemove.name().toLowerCase(Locale.ENGLISH);
            String path = "ResourcePack" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "gui" + File.separator + "sprites" + File.separator + "boss_bar" + File.separator;
            plugin.saveResource(path + color + "_background.png", true);
            plugin.saveResource(path + color + "_progress.png", true);
        }
        if (CNConfig.legacyBossBarImage) {
            String path = "ResourcePack" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "gui" + File.separator + "bars.png";
            plugin.saveResource(path, true);
            try {
                File inputFile = new File(plugin.getDataFolder(), path);
                BufferedImage image = ImageIO.read(inputFile);
                int y;
                switch (CNConfig.barColorToRemove) {
                    case PINK -> y = 0;
                    case BLUE -> y = 10;
                    case RED -> y = 20;
                    case GREEN -> y = 30;
                    case PURPLE -> y = 50;
                    case WHITE -> y = 60;
                    default -> y = 40;
                }
                int width = 182;
                int height = 10;
                for (int i = 0; i < width; i++) {
                    for (int j = y; j < y + height; j++) {
                        image.setRGB(i, j, 0);
                    }
                }
                ImageIO.write(image, "png", inputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFont(JsonObject fontJson) {
        try (FileWriter fileWriter = new FileWriter(
                plugin.getDataFolder() +
                        File.separator + "ResourcePack" +
                        File.separator + "assets" +
                        File.separator + CNConfig.namespace +
                        File.separator + "font" +
                        File.separator + CNConfig.font + ".json")
        ) {
            fileWriter.write(fontJson.toString().replace("\\\\", "\\"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Integer> ascentTexts = new ArrayList<>();
        ArrayList<Integer> ascentUnicodes = new ArrayList<>();

        for (DescentText descentText : plugin.getPlaceholderManager().getDescentTexts()) {
            if (descentText.isUnicode()) {
                ascentUnicodes.add(descentText.getAscent());
            } else {
                ascentTexts.add(descentText.getAscent());
            }
        }

        ascentTexts.removeAll(ascentUnicodes);

        for (int ascent : ascentTexts) {
            String line;
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(plugin.getDataFolder(), "font" + File.separator + "default.json")), StandardCharsets.UTF_8))) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            File outPut = new File(plugin.getDataFolder(),
                    "ResourcePack" +
                            File.separator + "assets" +
                            File.separator + CNConfig.namespace +
                            File.separator + "font" +
                            File.separator + "ascent_" + ascent + ".json");
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outPut), StandardCharsets.UTF_8))) {
                writer.write(sb.toString().replace("\\\\", "\\").replace("%ascent%", String.valueOf(ascent)).replace("%ASCENT%", String.valueOf(ascent+3)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int ascent : ascentUnicodes) {
            String line;
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(plugin.getDataFolder(), "font" + File.separator + "unicode.json")), StandardCharsets.UTF_8))) {
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
                                    File.separator + CNConfig.namespace +
                                    File.separator + "font" +
                                    File.separator + "ascent_" + ascent + ".json")), StandardCharsets.UTF_8))) {
                writer.write(sb.toString().replace("\\\\", "\\").replace("%ascent%", String.valueOf(ascent)).replace("%ASCENT%", String.valueOf(ascent+3)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleteDirectory(File file){
        if (file.exists()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveSplit(File texturesFolder) {
        try {
            plugin.saveResource("space_split.png", false);
            FileUtils.copyFile(new File(plugin.getDataFolder(),"space_split.png"), new File(texturesFolder, CNConfig.folderSplit.replace("\\", File.separator) + "space_split.png"));
            File file = new File(plugin.getDataFolder(),"space_split.png");
            if (file.exists()) {
                file.delete();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void copyResourcePackToHookedPlugins(File resourcePackFolder) {
        Plugin ia = Bukkit.getPluginManager().getPlugin("ItemsAdder");
        if (ia != null) {
            File file = new File(ia.getDataFolder(), "config.yml");
            YamlConfiguration iaConfig = YamlConfiguration.loadConfiguration(file);
            List<String> folders = iaConfig.getStringList("resource-pack.zip.merge_other_plugins_resourcepacks_folders");
            boolean changed = false;
            if (CNConfig.copyPackIA) {
                if (!folders.contains("CustomNameplates/ResourcePack")) {
                    folders.add("CustomNameplates/ResourcePack");
                    iaConfig.set("resource-pack.zip.merge_other_plugins_resourcepacks_folders", folders);
                    changed = true;
                }
            } else {
                if (folders.contains("CustomNameplates/ResourcePack")) {
                    folders.remove("CustomNameplates/ResourcePack");
                    iaConfig.set("resource-pack.zip.merge_other_plugins_resourcepacks_folders", folders);
                    changed = true;
                }
            }
            if (changed) {
                try {
                    iaConfig.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (CNConfig.copyPackIAOld){
            try {
                FileUtils.copyDirectory(new File(resourcePackFolder, "assets"), new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ItemsAdder")).getDataFolder() + File.separator + "contents" + File.separator + "nameplates" + File.separator + "resourcepack" + File.separator + "assets") );
            }
            catch (IOException e){
                e.printStackTrace();
                LogUtils.warn("Failed to copy files to ItemsAdder...");
            }
        }
        if (CNConfig.copyPackOraxen){
            try {
                FileUtils.copyDirectory(new File(resourcePackFolder, "assets"), new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Oraxen")).getDataFolder() + File.separator + "pack" + File.separator + "assets"));
            }
            catch (IOException e){
                e.printStackTrace();
                LogUtils.warn("Failed to copy files to Oraxen...");
            }
        }
    }

    private List<JsonObject> getNameplates(File texturesFolder) {
        ArrayList<JsonObject> list = new ArrayList<>();
        if (!CNConfig.nameplateModule) return list;
        for (Nameplate nameplate : plugin.getNameplateManager().getNameplates()) {
            for (ConfiguredChar configuredChar : new ConfiguredChar[]{nameplate.getLeft(), nameplate.getMiddle(), nameplate.getRight()}) {
                JsonObject jo = new JsonObject();
                jo.add("type", new JsonPrimitive("bitmap"));
                jo.add("file", new JsonPrimitive(CNConfig.namespace + ":" + CNConfig.folderNameplate.replaceAll("\\\\", "/") + configuredChar.getFile()));
                jo.add("ascent", new JsonPrimitive(configuredChar.getAscent()));
                jo.add("height", new JsonPrimitive(configuredChar.getHeight()));
                JsonArray ja = new JsonArray();
                ja.add(ConfigUtils.native2ascii(configuredChar.getCharacter()));
                jo.add("chars", ja);
                list.add(jo);
                try {
                    FileUtils.copyFile(
                            new File(plugin.getDataFolder(),
                                    "contents" + File.separator + "nameplates" + File.separator + configuredChar.getFile()),
                            new File(texturesFolder,
                                    CNConfig.folderNameplate.replace("\\", File.separator) + configuredChar.getFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }


    private List<JsonObject> getBackgrounds(File texturesFolder) {
        ArrayList<JsonObject> list = new ArrayList<>();
        if (!CNConfig.backgroundModule) return list;
        for (BackGround backGround : plugin.getBackGroundManager().getBackGrounds()) {
            for (ConfiguredChar configuredChar : new ConfiguredChar[]{
                    backGround.getLeft(), backGround.getOffset_1(),
                    backGround.getOffset_2(), backGround.getOffset_4(),
                    backGround.getOffset_8(), backGround.getOffset_16(),
                    backGround.getOffset_32(), backGround.getOffset_64(),
                    backGround.getOffset_128(), backGround.getRight()}
            ) {
                JsonObject jo = new JsonObject();
                jo.add("type", new JsonPrimitive("bitmap"));
                jo.add("file", new JsonPrimitive(CNConfig.namespace + ":" + CNConfig.folderBackground.replaceAll("\\\\", "/") + configuredChar.getFile()));
                jo.add("ascent", new JsonPrimitive(configuredChar.getAscent()));
                jo.add("height", new JsonPrimitive(configuredChar.getHeight()));
                JsonArray ja = new JsonArray();
                ja.add(ConfigUtils.native2ascii(configuredChar.getCharacter()));
                jo.add("chars", ja);
                list.add(jo);
                try {
                    FileUtils.copyFile(
                            new File(plugin.getDataFolder(),
                                    "contents" + File.separator + "backgrounds" + File.separator + configuredChar.getFile()),
                            new File(texturesFolder,
                                    CNConfig.folderBackground.replace("\\", File.separator) + configuredChar.getFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    private List<JsonObject> getBubbles(File texturesFolder) {
        ArrayList<JsonObject> list = new ArrayList<>();
        if (!CNConfig.bubbleModule) return list;
        for (Bubble bubble : plugin.getBubbleManager().getBubbles()) {
            for (ConfiguredChar configuredChar : new ConfiguredChar[]{bubble.getLeft(), bubble.getMiddle(), bubble.getRight(), bubble.getTail()}) {
                JsonObject jo = new JsonObject();
                jo.add("type", new JsonPrimitive("bitmap"));
                jo.add("file", new JsonPrimitive(CNConfig.namespace + ":" + CNConfig.folderBubble.replaceAll("\\\\", "/") + configuredChar.getFile()));
                jo.add("ascent", new JsonPrimitive(configuredChar.getAscent()));
                jo.add("height", new JsonPrimitive(configuredChar.getHeight()));
                JsonArray ja = new JsonArray();
                ja.add(ConfigUtils.native2ascii(configuredChar.getCharacter()));
                jo.add("chars", ja);
                list.add(jo);
                try {
                    FileUtils.copyFile(
                            new File(plugin.getDataFolder(),
                                    "contents" + File.separator + "bubbles" + File.separator + configuredChar.getFile()),
                            new File(texturesFolder,
                                    CNConfig.folderBubble.replace("\\", File.separator) + configuredChar.getFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    private List<JsonObject> getImages(File texturesFolder) {
        ArrayList<JsonObject> list = new ArrayList<>();
        if (!CNConfig.imageModule) return list;
        for (ConfiguredChar configuredChar : plugin.getImageManager().getImages()) {
            JsonObject jo = new JsonObject();
            jo.add("type", new JsonPrimitive("bitmap"));
            jo.add("file", new JsonPrimitive(CNConfig.namespace + ":" + CNConfig.folderImage.replaceAll("\\\\", "/") + configuredChar.getFile()));
            jo.add("ascent", new JsonPrimitive(configuredChar.getAscent()));
            jo.add("height", new JsonPrimitive(configuredChar.getHeight()));
            JsonArray ja = new JsonArray();
            ja.add(ConfigUtils.native2ascii(configuredChar.getCharacter()));
            jo.add("chars", ja);
            list.add(jo);
            try {
                FileUtils.copyFile(
                        new File(plugin.getDataFolder(),
                                "contents" + File.separator + "images" + File.separator + configuredChar.getFile()),
                        new File(texturesFolder,
                                CNConfig.folderImage.replace("\\", File.separator) + configuredChar.getFile()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private List<JsonObject> getOffsets(File texturesFolder) {
        this.saveSplit(texturesFolder);
        ArrayList<JsonObject> list = new ArrayList<>();
        for (OffsetFont offsetFont : OffsetFont.values()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("type", new JsonPrimitive("bitmap"));
            jsonObject.add("file", new JsonPrimitive(CNConfig.namespace + ":" + CNConfig.folderSplit.replaceAll("\\\\","/") + "space_split.png"));
            jsonObject.add("ascent", new JsonPrimitive(-5000));
            jsonObject.add("height", new JsonPrimitive(offsetFont.getHeight()));
            final JsonArray jsonArray = new JsonArray();
            jsonArray.add(ConfigUtils.native2ascii(offsetFont.getCharacter()));
            jsonObject.add("chars", jsonArray);
            list.add(jsonObject);
        }
        return list;
    }

    private void setPackFormat() {
        if (plugin.getVersionManager().isVersionNewerThan1_20_5()) {
            plugin.saveResource("ResourcePack" + File.separator + "pack_1_20_5.mcmeta", false);
            File file = new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "pack_1_20_5.mcmeta");
            file.renameTo(new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "pack.mcmeta"));
        } else {
            plugin.saveResource("ResourcePack" + File.separator + "pack.mcmeta", false);
        }

//        File format_file = new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "pack.mcmeta");
//        String line;
//        StringBuilder sb = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(format_file), StandardCharsets.UTF_8))) {
//            while ((line = reader.readLine()) != null) {
//                sb.append(line).append(System.lineSeparator());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try (BufferedWriter writer = new BufferedWriter(
//                new OutputStreamWriter(new FileOutputStream(new File(plugin.getDataFolder(),
//                        "ResourcePack" + File.separator + "pack.mcmeta")), StandardCharsets.UTF_8))) {
//            writer.write(sb.toString().replace("%version%", String.valueOf(plugin.getVersionManager().getPackFormat())));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static class ShaderConstants {

        public static final String Nameplates_Shader_1_20_5 =
                "if (Color.xyz == vec3(255., 254., 253.) / 255.) {\n" +
                        "        vertexColor = Color*texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        vertex.y += 1;\n" +
                        "        vertex.x += 1;\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    } else if (Color.xyz == vec3(254., 254., 254.) / 255.) {\n" +
                        "        vertexColor = Color*texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        vertex.z *= 1.001;\n" +
                        "        vertex.x *= 1.001;\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    } else if (Color.xyz == vec3(253., 254., 254.) / 255.) {\n" +
                        "        vertexColor = Color*texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        vertex.z *= 1.001001;\n" +
                        "        vertex.x *= 1.001001;\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    } else {\n" +
                        "        vertexColor = Color*texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    }";

        public static final String Nameplates_Shader_1_20_4 =
                "if (Color.xyz == vec3(255., 254., 253.) / 255.) {\n" +
                "        vertexColor = Color*texelFetch(Sampler2, UV2 / 16, 0);\n" +
                "        vertex.y += 1;\n" +
                "        vertex.x += 1;\n" +
                "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                "    } else if (Color.xyz == vec3(254., 254., 254.) / 255.) {\n" +
                "        vertexColor = Color*texelFetch(Sampler2, UV2 / 16, 0);\n" +
                "        vertex.z -= 0.001;\n" +
                "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                "    } else if (Color.xyz == vec3(253., 254., 254.) / 255.) {\n" +
                "        vertexColor = Color*texelFetch(Sampler2, UV2 / 16, 0);\n" +
                "        vertex.z -= 0.0011;\n" +
                "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                "    } else {\n" +
                "        vertexColor = Color*texelFetch(Sampler2, UV2 / 16, 0);\n" +
                "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                "    }";

        public static final String ItemsAdder_Text_Effects =
                "if (Color.xyz == vec3(255., 255., 254.) / 255.) {\n" +
                "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                "        vertexColor = ((.6 + .6 * cos(6. * (gl_Position.x + GameTime * 1000.) + vec4(0, 23, 21, 1))) + vec4(0., 0., 0., 1.)) * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                "    } else if (Color.xyz == vec3(255., 255., 253.) / 255.) {\n" +
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
                "    } else ";
        public static final String Hide_ScoreBoard_Numbers =
                "\n" +
                "    if (Position.z == 0.0\n" +
                "        && gl_Position.x >= 0.94\n" +
                "        && gl_Position.y >= -0.35\n" +
                "        && vertexColor.g == 84.0/255.0\n" +
                "        && vertexColor.g == 84.0/255.0\n" +
                "        && vertexColor.r == 252.0/255.0\n" +
                "        && gl_VertexID <= 7\n" +
                "    ) {\n" +
                "        gl_Position = ProjMat * ModelViewMat * vec4(ScreenSize + 100.0, 0.0, ScreenSize + 100.0);\n" +
                "    }";

        public static final String Animated_Text_FSH =
                "\n" +
                "    vec2 p1 = round(pos1 / (posID == 0 ? 1 - coord.x : 1 - coord.y));\n" +
                "    vec2 p2 = round(pos2 / (posID == 0 ? coord.y : coord.x));\n" +
                "    ivec2 resolution = ivec2(abs(p1 - p2));\n" +
                "    ivec2 corner = ivec2(min(p1, p2));\n" +
                "    vec4 pixel = texture(Sampler0, corner / 256.0) * 255;\n" +
                "    if (pixel.a == 1) {\n" +
                "        ivec2 frames = ivec2(resolution / pixel.gb);\n" +
                "        vec2 uv = (texCoord0 * 256 - corner) / frames.x;\n" +
                "        if (uv.x > pixel.y || uv.y > pixel.z)\n" +
                "            discard;\n" +
                "        int time = int(GameTime * pixel.r * 10 * pixel.x) % int(frames.x * frames.y);\n" +
                "        uv = corner + mod(uv, pixel.yz) + vec2(time % frames.x, time / frames.x % frames.y) * pixel.yz;\n" +
                "        color = texture(Sampler0, uv / 256.0) * vertexColor * ColorModulator;\n" +
                "    }";

        public static final String Animated_Text_VSH =
                "\n" +
                "    pos1 = pos2 = vec2(0);\n" +
                "    posID = gl_VertexID % 4;\n" +
                "    const vec2[4] corners = vec2[4](vec2(0), vec2(0, 1), vec2(1), vec2(1, 0));\n" +
                "    coord = corners[posID];\n" +
                "    if (posID == 0) pos1 = UV0 * 256;\n" +
                "    if (posID == 2) pos2 = UV0 * 256;";

        public static final String Animated_Text_Out =
                "\n" +
                "out vec2 pos1;\n" +
                "out vec2 pos2;\n" +
                "out vec2 coord;\n" +
                "flat out int posID;\n";

        public static final String Animated_Text_In =
                "\n" +
                "in vec2 pos1;\n" +
                "in vec2 pos2;\n" +
                "in vec2 coord;\n" +
                "flat in int posID;\n";
    }
}
