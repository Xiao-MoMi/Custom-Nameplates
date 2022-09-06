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
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.objects.BackGround;
import net.momirealms.customnameplates.nameplates.NameplateInstance;
import net.momirealms.customnameplates.font.FontChar;
import net.momirealms.customnameplates.font.FontNegative;
import net.momirealms.customnameplates.nameplates.NameplateConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static net.momirealms.customnameplates.ConfigManager.MainConfig.start;

public class ResourceManager {

    public static HashMap<String, NameplateInstance> NAMEPLATES = new HashMap<>();
    public static HashMap<String, HashMap<String, Character>> BACKGROUNDS = new HashMap<>();

    public void generateResourcePack() {

        File r_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "nameplates");
        File b_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "backgrounds");
        File g_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "ResourcePack");

        if (!r_file.exists()) {
            if (!r_file.mkdir()) {
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to create resources folder...</red>");
                return;
            }
            saveDefaultResources();
        }

        if (!b_file.exists()) {
            if (!b_file.mkdir()) {
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to create resources folder...</red>");
                return;
            }
            saveDefaultBGResources();
        }

        File[] pngFiles = r_file.listFiles(file -> file.getName().endsWith(".png"));

        if (pngFiles == null) {
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! No png files detected in resource folder...</red>");
            return;
        }

        Arrays.sort(pngFiles);
        deleteDirectory(g_file);

        File f_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "ResourcePack" + File.separatorChar + ConfigManager.MainConfig.namespace + File.separatorChar + "font");
        File t_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "ResourcePack" + File.separatorChar + ConfigManager.MainConfig.namespace + File.separatorChar + "textures");

        if (!f_file.mkdirs() || !t_file.mkdirs()) {
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to generate resource pack folders...</red>");
            return;
        }

        if (ConfigManager.MainConfig.offsets != null){
            ConfigManager.MainConfig.offsets.forEach(offset -> {
                JsonObject jsonObject_offset = new JsonObject();
                JsonArray jsonArray_offset = new JsonArray();
                jsonObject_offset.add("providers", jsonArray_offset);
                JsonObject jsonObject_3 = new JsonObject();
                jsonObject_3.add("type", new JsonPrimitive("space"));
                JsonObject jsonObject_4 = new JsonObject();
                jsonObject_4.add(" ", new JsonPrimitive(4));
                jsonObject_4.add("\\u200c", new JsonPrimitive(0));
                jsonObject_3.add("advances", jsonObject_4);
                jsonArray_offset.add(jsonObject_3);
                JsonObject jsonObject_2 = new JsonObject();
                jsonObject_2.add("type", new JsonPrimitive("bitmap"));
                jsonObject_2.add("file", new JsonPrimitive("minecraft:font/ascii.png"));
                jsonObject_2.add("ascent", new JsonPrimitive(offset));
                jsonObject_2.add("height", new JsonPrimitive(8));
                JsonArray jsonArray_2 = new JsonArray();
                jsonArray_2.add("\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000");
                jsonArray_2.add("\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000");
                jsonArray_2.add("\\u0020\\u0021\\u0022\\u0023\\u0024\\u0025\\u0026\\u0027\\u0028\\u0029\\u002a\\u002b\\u002c\\u002d\\u002e\\u002f");
                jsonArray_2.add("\\u0030\\u0031\\u0032\\u0033\\u0034\\u0035\\u0036\\u0037\\u0038\\u0039\\u003a\\u003b\\u003c\\u003d\\u003e\\u003f");
                jsonArray_2.add("\\u0040\\u0041\\u0042\\u0043\\u0044\\u0045\\u0046\\u0047\\u0048\\u0049\\u004a\\u004b\\u004c\\u004d\\u004e\\u004f");
                jsonArray_2.add("\\u0050\\u0051\\u0052\\u0053\\u0054\\u0055\\u0056\\u0057\\u0058\\u0059\\u005a\\u005b\\u005c\\u005d\\u005e\\u005f");
                jsonArray_2.add("\\u0060\\u0061\\u0062\\u0063\\u0064\\u0065\\u0066\\u0067\\u0068\\u0069\\u006a\\u006b\\u006c\\u006d\\u006e\\u006f");
                jsonArray_2.add("\\u0070\\u0071\\u0072\\u0073\\u0074\\u0075\\u0076\\u0077\\u0078\\u0079\\u007a\\u007b\\u007c\\u007d\\u007e\\u0000");
                jsonArray_2.add("\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000");
                jsonArray_2.add("\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u00a3\\u0000\\u0000\\u0192");
                jsonArray_2.add("\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u00aa\\u00ba\\u0000\\u0000\\u00ac\\u0000\\u0000\\u0000\\u00ab\\u00bb");
                jsonArray_2.add("\\u2591\\u2592\\u2593\\u2502\\u2524\\u2561\\u2562\\u2556\\u2555\\u2563\\u2551\\u2557\\u255d\\u255c\\u255b\\u2510");
                jsonArray_2.add("\\u2514\\u2534\\u252c\\u251c\\u2500\\u253c\\u255e\\u255f\\u255a\\u2554\\u2569\\u2566\\u2560\\u2550\\u256c\\u2567");
                jsonArray_2.add("\\u2568\\u2564\\u2565\\u2559\\u2558\\u2552\\u2553\\u256b\\u256a\\u2518\\u250c\\u2588\\u2584\\u258c\\u2590\\u2580");
                jsonArray_2.add("\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u2205\\u2208\\u0000");
                jsonArray_2.add("\\u2261\\u00b1\\u2265\\u2264\\u2320\\u2321\\u00f7\\u2248\\u00b0\\u2219\\u0000\\u221a\\u207f\\u00b2\\u25a0\\u0000");
                jsonObject_2.add("chars", jsonArray_2);
                jsonArray_offset.add(jsonObject_2);

                try (FileWriter fileWriter = new FileWriter(
                        CustomNameplates.instance.getDataFolder() +
                                File.separator + "ResourcePack" +
                                File.separator + ConfigManager.MainConfig.namespace +
                                File.separator + "font" +
                                File.separator + "offset_" + offset + ".json")) {
                    fileWriter.write(jsonObject_offset.toString().replace("\\\\", "\\"));
                }
                catch (IOException e) {
                    AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to generate offset font json...</red>");
                }
            });
        }

        JsonObject jsonObject_1 = new JsonObject();
        JsonArray jsonArray_1 = new JsonArray();
        jsonObject_1.add("providers", jsonArray_1);
        getNegativeFontEnums().forEach(jsonArray_1::add);
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
                NAMEPLATES.put(pngName, new NameplateInstance(pngName, fontChar, config));
                jsonObject_2.add("type", new JsonPrimitive("bitmap"));
                jsonObject_2.add("file", new JsonPrimitive(ConfigManager.MainConfig.namespace + ":" + ConfigManager.MainConfig.folder_path.replaceAll("\\\\","/") + png.getName().toLowerCase()));
                jsonObject_2.add("ascent", new JsonPrimitive(config.getYOffset()));
                jsonObject_2.add("height", new JsonPrimitive(config.getHeight()));
                JsonArray jsonArray_2 = new JsonArray();
                jsonArray_2.add(native2ascii(fontChar.getLeft()) + native2ascii(fontChar.getMiddle()) + native2ascii(fontChar.getRight()));
                jsonObject_2.add("chars", jsonArray_2);
                jsonArray_1.add(jsonObject_2);
                try{
                    FileUtils.copyFile(png, new File(t_file.getPath() + File.separatorChar + StringUtils.replace(ConfigManager.MainConfig.folder_path, "\\", String.valueOf(File.separatorChar)) + png.getName()));
                }catch (IOException e){
                    AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to copy png files to resource pack...</red>");
                }
            }
            NAMEPLATES.put("none", NameplateInstance.EMPTY);
        }
        if (ConfigManager.background){
            ConfigManager.backgrounds.forEach((key, backGround) -> {
                getBackgrounds(backGround).forEach(jsonArray_1::add);
            });
        }

        if (ConfigManager.MainConfig.extract) {
            String path = "ResourcePack" + File.separator + "minecraft" + File.separator + "shaders" + File.separator + "core" + File.separator;
            CustomNameplates.instance.saveResource(path + "rendertype_text.fsh", true);
            CustomNameplates.instance.saveResource(path + "rendertype_text.json", true);
            CustomNameplates.instance.saveResource(path + "rendertype_text.vsh", true);
            CustomNameplates.instance.saveResource(path + "rendertype_text_see_through.fsh", true);
            CustomNameplates.instance.saveResource(path + "rendertype_text_see_through.json", true);
            CustomNameplates.instance.saveResource(path + "rendertype_text_see_through.vsh", true);
        }

        try{
            CustomNameplates.instance.saveResource("space_split.png", false);
            FileUtils.copyFile(new File(CustomNameplates.instance.getDataFolder(),"space_split.png"), new File(t_file.getPath() + File.separator + StringUtils.replace(ConfigManager.MainConfig.ss_folder_path, "\\", String.valueOf(File.separatorChar)) + "space_split.png"));
            new File(CustomNameplates.instance.getDataFolder(),"space_split.png").delete();
        }catch (IOException e){
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to copy space_split.png to resource pack...</red>");
        }

        try (FileWriter fileWriter = new FileWriter(
                CustomNameplates.instance.getDataFolder() +
                        File.separator + "ResourcePack" +
                        File.separator + ConfigManager.MainConfig.namespace +
                        File.separator + "font" +
                        File.separator + ConfigManager.MainConfig.font + ".json"))
        {
            fileWriter.write(jsonObject_1.toString().replace("\\\\", "\\"));
        } catch (IOException e) {
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to generate font json...</red>");
        }

        AdventureUtil.consoleMessage("[CustomNameplates] ResourcePack has been generated!");
        AdventureUtil.consoleMessage("[CustomNameplates] Loaded <green>" + (NAMEPLATES.size() -1) + " <gray>nameplates");

        if (ConfigManager.MainConfig.itemsAdder){
            try{
                FileUtils.copyDirectory(g_file, new File(Bukkit.getPluginManager().getPlugin("ItemsAdder").getDataFolder() + File.separator + "data"+ File.separator + "resource_pack" + File.separator + "assets") );
                AdventureUtil.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>Detected <color:#90EE90>ItemsAdder!<color:#baffd1> Automatically sent rp to ItemsAdder folder!");
            }catch (IOException e){
                e.printStackTrace();
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to copy files to ItemsAdder...</red>");
            }
        }
        if (ConfigManager.MainConfig.oraxen){
            try{
                FileUtils.copyDirectory(g_file, new File(Bukkit.getPluginManager().getPlugin("Oraxen").getDataFolder() + File.separator + "pack"+ File.separator + "assets"));
                AdventureUtil.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>Detected <color:#90EE90>Oraxen!<color:#baffd1> Automatically sent rp to Oraxen folder!");
            }catch (IOException e){
                e.printStackTrace();
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to copy files to Oraxen...</red>");
            }
        }
    }

    private void saveDefaultResources() {
        List<String> list = Arrays.asList("cat", "egg", "cheems", "wither", "xmas", "halloween","hutao","starsky","trident","rabbit");
        list.forEach(name -> CustomNameplates.instance.saveResource("nameplates" + File.separatorChar + name + ".png", false));
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
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to delete generated folder...</red>" );
            }
        }
    }

    private NameplateConfig getConfiguration(String nameplate) {
        try {
            File file = new File(CustomNameplates.instance.getDataFolder().getPath() + File.separator + "nameplates" + File.separator + nameplate + ".yml");
            if (!file.exists()){
                try {
                    file.createNewFile();
                }
                catch (IOException e) {
                    AdventureUtil.consoleMessage("<red>[CustomNameplates] Error occurred when generating default nameplate config!");
                }
            }
            YamlConfiguration config = new YamlConfiguration();
            config.load(CustomNameplates.instance.getDataFolder().getPath() + File.separator + "nameplates" + File.separator + nameplate + ".yml");
            if (!config.contains("name")) config.set("name", nameplate);
            if (!config.contains("color")) config.set("color","WHITE");
            if (!config.contains("size")) config.set("size", 16);
            if (!config.contains("yoffset")) config.set("yoffset", 12);
            ChatColor color = ChatColor.valueOf(Objects.requireNonNull(config.getString("color","WHITE")).toUpperCase());
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
        jsonObject.add("file", new JsonPrimitive(ConfigManager.MainConfig.namespace + ":" + ConfigManager.MainConfig.ss_folder_path.replaceAll("\\\\","/") + "space_split.png"));
        jsonObject.add("ascent", new JsonPrimitive(-5000));
        jsonObject.add("height", new JsonPrimitive(height));
        final JsonArray jsonArray = new JsonArray();
        jsonArray.add(native2ascii(character));
        jsonObject.add("chars", jsonArray);
        return jsonObject;
    }

    public NameplateInstance getNameplateInstance(String nameplate) {
        return NAMEPLATES.get(nameplate);
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

    private List<JsonObject> getBackgrounds(BackGround backGround) {
        ArrayList<JsonObject> list = new ArrayList<>();
        int y_offset = backGround.getOffset_y();
        int size = backGround.getSize();
        String name = backGround.getKey();
        HashMap<String, Character> chars = new HashMap<>();
        list.add(setBackgrounds(backGround.getStart(),y_offset,size,chars));
        list.add(setBackgrounds(backGround.getOffset_1(),y_offset,size,chars));
        list.add(setBackgrounds(backGround.getOffset_2(),y_offset,size,chars));
        list.add(setBackgrounds(backGround.getOffset_4(),y_offset,size,chars));
        list.add(setBackgrounds(backGround.getOffset_8(),y_offset,size,chars));
        list.add(setBackgrounds(backGround.getOffset_16(),y_offset,size,chars));
        list.add(setBackgrounds(backGround.getOffset_32(),y_offset,size,chars));
        list.add(setBackgrounds(backGround.getOffset_64(),y_offset,size,chars));
        list.add(setBackgrounds(backGround.getOffset_128(),y_offset,size,chars));
        list.add(setBackgrounds(backGround.getEnd(),y_offset,size,chars));
        BACKGROUNDS.put(name, chars);
        return list;
    }

    private JsonObject setBackgrounds(String name, int y_offset, int size, HashMap<String, Character> chars){
        JsonObject jsonObject_2 = new JsonObject();
        jsonObject_2.add("type", new JsonPrimitive("bitmap"));
        jsonObject_2.add("file", new JsonPrimitive(ConfigManager.MainConfig.namespace + ":" + ConfigManager.MainConfig.bg_folder_path.replaceAll("\\\\","/") + name.toLowerCase() + ".png"));
        jsonObject_2.add("ascent", new JsonPrimitive(y_offset));
        jsonObject_2.add("height", new JsonPrimitive(size));
        JsonArray jsonArray_2 = new JsonArray();
        char character = start;
        jsonArray_2.add(native2ascii(character));
        jsonObject_2.add("chars", jsonArray_2);
        start = (char)(start + '\u0001');
        chars.put(name, character);
        try {
            FileUtils.copyFile(new File(CustomNameplates.instance.getDataFolder() + File.separator + "backgrounds" + File.separator + name + ".png"), new File(CustomNameplates.instance.getDataFolder() + File.separator + "ResourcePack" + File.separator + ConfigManager.MainConfig.namespace + File.separatorChar + "textures" + File.separatorChar + StringUtils.replace(ConfigManager.MainConfig.bg_folder_path, "\\", String.valueOf(File.separatorChar)) + name + ".png"));
        }
        catch (IOException e){
            e.printStackTrace();
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to copy background png files to resource pack...</red>");
        }
        return jsonObject_2;
    }
}