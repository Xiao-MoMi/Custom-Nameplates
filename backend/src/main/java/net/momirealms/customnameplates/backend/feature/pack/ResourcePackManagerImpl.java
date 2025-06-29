/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customnameplates.backend.feature.pack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;
import net.momirealms.customnameplates.api.feature.OffsetFont;
import net.momirealms.customnameplates.api.feature.advance.CharacterFontAdvanceData;
import net.momirealms.customnameplates.api.feature.background.Background;
import net.momirealms.customnameplates.api.feature.bubble.Bubble;
import net.momirealms.customnameplates.api.feature.image.Animation;
import net.momirealms.customnameplates.api.feature.image.Image;
import net.momirealms.customnameplates.api.feature.nameplate.Nameplate;
import net.momirealms.customnameplates.api.feature.pack.ResourcePackManager;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.util.CharacterUtils;
import net.momirealms.customnameplates.api.util.ZipUtils;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ResourcePackManagerImpl implements ResourcePackManager {

    private final CustomNameplates plugin;

    public ResourcePackManagerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void generate() {
        File resourcePackFolder = new File(plugin.getDataFolder() + File.separator + "ResourcePack");
        // delete the old one
        this.deleteDirectory(resourcePackFolder);

        // create folders
        File fontFolder = new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "assets" + File.separator + ConfigManager.namespace() + File.separatorChar + "font");
        File texturesFolder = new File(plugin.getDataFolder(), "ResourcePack" + File.separator+ "assets" + File.separator + ConfigManager.namespace() + File.separatorChar + "textures");
        if (!fontFolder.mkdirs() || !texturesFolder.mkdirs()) {
            plugin.getPluginLogger().severe("Failed to generate resource pack folders");
            return;
        }

        // save BossBars
        this.saveBossBar();
        // save unicodes
        this.saveLegacyUnicodes();

        if (ConfigManager.enableShader()) {
            if (ConfigManager.minPackVersion() >= 21.4f) {
                // do nothing
            } else if (ConfigManager.minPackVersion() >= 21.2f) {
                this.generateShaders("ResourcePack" + File.separator + "overlay_1_21_2" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "shaders" + File.separator + "core" + File.separator, true);
            } else if (ConfigManager.minPackVersion() >= 20.5f) {
                this.generateShaders("ResourcePack" + File.separator + "overlay_1_20_5" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "shaders" + File.separator + "core" + File.separator, true);
                this.generateShaders("ResourcePack" + File.separator + "overlay_1_21_2" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "shaders" + File.separator + "core" + File.separator, true);
            } else {
                this.generateShaders("ResourcePack" + File.separator + "overlay_1_20_2" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "shaders" + File.separator + "core" + File.separator, false);
                this.generateShaders("ResourcePack" + File.separator + "overlay_1_20_5" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "shaders" + File.separator + "core" + File.separator, true);
                this.generateShaders("ResourcePack" + File.separator + "overlay_1_21_2" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "shaders" + File.separator + "core" + File.separator, true);
            }
        }

        // create json object
        JsonObject fontJson = new JsonObject();
        JsonArray providers = new JsonArray();
        fontJson.add("providers", providers);
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
        // save json object to file
        this.saveFont(fontJson);
        // generate shift fonts
        this.generateFont();
        // set pack.mcmeta/pack.png
        this.setPackFormat();
        // copy the resource pack to hooked plugins
        this.copyResourcePackToHookedPlugins(resourcePackFolder);

        try {
            ZipUtils.zipDirectory(resourcePackFolder.toPath(), plugin.getDataFolder().toPath().resolve("resourcepack.zip"));
        } catch (IOException e) {
            plugin.getPluginLogger().warn("Failed to zip resourcepack.zip", e);
        }
    }

    private void saveFont(JsonObject fontJson) {
        try (FileWriter fileWriter = new FileWriter(
                plugin.getDataFolder() +
                        File.separator + "ResourcePack" +
                        File.separator + "assets" +
                        File.separator + ConfigManager.namespace() +
                        File.separator + "font" +
                        File.separator + ConfigManager.font() + ".json")
        ) {
            fileWriter.write(fontJson.toString().replace("\\\\", "\\"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateFont() {
        YamlDocument document = ConfigManager.getMainConfig();
        Section section = document.getSection("other-settings.shift-fonts");
        if (section != null) {
            for (Object key : section.getKeys()) {
                if (key instanceof String font) {
                    JsonObject jo = new JsonObject();
                    JsonArray providers = new JsonArray();
                    jo.add("providers", providers);
                    List<String> order = section.getStringList(font);
                    for (String f : order) {
                        String[] split = f.split(":", 2);
                        Map<String, Object> properties = new HashMap<>();
                        if (split.length == 2) {
                            properties.put("shift_y", Integer.parseInt(split[1]));
                        }
                        CharacterFontAdvanceData data = plugin.getAdvanceManager().templateFontDataById(split[0]);
                        if (data == null) {
                            plugin.getPluginLogger().warn("Font template [" + split[0] + "] not found");
                            continue;
                        }
                        List<JsonObject> jsonObject = data.fontProvider(properties);
                        if (jsonObject == null) {
                            plugin.getPluginLogger().warn("Font template [" + split[0] + "] doesn't support shift");
                            continue;
                        }
                        for (JsonObject o : jsonObject) {
                            providers.add(o);
                        }
                    }

                    try (FileWriter file = new FileWriter(new File(plugin.getDataFolder(),
                            "ResourcePack" +
                            File.separator + "assets" +
                            File.separator + ConfigManager.namespace() +
                            File.separator + "font" +
                            File.separator + font + ".json"))) {
                        file.write(jo.toString().replace("\\\\", "\\"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void setPackFormat() {
        if (ConfigManager.minPackVersion() >= 21.4f) {
            plugin.getConfigManager().saveResource("ResourcePack" + File.separator + "pack_1_21_4.mcmeta");
            File file = new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "pack_1_21_4.mcmeta");
            file.renameTo(new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "pack.mcmeta"));
        } else if (ConfigManager.minPackVersion() >= 21.2f) {
            plugin.getConfigManager().saveResource("ResourcePack" + File.separator + "pack_1_21_2.mcmeta");
            File file = new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "pack_1_21_2.mcmeta");
            file.renameTo(new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "pack.mcmeta"));
        } else if (ConfigManager.minPackVersion() >= 20.5f) {
            plugin.getConfigManager().saveResource("ResourcePack" + File.separator + "pack_1_20_5.mcmeta");
            File file = new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "pack_1_20_5.mcmeta");
            file.renameTo(new File(plugin.getDataFolder(), "ResourcePack" + File.separator + "pack.mcmeta"));
        } else {
            plugin.getConfigManager().saveResource("ResourcePack" + File.separator + "pack.mcmeta");
        }
        plugin.getConfigManager().saveResource("ResourcePack" + File.separator + "pack.png");
    }

    private void copyResourcePackToHookedPlugins(File resourcePackFolder) {
        File pluginsFolder = plugin.getDataFolder().getParentFile();
        if (ConfigManager.packItemsAdder()) {
            File file = new File(pluginsFolder, "ItemsAdder" + File.separator + "config.yml");
            YamlDocument iaConfig = plugin.getConfigManager().loadData(file);
            List<String> folders = iaConfig.getStringList("resource-pack.zip.merge_other_plugins_resourcepacks_folders");
            boolean changed = false;
            if (!folders.contains("CustomNameplates/ResourcePack")) {
                folders.add("CustomNameplates/ResourcePack");
                iaConfig.set("resource-pack.zip.merge_other_plugins_resourcepacks_folders", folders);
                changed = true;
            }
            if (changed) {
                try {
                    iaConfig.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (ConfigManager.packItemsAdderLegacy()){
            try {
                FileUtils.copyDirectory(new File(resourcePackFolder, "assets"), new File(pluginsFolder,  "ItemsAdder" + File.separator + "contents" + File.separator + "nameplates" + File.separator + "resourcepack" + File.separator + "assets") );
            } catch (IOException e){
                plugin.getPluginLogger().warn("Failed to copy files to ItemsAdder", e);
            }
        }
        if (ConfigManager.packOraxen()){
            try {
                FileUtils.copyDirectory(new File(resourcePackFolder, "assets"), new File(pluginsFolder, "Oraxen" + File.separator + "pack" + File.separator + "assets"));
            } catch (IOException e){
                plugin.getPluginLogger().warn("Failed to copy files to Oraxen", e);
            }
        }
        if (ConfigManager.packNexo()){
            try {
                FileUtils.deleteDirectory(new File(pluginsFolder, "Nexo" + File.separator + "pack" + File.separator + "external_packs" + File.separator + "CustomNameplates"));
                FileUtils.copyDirectory(resourcePackFolder, new File(pluginsFolder, "Nexo" + File.separator + "pack" + File.separator + "external_packs" + File.separator + "CustomNameplates"));
            } catch (IOException e){
                plugin.getPluginLogger().warn("Failed to copy files to Nexo", e);
            }
        }
        if (ConfigManager.packCraftEngine()){
            try {
                FileUtils.deleteDirectory(new File(pluginsFolder, "CraftEngine" + File.separator + "resources" + File.separator + "nameplates"));
                FileUtils.copyDirectory(resourcePackFolder, new File(pluginsFolder, "CraftEngine" + File.separator + "resources" + File.separator + "CustomNameplates" + File.separator + "resourcepack"));
                FileUtils.delete(new File(pluginsFolder, "CraftEngine" + File.separator + "resources" + File.separator + "CustomNameplates" + File.separator + "resourcepack" + File.separator + "pack.mcmeta"));
                FileUtils.delete(new File(pluginsFolder, "CraftEngine" + File.separator + "resources" + File.separator + "CustomNameplates" + File.separator + "resourcepack" + File.separator + "pack.png"));
            } catch (IOException e){
                plugin.getPluginLogger().warn("Failed to copy files to CraftEngine", e);
            }
        }
        if(ConfigManager.packCreativeCentral()) {
            try {
                FileUtils.copyDirectory(new File(resourcePackFolder, "assets"), new File(pluginsFolder, "creative-central" + File.separator + "resources" + File.separator + "assets"));
            } catch (IOException e){
                plugin.getPluginLogger().warn("Failed to copy files to Creative-Central", e);
            }
        }
    }

    private List<JsonObject> getBubbles(File texturesFolder) {
        ArrayList<JsonObject> list = new ArrayList<>();
        if (!ConfigManager.bubbleModule()) return list;
        for (Bubble bubble : plugin.getBubbleManager().bubbles()) {
            for (ConfiguredCharacter configuredChar : new ConfiguredCharacter[]{bubble.left(), bubble.middle(), bubble.right(), bubble.tail()}) {
                JsonObject jo = new JsonObject();
                jo.add("type", new JsonPrimitive("bitmap"));
                jo.add("file", new JsonPrimitive(ConfigManager.namespace() + ":" + ConfigManager.bubblePath().replace("\\", "/") + configuredChar.imageFile().getName()));
                jo.add("ascent", new JsonPrimitive(configuredChar.ascent()));
                jo.add("height", new JsonPrimitive(configuredChar.height()));
                JsonArray ja = new JsonArray();
                ja.add(CharacterUtils.char2Unicode(configuredChar.character()));
                jo.add("chars", ja);
                list.add(jo);
                try {
                    FileUtils.copyFile(
                            new File(plugin.getDataFolder(),
                                    "contents" + File.separator + "bubbles" + File.separator + configuredChar.imageFile().getName()),
                            new File(texturesFolder,
                                    ConfigManager.bubblePath().replace("\\", File.separator) + configuredChar.imageFile().getName()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return list;
    }

    private List<JsonObject> getBackgrounds(File texturesFolder) {
        ArrayList<JsonObject> list = new ArrayList<>();
        if (!ConfigManager.backgroundModule()) return list;
        for (Background backGround : plugin.getBackgroundManager().getBackgrounds()) {
            for (ConfiguredCharacter configuredChar : new ConfiguredCharacter[]{
                    backGround.left(), backGround.width_1(),
                    backGround.width_2(), backGround.width_4(),
                    backGround.width_8(), backGround.width_16(),
                    backGround.width_32(), backGround.width_64(),
                    backGround.width_128(), backGround.right()}
            ) {
                JsonObject jo = new JsonObject();
                jo.add("type", new JsonPrimitive("bitmap"));
                jo.add("file", new JsonPrimitive(ConfigManager.namespace() + ":" + ConfigManager.backgroundPath().replace("\\", "/") + configuredChar.imageFile().getName()));
                jo.add("ascent", new JsonPrimitive(configuredChar.ascent()));
                jo.add("height", new JsonPrimitive(configuredChar.height()));
                JsonArray ja = new JsonArray();
                ja.add(CharacterUtils.char2Unicode(configuredChar.character()));
                jo.add("chars", ja);
                list.add(jo);
                try {
                    FileUtils.copyFile(
                            new File(plugin.getDataFolder(),
                                    "contents" + File.separator + "backgrounds" + File.separator + configuredChar.imageFile().getName()),
                            new File(texturesFolder,
                                    ConfigManager.backgroundPath().replace("\\", File.separator) + configuredChar.imageFile().getName()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return list;
    }

    private List<JsonObject> getImages(File texturesFolder) {
        ArrayList<JsonObject> list = new ArrayList<>();
        if (!ConfigManager.imageModule()) return list;
        for (Image image : plugin.getImageManager().images()) {
            ConfiguredCharacter character = image.character();
            JsonObject jo = new JsonObject();
            jo.add("type", new JsonPrimitive("bitmap"));
            jo.add("file", new JsonPrimitive(ConfigManager.namespace() + ":" + ConfigManager.imagePath().replace("\\", "/") + character.imageFile().getName()));
            jo.add("ascent", new JsonPrimitive(character.ascent()));
            jo.add("height", new JsonPrimitive(character.height()));
            JsonArray ja = new JsonArray();
            ja.add(CharacterUtils.char2Unicode(character.character()));
            jo.add("chars", ja);
            list.add(jo);
            try {
                File targetFile = new File(texturesFolder,
                        ConfigManager.imagePath().replace("\\", File.separator) + character.imageFile().getName());
                FileUtils.copyFile(
                        new File(plugin.getDataFolder(),
                                "contents" + File.separator + "images" + File.separator + character.imageFile().getName()), targetFile);
                if (image.removeShadow() || image.animation() != null) {
                    BufferedImage bufferedImage = ImageIO.read(targetFile);
                    if (image.removeShadow()) {
                        for (int y = 0; y < bufferedImage.getHeight(); y++) {
                            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                                int argb = bufferedImage.getRGB(x, y);
                                int alpha = (argb >> 24) & 0xff;
                                if (alpha != 0) {
                                    int rgb = argb & 0x00ffffff;
                                    int newArgb = (254 << 24) | rgb;
                                    bufferedImage.setRGB(x, y, newArgb);
                                }
                            }
                        }
                    }

                    Animation animation = image.animation();
                    if (animation != null) {
                        int height = bufferedImage.getHeight();
                        int extra = height % animation.frames();
                        if (extra > 0) {
                            plugin.getPluginLogger().warn("Image height is not a multiple of frame rate: " + image.id());
                            continue;
                        }
                        int eachFrameHeight = height / animation.frames();
                        int width = bufferedImage.getWidth();
                        int speed = Math.min(Math.max(animation.speed(), 1), 255);

                        int alpha = 1;
                        int red = speed;
                        int green = width;
                        int blue = eachFrameHeight;
                        int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;

                        for (int i = 0; i < animation.frames(); i++) {
                            int y = i * eachFrameHeight;
                            bufferedImage.setRGB(0, y, argb);
                        }
                    }

                    ImageIO.write(bufferedImage, "png", targetFile);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    private List<JsonObject> getNameplates(File texturesFolder) {
        ArrayList<JsonObject> list = new ArrayList<>();
        if (!ConfigManager.nameplateModule()) return list;
        for (Nameplate nameplate : plugin.getNameplateManager().nameplates()) {
            for (ConfiguredCharacter configuredChar : new ConfiguredCharacter[]{nameplate.left(), nameplate.middle(), nameplate.right()}) {
                JsonObject jo = new JsonObject();
                jo.add("type", new JsonPrimitive("bitmap"));
                jo.add("file", new JsonPrimitive(ConfigManager.namespace() + ":" + ConfigManager.nameplatePath().replace("\\", "/") + configuredChar.imageFile().getName()));
                jo.add("ascent", new JsonPrimitive(configuredChar.ascent()));
                jo.add("height", new JsonPrimitive(configuredChar.height()));
                JsonArray ja = new JsonArray();
                ja.add(CharacterUtils.char2Unicode(configuredChar.character()));
                jo.add("chars", ja);
                list.add(jo);
                try {
                    FileUtils.copyFile(
                            new File(plugin.getDataFolder(),
                                    "contents" + File.separator + "nameplates" + File.separator + configuredChar.imageFile().getName()),
                            new File(texturesFolder,
                                    ConfigManager.nameplatePath().replace("\\", File.separator) + configuredChar.imageFile().getName()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
            jsonObject.add("file", new JsonPrimitive(ConfigManager.namespace() + ":" + ConfigManager.spaceSplitPath().replace("\\","/") + "space_split.png"));
            jsonObject.add("ascent", new JsonPrimitive(-5000));
            jsonObject.add("height", new JsonPrimitive(offsetFont.height()));
            final JsonArray jsonArray = new JsonArray();
            jsonArray.add(CharacterUtils.char2Unicode(offsetFont.character()));
            jsonObject.add("chars", jsonArray);
            list.add(jsonObject);
        }
        return list;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveSplit(File texturesFolder) {
        try {
            plugin.getConfigManager().saveResource("space_split.png");
            FileUtils.copyFile(new File(plugin.getDataFolder(),"space_split.png"), new File(texturesFolder, ConfigManager.spaceSplitPath().replace("\\", File.separator) + "space_split.png"));
            File file = new File(plugin.getDataFolder(),"space_split.png");
            if (file.exists()) {
                file.delete();
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    public void deleteDirectory(File file){
        if (file.exists()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    private void saveBossBar() {
        if (ConfigManager.bossBar1_20_2()) {
            String color = ConfigManager.removedBarColor().name().toLowerCase(Locale.ENGLISH);
            String path = "ResourcePack" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "gui" + File.separator + "sprites" + File.separator + "boss_bar" + File.separator;
            plugin.getConfigManager().saveResource(path + color + "_background.png");
            plugin.getConfigManager().saveResource(path + color + "_progress.png");
        }
        if (ConfigManager.bossBar1_17()) {
            String path = "ResourcePack" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "gui" + File.separator + "bars.png";
            plugin.getConfigManager().saveResource(path);
            try {
                File inputFile = new File(plugin.getDataFolder(), path);
                BufferedImage image = ImageIO.read(inputFile);
                int y;
                switch (ConfigManager.removedBarColor()) {
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
                throw new RuntimeException(e);
            }
        }
    }

    private void saveLegacyUnicodes() {
        if (ConfigManager.legacyUnicodes()) {
            for (int i = 0; i < 256; i++) {
                var path = "font" + File.separator + "unicode_page_" + String.format("%02x", i) + ".png";
                var destination = "ResourcePack" + File.separator + "assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "font";
                File imageFile = new File(plugin.getDataFolder(), path);
                File destinationFolder = new File(plugin.getDataFolder(), destination);
                if (imageFile.exists()) {
                    try {
                        FileUtils.copyFileToDirectory(imageFile, destinationFolder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void generateShaders(String path, boolean v1_20_5) {

        plugin.getConfigManager().saveResource(path + "rendertype_text.fsh");
        plugin.getConfigManager().saveResource(path + "rendertype_text.json");
        plugin.getConfigManager().saveResource(path + "rendertype_text.vsh");

        String line;
        StringBuilder sb1 = new StringBuilder();
        File shader1 = new File(plugin.getDataFolder(), path + "rendertype_text.vsh");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(shader1), StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                sb1.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String mainShader = v1_20_5 ? ShaderConstants.Nameplates_Shader_1_20_5 : ShaderConstants.Nameplates_Shader_1_20_4;
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(shader1), StandardCharsets.UTF_8))) {
            writer.write(sb1.toString()
                    .replace("%SHADER_0%", !ConfigManager.animatedText() ? "" : ShaderConstants.Animated_Text_Out)
                    .replace("%SHADER_1%", !ConfigManager.itemsAdderEffect() ? mainShader : ShaderConstants.ItemsAdder_Text_Effects + mainShader)
                    .replace("%SHADER_2%", !ConfigManager.animatedText() ? "" : ShaderConstants.Animated_Text_VSH)
                    .replace("%SHADER_3%", !ConfigManager.hideScoreBoardNumber() ? "" : ShaderConstants.Hide_ScoreBoard_Numbers)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File shader2 = new File(plugin.getDataFolder(), path + "rendertype_text.fsh");
        StringBuilder sb2 = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(shader2), StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                sb2.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(shader2), StandardCharsets.UTF_8))) {
            writer.write(sb2.toString()
                    .replace("%SHADER_0%", !ConfigManager.animatedText() ? "" : ShaderConstants.Animated_Text_In)
                    .replace("%SHADER_1%", !ConfigManager.animatedText() ? "" : ShaderConstants.Animated_Text_FSH)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ShaderConstants {

        public static final String Nameplates_Shader_1_20_5 =
                        "if (Color.xyz == vec3(255., 254., 253.) / 255. && (Position.z == 50.03 || Position.z == 200.03 || Position.z == 400.03 || Position.z == 1000.03 || Position.z == 2000 || Position.z == 2200.03 || Position.z == 2400.06 || Position.z == 2400.12 || Position.z == 2650.03 || Position.z == 2800.03)) {\n" +
                        "        vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        vertex.y += 1;\n" +
                        "        vertex.x += 1;\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    } else if (Color.xyz == vec3(240., 240., 240.) / 255.) {\n" +
                        "        vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        vertexColor.rgb = texelFetch(Sampler2, UV2 / 16, 0).rgb;\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    } else if (Color.xyz == vec3(60., 60., 60.) / 255.) {\n" +
                        "        vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        depthLevel = 114514.0;\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    } else {\n" +
                        "        vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    }";

        public static final String Nameplates_Shader_1_20_4 =
                        "if (Color.xyz == vec3(255., 254., 253.) / 255. && (Position.z == 0.03 || Position.z == 0.06 || Position.z == 0.12 || Position.z == 100.03 || Position.z == 200.03 || Position.z == 400.03)) {\n" +
                        "        vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        vertex.y += 1;\n" +
                        "        vertex.x += 1;\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    } else if (Color.xyz == vec3(240., 240., 240.) / 255.) {\n" +
                        "        vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        vertexColor.rgb = texelFetch(Sampler2, UV2 / 16, 0).rgb;\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    } else if (Color.xyz == vec3(60., 60., 60.) / 255.) {\n" +
                        "        vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);\n" +
                        "        depthLevel = 114514.0;\n" +
                        "        gl_Position = ProjMat * ModelViewMat * vertex;\n" +
                        "    } else {\n" +
                        "        vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);\n" +
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
