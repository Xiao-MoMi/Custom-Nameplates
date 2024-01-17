package net.momirealms.customnameplates.paper.mechanic.pack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.ResourcePackManager;
import net.momirealms.customnameplates.api.mechanic.background.BackGround;
import net.momirealms.customnameplates.api.mechanic.character.ConfiguredChar;
import net.momirealms.customnameplates.api.mechanic.font.OffsetFont;
import net.momirealms.customnameplates.api.mechanic.nameplate.Nameplate;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import org.bukkit.Bukkit;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public String native2ascii(char c) {
        if (c > '\u007f') {
            StringBuilder stringBuilder_1 = new StringBuilder("\\u");
            StringBuilder stringBuilder_2 = new StringBuilder(Integer.toHexString(c));
            stringBuilder_2.reverse();
            for (int n = 4 - stringBuilder_2.length(), i = 0; i < n; i++) stringBuilder_2.append('0');
            for (int j = 0; j < 4; j++) stringBuilder_1.append(stringBuilder_2.charAt(3 - j));
            return stringBuilder_1.toString();
        }
        return Character.toString(c);
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
        if (CNConfig.copyPackIA) {
            try {
                FileUtils.copyDirectory(new File(resourcePackFolder, "assets"), new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ItemsAdder")).getDataFolder() + File.separator + "contents" + File.separator + "nameplates" + File.separator + "resourcepack" + File.separator + "assets") );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (CNConfig.copyPackOraxen) {
            try {
                FileUtils.copyDirectory(new File(resourcePackFolder, "assets"), new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Oraxen")).getDataFolder() + File.separator + "pack" + File.separator + "assets"));
            } catch (IOException e) {
                e.printStackTrace();
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
                ja.add(native2ascii(configuredChar.getCharacter()));
                jo.add("chars", ja);
                list.add(jo);
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
                jo.add("file", new JsonPrimitive(CNConfig.namespace + ":" + CNConfig.folderNameplate.replaceAll("\\\\", "/") + configuredChar.getFile()));
                jo.add("ascent", new JsonPrimitive(configuredChar.getAscent()));
                jo.add("height", new JsonPrimitive(configuredChar.getHeight()));
                JsonArray ja = new JsonArray();
                ja.add(native2ascii(configuredChar.getCharacter()));
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
        return list;
    }

    private List<JsonObject> getImages(File texturesFolder) {
        ArrayList<JsonObject> list = new ArrayList<>();
        if (!CNConfig.imageModule) return list;
        for (ConfiguredChar configuredChar : plugin.getImageManager().getImages()) {
            JsonObject jo = new JsonObject();
            jo.add("type", new JsonPrimitive("bitmap"));
            jo.add("file", new JsonPrimitive(CNConfig.namespace + ":" + CNConfig.folderNameplate.replaceAll("\\\\", "/") + configuredChar.getFile()));
            jo.add("ascent", new JsonPrimitive(configuredChar.getAscent()));
            jo.add("height", new JsonPrimitive(configuredChar.getHeight()));
            JsonArray ja = new JsonArray();
            ja.add(native2ascii(configuredChar.getCharacter()));
            jo.add("chars", ja);
            list.add(jo);
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
            jsonArray.add(native2ascii(offsetFont.getCharacter()));
            jsonObject.add("chars", jsonArray);
            list.add(jsonObject);
        }
        return list;
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
            writer.write(sb.toString().replace("%version%", String.valueOf(plugin.getVersionManager().getPackFormat())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
