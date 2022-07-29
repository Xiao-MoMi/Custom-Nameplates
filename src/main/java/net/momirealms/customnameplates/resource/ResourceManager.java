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

package net.momirealms.customnameplates.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.AdventureManager;
import net.momirealms.customnameplates.background.BackGround;
import net.momirealms.customnameplates.font.FontCache;
import net.momirealms.customnameplates.font.FontChar;
import net.momirealms.customnameplates.font.FontNegative;
import net.momirealms.customnameplates.nameplates.NameplateConfig;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static net.momirealms.customnameplates.ConfigManager.MainConfig.start;

public class ResourceManager {

    public static HashMap<String, FontCache> caches = new HashMap<>();
    public static HashMap<String, HashMap<String, Character>> bgCaches = new HashMap<>();
    private final CustomNameplates plugin;

    public ResourceManager(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    public void generateResourcePack() {

        File r_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "resources");
        File b_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "backgrounds");
        File g_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "generated");

        if (!r_file.exists()) {
            if (!r_file.mkdir()) {
                AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to create resources folder...</red>");
                return;
            }
            saveDefaultResources();
        }

        if (!b_file.exists()) {
            if (!b_file.mkdir()) {
                AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to create resources folder...</red>");
                return;
            }
            saveDefaultBGResources();
        }

        File[] pngFiles = r_file.listFiles(file -> file.getName().endsWith(".png"));

        if (pngFiles == null) {
            AdventureManager.consoleMessage("<red>[CustomNameplates] Error! No png files detected in resource folder...</red>");
            return;
        }

        Arrays.sort(pngFiles);
        deleteDirectory(g_file);

        File f_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "generated" + File.separatorChar + ConfigManager.MainConfig.namespace + File.separatorChar + "font");
        File t_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "generated" + File.separatorChar + ConfigManager.MainConfig.namespace + File.separatorChar + "textures");

        if (!f_file.mkdirs() || !t_file.mkdirs()) {
            AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to generate resource pack folders...</red>");
            return;
        }

        JsonObject jsonObject_1 = new JsonObject();
        JsonArray jsonArray_1 = new JsonArray();
        jsonObject_1.add("providers", jsonArray_1);

        if (ConfigManager.MainConfig.anotherFont){
            JsonObject jsonObject_2 = new JsonObject();
            jsonObject_2.add("type", new JsonPrimitive("bitmap"));
            jsonObject_2.add("file", new JsonPrimitive("minecraft:font/ascii.png"));
            jsonObject_2.add("ascent", new JsonPrimitive(ConfigManager.MainConfig.fontOffset));
            jsonObject_2.add("height", new JsonPrimitive(8));
            JsonArray jsonArray_2 = new JsonArray();
            jsonArray_2.add("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000");
            jsonArray_2.add("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000");
            jsonArray_2.add(" !\"#$%&'()*+,-./");
            jsonArray_2.add("0123456789:;<=>?");
            jsonArray_2.add("@ABCDEFGHIJKLMNO");
            jsonArray_2.add("PQRSTUVWXYZ[\\\\]^_");
            jsonArray_2.add("`abcdefghijklmno");
            jsonArray_2.add("pqrstuvwxyz{|}~\u0000");
            jsonArray_2.add("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000");
            jsonArray_2.add("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000");
            jsonArray_2.add("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000");
            jsonArray_2.add("\\u2591\\u2592\\u2593\\u2502\\u2524\\u2561\\u2562\\u2556\\u2555\\u2563\\u2551\\u2557\\u255d\\u255c\\u255b\\u2510");
            jsonArray_2.add("\\u2514\\u2534\\u252c\\u251c\\u2500\\u253c\\u255e\\u255f\\u255a\\u2554\\u2569\\u2566\\u2560\\u2550\\u256c\\u2567");
            jsonArray_2.add("\\u2568\\u2564\\u2565\\u2559\\u2558\\u2552\\u2553\\u256b\\u256a\\u2518\\u250c\\u2588\\u2584\\u258c\\u2590\\u2580");
            jsonArray_2.add("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\\u2205\\u2208\u0000");
            jsonArray_2.add("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000");
            jsonObject_2.add("chars", jsonArray_2);
            jsonArray_1.add(jsonObject_2);
        }

        if (ConfigManager.nameplate){
            for (File png : pngFiles) {
                JsonObject jsonObject_2 = new JsonObject();
                char left = start;
                char middle;
                char right;
                start = (char)((right = (char)((middle = (char)(start + '\u0001')) + '\u0001')) + '\u0001');
                FontChar fontChar = new FontChar(left, middle, right);
                String pngName = png.getName().substring(0, png.getName().length() - 4);
                NameplateConfig config = this.getConfiguration(pngName);
                caches.put(pngName, new FontCache(pngName, fontChar, config));
                jsonObject_2.add("type", new JsonPrimitive("bitmap"));
                jsonObject_2.add("file", new JsonPrimitive(ConfigManager.MainConfig.namespace + ":" + ConfigManager.MainConfig.folder_path.replaceAll("\\\\","/") + png.getName().toLowerCase()));
                jsonObject_2.add("ascent", new JsonPrimitive(config.getyoffset()));
                jsonObject_2.add("height", new JsonPrimitive(config.getHeight()));
                JsonArray jsonArray_2 = new JsonArray();
                jsonArray_2.add(native2ascii(fontChar.getLeft()) + native2ascii(fontChar.getMiddle()) + native2ascii(fontChar.getRight()));
                jsonObject_2.add("chars", jsonArray_2);
                jsonArray_1.add(jsonObject_2);

                try{
                    FileUtils.copyFile(png, new File(t_file.getPath() + File.separatorChar + ConfigManager.MainConfig.folder_path + png.getName()));
                }catch (IOException e){
                    e.printStackTrace();
                    AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to copy png files to resource pack...</red>");
                }
            }
            caches.put("none", FontCache.EMPTY);
        }

        if (ConfigManager.background){
            ConfigManager.backgrounds.forEach((key, backGround) -> {
                getBackgrounds(backGround).forEach(jsonArray_1::add);
            });
        }

        getNegativeFontEnums().forEach(jsonArray_1::add);

        CustomNameplates.instance.saveResource("space_split.png", false); //复制space_split.png
        try{
            FileUtils.copyFile(new File(CustomNameplates.instance.getDataFolder(),"space_split.png"), new File(t_file.getPath() + File.separator + "space_split.png"));
        }catch (IOException e){
            e.printStackTrace();
            AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to copy space_split.png to resource pack...</red>");
            return;
        }
        new File(CustomNameplates.instance.getDataFolder(),"space_split.png").delete(); //删除拷贝出的默认文件

        try (FileWriter fileWriter = new FileWriter(CustomNameplates.instance.getDataFolder() + File.separator + "generated" + File.separatorChar + ConfigManager.MainConfig.namespace + File.separatorChar + "font" + File.separatorChar + ConfigManager.MainConfig.font + ".json")) {
            fileWriter.write(jsonObject_1.toString().replace("\\\\", "\\"));
        } catch (IOException e) {
            e.printStackTrace();
            AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to generate font json...</red>");
            return;
        }

        AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>ResourcePack has been generated! <white>" + (this.caches.size() -1) + " <color:#baffd1>nameplates loaded!");
        if (ConfigManager.MainConfig.itemsAdder){
            try{
                FileUtils.copyDirectory(g_file, new File(Bukkit.getPluginManager().getPlugin("ItemsAdder").getDataFolder() + File.separator + "data"+ File.separator + "resource_pack" + File.separator + "assets") );
                AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>Detected <color:#90EE90>ItemsAdder!<color:#baffd1> Automatically sent rp to ItemsAdder folder!");
            }catch (IOException e){
                e.printStackTrace();
                AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to copy files to ItemsAdder...</red>");
            }
        }
    }

    private void saveDefaultResources() {
        List<String> list = Arrays.asList("cat", "egg", "cheems", "wither", "xmas", "halloween","hutao","starsky","trident","rabbit");
        list.forEach(name -> CustomNameplates.instance.saveResource("resources" + File.separatorChar + name + ".png", false));
    }
    private void saveDefaultBGResources() {
        List<String> list = Arrays.asList("b0", "b1", "b2", "b4", "b8", "b16","b32","b64","b128");
        list.forEach(name -> CustomNameplates.instance.saveResource("backgrounds" + File.separatorChar + name + ".png", false));
    }

    private void deleteDirectory(File file){
        if(file.exists()){
            try{
                FileUtils.deleteDirectory(file);
            }catch (IOException e){
                e.printStackTrace();
                AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to delete generated folder...</red>" );
            }
        }
    }

    private NameplateConfig getConfiguration(String nameplate) {
        try {
            File file = new File(CustomNameplates.instance.getDataFolder().getPath() + File.separator + "resources" + File.separator + nameplate + ".yml");
            if (!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    AdventureManager.consoleMessage("铭牌配置生成出错!");
                }
            }
            YamlConfiguration config = new YamlConfiguration();
            config.load(CustomNameplates.instance.getDataFolder().getPath() + File.separator + "resources" + File.separator + nameplate + ".yml");
            if (!config.contains("name")){
                config.set("name", nameplate);
            }
            if (!config.contains("color")){
                config.set("color","WHITE");
            }
            if (!config.contains("size")){
                config.set("size", 16);
            }
            if (!config.contains("yoffset")){
                config.set("yoffset", 12);
            }
            ChatColor color = ChatColor.WHITE;
            try {
                color = ChatColor.valueOf(Objects.requireNonNull(config.getString("color")).toUpperCase());
            }
            catch (IllegalArgumentException ex) {
                AdventureManager.consoleMessage("<red>[CustomNameplates] Invalid Color of " + nameplate + "</red>");
            }
            int size = config.getInt("size");
            int yoffset = config.getInt("yoffset");
            String name = config.getString("name");
            config.save(file);
            return new NameplateConfig(color, size, name, yoffset);
        }
        catch (Exception e) {
            return NameplateConfig.EMPTY;
        }
    }

    private List<JsonObject> getNegativeFontEnums() {
        ArrayList<JsonObject> list = new ArrayList<>();
        for (FontNegative negativeFont : FontNegative.values()) {
            list.add(this.getNegativeFontChar(negativeFont.getHeight(), negativeFont.getCharacter()));
        }
        return list;
    }

    private JsonObject getNegativeFontChar(int height, char character) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("type", new JsonPrimitive("bitmap"));
        jsonObject.add("file", new JsonPrimitive(ConfigManager.MainConfig.namespace + ":space_split.png"));
        jsonObject.add("ascent", new JsonPrimitive(-5000));
        jsonObject.add("height", new JsonPrimitive(height));
        final JsonArray jsonArray = new JsonArray();
        jsonArray.add(native2ascii(character));
        jsonObject.add("chars", jsonArray);
        return jsonObject;
    }

    public FontCache getNameplateInfo(String nameplate) {
        return caches.get(nameplate);
    }

    private String native2ascii(char ch) {
        if (ch > '\u007f') {
            StringBuilder stringBuilder_1 = new StringBuilder("\\u");
            StringBuilder stringBuilder_2 = new StringBuilder(Integer.toHexString(ch));
            stringBuilder_2.reverse();
            for (int n = 4 - stringBuilder_2.length(), i = 0; i < n; i++) {
                stringBuilder_2.append('0');
            }
            for (int j = 0; j < 4; j++) {
                stringBuilder_1.append(stringBuilder_2.charAt(3 - j));
            }
            return stringBuilder_1.toString();
        }
        return Character.toString(ch);
    }

    private List<JsonObject> getBackgrounds(BackGround backGround) {
        ArrayList<JsonObject> list = new ArrayList<>();
        int y_offset = backGround.getOffset_y();
        String name = backGround.getKey();
        HashMap<String, Character> chars = new HashMap<>();
        list.add(setBackgrounds(backGround.getStart(),y_offset,chars));
        list.add(setBackgrounds(backGround.getOffset_1(),y_offset,chars));
        list.add(setBackgrounds(backGround.getOffset_2(),y_offset,chars));
        list.add(setBackgrounds(backGround.getOffset_4(),y_offset,chars));
        list.add(setBackgrounds(backGround.getOffset_8(),y_offset,chars));
        list.add(setBackgrounds(backGround.getOffset_16(),y_offset,chars));
        list.add(setBackgrounds(backGround.getOffset_32(),y_offset,chars));
        list.add(setBackgrounds(backGround.getOffset_64(),y_offset,chars));
        list.add(setBackgrounds(backGround.getOffset_128(),y_offset,chars));
        list.add(setBackgrounds(backGround.getEnd(),y_offset,chars));
        bgCaches.put(name, chars);
        return list;
    }

    private JsonObject setBackgrounds(String name, int y_offset, HashMap<String, Character> chars){
        JsonObject jsonObject_2 = new JsonObject();
        jsonObject_2.add("type", new JsonPrimitive("bitmap"));
        jsonObject_2.add("file", new JsonPrimitive(ConfigManager.MainConfig.namespace + ":" + ConfigManager.MainConfig.bg_folder_path.replaceAll("\\\\","/") + name.toLowerCase() + ".png"));
        jsonObject_2.add("ascent", new JsonPrimitive(y_offset));
        jsonObject_2.add("height", new JsonPrimitive(14));
        JsonArray jsonArray_2 = new JsonArray();
        char character = start;
        jsonArray_2.add(native2ascii(character));
        jsonObject_2.add("chars", jsonArray_2);
        start = (char)(start + '\u0001');
        chars.put(name, character);
        try{
            FileUtils.copyFile(new File(CustomNameplates.instance.getDataFolder() + File.separator + "backgrounds" + File.separator + name + ".png"), new File(CustomNameplates.instance.getDataFolder() + File.separator + "generated" + File.separator + ConfigManager.MainConfig.namespace + File.separatorChar + "textures" + File.separator + ConfigManager.MainConfig.bg_folder_path + name + ".png"));
        }catch (IOException e){
            e.printStackTrace();
            AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to copy background png files to resource pack...</red>");
        }
        return jsonObject_2;
    }
}