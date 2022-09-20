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
import net.momirealms.customnameplates.helper.Log;
import net.momirealms.customnameplates.nameplates.BubbleConfig;
import net.momirealms.customnameplates.objects.SimpleChar;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.objects.BackGround;
import net.momirealms.customnameplates.font.FontOffset;
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

import static net.momirealms.customnameplates.ConfigManager.Main.start;

public class ResourceManager {

    public static HashMap<String, NameplateConfig> NAMEPLATES;
    public static HashMap<String, BubbleConfig> BUBBLES;
    public static HashMap<String, BackGround> BACKGROUNDS;


    //生成资源包并载入新的配置
    public void generateResourcePack() {

        //铭牌
        File np_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "nameplates");
        //背景
        File bg_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "backgrounds");
        //气泡
        File bb_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "bubbles");
        //生成的
        File gd_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "ResourcePack");

        //清除之前的旧文件
        deleteDirectory(gd_file);

        File font_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "ResourcePack" + File.separatorChar + ConfigManager.Main.namespace + File.separatorChar + "font");
        File textures_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "ResourcePack" + File.separatorChar + ConfigManager.Main.namespace + File.separatorChar + "textures");

        if (!font_file.mkdirs() || !textures_file.mkdirs()) {
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to generate resource pack folders...</red>");
            return;
        }

        //保存偏移字符图
        saveSplit(textures_file.getPath());

        //新建Json
        JsonObject jsonObject_default = new JsonObject();
        JsonArray jsonArray_default = new JsonArray();
        jsonObject_default.add("providers", jsonArray_default);

        //获取偏移字符
        getOffsetFontEnums().forEach(jsonArray_default::add);

        //铭牌模块
        if (ConfigManager.Module.nameplate) {

            //保存默认的文件
            if (!np_file.exists()) {
                if (!np_file.mkdir()) {
                    AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to create resources folder...</red>");
                    return;
                }
                saveDefaultNameplates();
            }

            File[] np_config_files = np_file.listFiles(file -> file.getName().endsWith(".yml"));
            //滚
            if (np_config_files == null) return;
            //排序
            Arrays.sort(np_config_files);

            if (ConfigManager.Module.nameplate){

                NAMEPLATES = new HashMap<>();
                NAMEPLATES.put("none", NameplateConfig.EMPTY);

                for (File np_config_file : np_config_files) {

                    //初始化当前三个字符的unicode
                    char left = start;
                    char middle;
                    char right;
                    start = (char)((right = (char)((middle = (char)(start + '\u0001')) + '\u0001')) + '\u0001');

                    try {

                        String key = np_config_file.getName().substring(0, np_config_file.getName().length() - 4);

                        YamlConfiguration config = YamlConfiguration.loadConfiguration(np_config_file);
                        if (!config.contains("display-name")) config.set("display-name", key);
                        if (!config.contains("name-color")) config.set("name-color", "white");
                        if (!config.contains("left.image")) config.set("left.image", key + "_left");
                        if (!config.contains("left.height")) config.set("left.height", 16);
                        if (!config.contains("left.ascent")) config.set("left.ascent", 12);
                        if (!config.contains("left.width")) config.set("left.width", 16);
                        if (!config.contains("middle.image")) config.set("middle.image", key + "_middle");
                        if (!config.contains("middle.height")) config.set("middle.height", 16);
                        if (!config.contains("middle.ascent")) config.set("middle.ascent", 12);
                        if (!config.contains("middle.width")) config.set("middle.width", 16);
                        if (!config.contains("right.image")) config.set("right.image", key + "_right");
                        if (!config.contains("right.height")) config.set("right.height", 16);
                        if (!config.contains("right.ascent")) config.set("right.ascent", 12);
                        if (!config.contains("right.width")) config.set("right.width", 16);

                        SimpleChar leftChar = new SimpleChar(config.getInt("left.height"), config.getInt("left.ascent"), config.getInt("left.width"), left, config.getString("left.image") + ".png");
                        SimpleChar middleChar = new SimpleChar(config.getInt("middle.height"), config.getInt("middle.ascent"), config.getInt("middle.width"), middle, config.getString("middle.image") + ".png");
                        SimpleChar rightChar = new SimpleChar(config.getInt("right.height"), config.getInt("right.ascent"), config.getInt("right.width"), right, config.getString("right.image") + ".png");

                        ChatColor color = ChatColor.valueOf(Objects.requireNonNull(config.getString("color","WHITE")).toUpperCase());

                        config.save(np_config_file);

                        NameplateConfig nameplateConfig = new NameplateConfig(color, config.getString("display-name"), leftChar, middleChar, rightChar);

                        NAMEPLATES.put(key, nameplateConfig);

                        SimpleChar[] simpleChars = new SimpleChar[]{leftChar, middleChar, rightChar};

                        for (SimpleChar simpleChar : simpleChars) {

                            JsonObject jo_np = new JsonObject();

                            jo_np.add("type", new JsonPrimitive("bitmap"));
                            jo_np.add("file", new JsonPrimitive(ConfigManager.Main.namespace + ":" + ConfigManager.Main.np_folder_path.replaceAll("\\\\","/") + simpleChar.getFile()));
                            jo_np.add("ascent", new JsonPrimitive(simpleChar.getAscent()));
                            jo_np.add("height", new JsonPrimitive(simpleChar.getHeight()));
                            JsonArray ja_simple = new JsonArray();
                            ja_simple.add(native2ascii(simpleChar.getChars()));
                            jo_np.add("chars", ja_simple);
                            jsonArray_default.add(jo_np);

                            try {
                                FileUtils.copyFile(new File(np_config_file.getParent() + File.separator + simpleChar.getFile()), new File(textures_file.getPath() + File.separatorChar + StringUtils.replace(ConfigManager.Main.np_folder_path, "\\", File.separator) + simpleChar.getFile()));
                            }
                            catch (IOException e){
                                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to copy nameplate " + key + " png files to resource pack...</red>");
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //气泡模块
        if (ConfigManager.Module.bubbles) {

            if (!bb_file.exists()) {
                if (!bb_file.mkdir()) {
                    AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to create resources folder...</red>");
                    return;
                }
                saveDefaultBubbles();
            }

            BUBBLES = new HashMap<>();

            File[] bb_config_files = bb_file.listFiles(file -> file.getName().endsWith(".yml"));
            if (bb_config_files == null) return;
            Arrays.sort(bb_config_files);

            for (File bb_config_file : bb_config_files) {

                char left = start;
                char middle;
                char right;
                char tail;
                start = (char) ((tail = (char)((right = (char)((middle = (char)(start + '\u0001')) + '\u0001')) + '\u0001')) + '\u0001');

                try {

                    String key = bb_config_file.getName().substring(0, bb_config_file.getName().length() - 4);

                    YamlConfiguration config = YamlConfiguration.loadConfiguration(bb_config_file);
                    if (!config.contains("display-name")) config.set("display-name", key);
                    if (!config.contains("text-format")) config.set("text-format", "<white>");
                    if (!config.contains("left.image")) config.set("left.image", key + "_left");
                    if (!config.contains("left.height")) config.set("left.height", 16);
                    if (!config.contains("left.width")) config.set("left.width", 16);
                    if (!config.contains("left.ascent")) config.set("left.ascent", 12);
                    if (!config.contains("middle.image")) config.set("middle.image", key + "_middle");
                    if (!config.contains("middle.height")) config.set("middle.height", 16);
                    if (!config.contains("middle.width")) config.set("middle.width", 16);
                    if (!config.contains("middle.ascent")) config.set("middle.ascent", 12);
                    if (!config.contains("right.image")) config.set("right.image", key + "_right");
                    if (!config.contains("right.height")) config.set("right.height", 16);
                    if (!config.contains("right.width")) config.set("right.width", 16);
                    if (!config.contains("right.ascent")) config.set("right.ascent", 12);
                    if (!config.contains("tail.image")) config.set("tail.image", key + "_tail");
                    if (!config.contains("tail.height")) config.set("tail.height", 16);
                    if (!config.contains("tail.width")) config.set("tail.width", 16);
                    if (!config.contains("tail.ascent")) config.set("tail.ascent", 12);

                    SimpleChar leftChar = new SimpleChar(config.getInt("left.height"), config.getInt("left.ascent"), config.getInt("left.width"), left, config.getString("left.image") + ".png");
                    SimpleChar middleChar = new SimpleChar(config.getInt("middle.height"), config.getInt("middle.ascent"), config.getInt("middle.width"), middle, config.getString("middle.image") + ".png");
                    SimpleChar rightChar = new SimpleChar(config.getInt("right.height"), config.getInt("right.ascent"), config.getInt("right.width"), right, config.getString("right.image") + ".png");
                    SimpleChar tailChar = new SimpleChar(config.getInt("tail.height"), config.getInt("tail.ascent"), config.getInt("tail.width"), tail, config.getString("tail.image") + ".png");

                    config.save(bb_config_file);

                    BubbleConfig bubbleConfig = new BubbleConfig(config.getString("text-format"), config.getString("display-name"),
                                                                                    leftChar, middleChar,
                                                                                    rightChar, tailChar);

                    BUBBLES.put(key, bubbleConfig);

                    SimpleChar[] simpleChars = new SimpleChar[]{leftChar, middleChar, rightChar, tailChar};

                    for (SimpleChar simpleChar : simpleChars) {

                        JsonObject jo_bb = new JsonObject();

                        jo_bb.add("type", new JsonPrimitive("bitmap"));
                        jo_bb.add("file", new JsonPrimitive(ConfigManager.Main.namespace + ":" + ConfigManager.Main.bb_folder_path.replaceAll("\\\\","/") + simpleChar.getFile()));
                        jo_bb.add("ascent", new JsonPrimitive(simpleChar.getAscent()));
                        jo_bb.add("height", new JsonPrimitive(simpleChar.getHeight()));
                        JsonArray ja_simple = new JsonArray();
                        ja_simple.add(native2ascii(simpleChar.getChars()));
                        jo_bb.add("chars", ja_simple);
                        jsonArray_default.add(jo_bb);

                        try {
                            FileUtils.copyFile(new File(bb_config_file.getParent() + File.separator + simpleChar.getFile()), new File(textures_file.getPath() + File.separatorChar + StringUtils.replace(ConfigManager.Main.bb_folder_path, "\\", File.separator) + simpleChar.getFile()));
                        }
                        catch (IOException e){
                            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to copy bubble " + key + " png files to resource pack...</red>");
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //背景模块
        if (ConfigManager.Module.background) {

            if (!bg_file.exists()) {
                if (!bg_file.mkdir()) {
                    AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to create resources folder...</red>");
                    return;
                }
                saveDefaultBackgrounds();
            }

            BACKGROUNDS = new HashMap<>();

            File[] bg_config_files = bg_file.listFiles(file -> file.getName().endsWith(".yml"));
            if (bg_config_files == null) return;
            Arrays.sort(bg_config_files);

            for (File bg_config_file : bg_config_files) {

                try {

                    String key = bg_config_file.getName().substring(0, bg_config_file.getName().length() - 4);
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(bg_config_file);

                    char oStart = start;
                    char o1 = (char) (oStart + '\u0001');
                    char o2 = (char) (o1 + '\u0001');
                    char o4 = (char) (o2 + '\u0001');
                    char o8 = (char) (o4 + '\u0001');
                    char o16 = (char) (o8 + '\u0001');
                    char o32 = (char) (o16 + '\u0001');
                    char o64 = (char) (o32 + '\u0001');
                    char o128 = (char) (o64 + '\u0001');
                    char oEnd = (char) (o128 + '\u0001');
                    start = (char) (oEnd + '\u0001');

                    int height = config.getInt("middle.height", 14);
                    int ascent = config.getInt("middle.ascent", 8);
                    SimpleChar startChar = new SimpleChar(config.getInt("left.height"), config.getInt("left.ascent"),1 ,oStart, config.getString("left.image") + ".png");
                    SimpleChar offset_1 = new SimpleChar(height, ascent,1, o1, config.getString("middle.1") + ".png");
                    SimpleChar offset_2 = new SimpleChar(height, ascent,1, o2, config.getString("middle.2") + ".png");
                    SimpleChar offset_4 = new SimpleChar(height, ascent,1, o4, config.getString("middle.4") + ".png");
                    SimpleChar offset_8 = new SimpleChar(height, ascent,1, o8, config.getString("middle.8") + ".png");
                    SimpleChar offset_16 = new SimpleChar(height, ascent,1, o16, config.getString("middle.16") + ".png");
                    SimpleChar offset_32 = new SimpleChar(height, ascent,1, o32, config.getString("middle.32") + ".png");
                    SimpleChar offset_64 = new SimpleChar(height, ascent,1, o64, config.getString("middle.64") + ".png");
                    SimpleChar offset_128 = new SimpleChar(height, ascent,1, o128, config.getString("middle.128") + ".png");
                    SimpleChar endChar = new SimpleChar(config.getInt("right.height"),config.getInt("right.ascent"),1, oEnd, config.getString("right.image") + ".png");
                    BackGround backGround = new BackGround(startChar, offset_1,
                                                            offset_2, offset_4,
                                                            offset_8, offset_16,
                                                            offset_32, offset_64,
                                                            offset_128, endChar,
                                                            config.getInt("start-width", 1), config.getInt("end-width", 1));

                    BACKGROUNDS.put(key, backGround);

                    SimpleChar[] simpleChars = new SimpleChar[]{startChar, offset_1,
                                                                offset_2, offset_4,
                                                                offset_8, offset_16,
                                                                offset_32, offset_64,
                                                                offset_128, endChar};

                    for (SimpleChar simpleChar : simpleChars) {

                        JsonObject jo_bg = new JsonObject();

                        jo_bg.add("type", new JsonPrimitive("bitmap"));
                        jo_bg.add("file", new JsonPrimitive(ConfigManager.Main.namespace + ":" + ConfigManager.Main.bg_folder_path.replaceAll("\\\\","/") + simpleChar.getFile()));
                        jo_bg.add("ascent", new JsonPrimitive(simpleChar.getAscent()));
                        jo_bg.add("height", new JsonPrimitive(simpleChar.getHeight()));
                        JsonArray ja_simple = new JsonArray();
                        ja_simple.add(native2ascii(simpleChar.getChars()));
                        jo_bg.add("chars", ja_simple);
                        jsonArray_default.add(jo_bg);

                        try {
                            FileUtils.copyFile(new File(bg_config_file.getParent() + File.separator + simpleChar.getFile()), new File(textures_file.getPath() + File.separatorChar + StringUtils.replace(ConfigManager.Main.bg_folder_path, "\\", File.separator) + simpleChar.getFile()));
                        }
                        catch (IOException e){
                            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to copy background " + key + " png files to resource pack...</red>");
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //存储默认shader
        if (ConfigManager.Main.extract) {
            String path = "ResourcePack" + File.separator + "minecraft" + File.separator + "shaders" + File.separator + "core" + File.separator;
            CustomNameplates.instance.saveResource(path + "rendertype_text.fsh", true);
            CustomNameplates.instance.saveResource(path + "rendertype_text.json", true);
            CustomNameplates.instance.saveResource(path + "rendertype_text.vsh", true);
            CustomNameplates.instance.saveResource(path + "rendertype_text_see_through.fsh", true);
            CustomNameplates.instance.saveResource(path + "rendertype_text_see_through.json", true);
            CustomNameplates.instance.saveResource(path + "rendertype_text_see_through.vsh", true);
        }

        //载入多个偏移
        if (ConfigManager.Main.offsets.size() != 0){

            for (int offset : ConfigManager.Main.offsets) {
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
                jo_ascii.add("ascent", new JsonPrimitive(offset));
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

                try (FileWriter fileWriter = new FileWriter(
                        CustomNameplates.instance.getDataFolder() +
                                File.separator + "ResourcePack" +
                                File.separator + ConfigManager.Main.namespace +
                                File.separator + "font" +
                                File.separator + "offset_" + offset + ".json")) {
                    fileWriter.write(jsonObject_offset.toString().replace("\\\\", "\\"));
                }
                catch (IOException e) {
                    AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to generate offset font json...</red>");
                }
            }
        }

        try (FileWriter fileWriter = new FileWriter(
                CustomNameplates.instance.getDataFolder() +
                        File.separator + "ResourcePack" +
                        File.separator + ConfigManager.Main.namespace +
                        File.separator + "font" +
                        File.separator + ConfigManager.Main.font + ".json"))
        {
            fileWriter.write(jsonObject_default.toString().replace("\\\\", "\\"));
        }
        catch (IOException e) {
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to generate font json...</red>");
        }

        if (NAMEPLATES != null) AdventureUtil.consoleMessage("[CustomNameplates] Loaded <green>" + (NAMEPLATES.size() -1) + " <gray>nameplates");
        if (BUBBLES != null) AdventureUtil.consoleMessage("[CustomNameplates] Loaded <green>" + BUBBLES.size() + " <gray>bubbles");
        if (BACKGROUNDS != null) AdventureUtil.consoleMessage("[CustomNameplates] Loaded <green>" + BACKGROUNDS.size() + " <gray>backgrounds");
        //复制到其他插件文件夹中
        hookCopy(gd_file);
    }

    private void saveDefaultNameplates() {
        String[] png_list = new String[]{"cat", "egg", "cheems", "wither", "xmas", "halloween", "hutao", "starsky", "trident", "rabbit"};
        String[] part_list = new String[]{"_left.png", "_middle.png", "_right.png", ".yml"};
        for (String name : png_list) {
            for (String part : part_list) {
                CustomNameplates.instance.saveResource("nameplates" + File.separatorChar + name + part, false);
            }
        }
    }

    private void saveDefaultBubbles() {
        String[] png_list = new String[]{"chat"};
        String[] part_list = new String[]{"_left.png", "_middle.png", "_right.png", "_tail.png", ".yml"};
        for (String name : png_list) {
            for (String part : part_list) {
                CustomNameplates.instance.saveResource("bubbles" + File.separatorChar + name + part, false);
            }
        }
    }

    private void saveDefaultBackgrounds() {
        String[] bg_list = new String[]{"b0", "b1", "b2", "b4", "b8", "b16","b32","b64","b128"};
        for (String bg : bg_list) {
            CustomNameplates.instance.saveResource("backgrounds" + File.separatorChar + bg + ".png", false);
        }
        String[] config_list = new String[]{"bedrock_1", "bedrock_2", "bedrock_3"};
        for (String config : config_list) {
            CustomNameplates.instance.saveResource("backgrounds" + File.separatorChar + config + ".yml", false);
        }
    }

    private void deleteDirectory(File file){
        if(file.exists()){
            try{
                FileUtils.deleteDirectory(file);
            }
            catch (IOException e){
                e.printStackTrace();
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to delete generated folder...</red>" );
            }
        }
    }

    private List<JsonObject> getOffsetFontEnums() {
        ArrayList<JsonObject> list = new ArrayList<>();
        for (FontOffset offsetFont : FontOffset.values()) {
            list.add(this.getOffsetFontChar(offsetFont.getHeight(), offsetFont.getCharacter()));
        }
        return list;
    }

    private JsonObject getOffsetFontChar(int height, char character) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("type", new JsonPrimitive("bitmap"));
        jsonObject.add("file", new JsonPrimitive(ConfigManager.Main.namespace + ":" + ConfigManager.Main.ss_folder_path.replaceAll("\\\\","/") + "space_split.png"));
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

    private void saveSplit(String path) {
        try{
            CustomNameplates.instance.saveResource("space_split.png", false);
            FileUtils.copyFile(new File(CustomNameplates.instance.getDataFolder(),"space_split.png"), new File(path + File.separator + StringUtils.replace(ConfigManager.Main.ss_folder_path, "\\", File.separator) + "space_split.png"));
            new File(CustomNameplates.instance.getDataFolder(),"space_split.png").delete();
        }catch (IOException e){
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to copy space_split.png to resource pack...</red>");
        }
    }

    private void hookCopy(File generated) {
        if (ConfigManager.Main.itemsAdder){
            try {
                FileUtils.copyDirectory(generated, new File(Bukkit.getPluginManager().getPlugin("ItemsAdder").getDataFolder() + File.separator + "data"+ File.separator + "resource_pack" + File.separator + "assets") );
            }
            catch (IOException e){
                e.printStackTrace();
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to copy files to ItemsAdder...</red>");
            }
        }
        if (ConfigManager.Main.oraxen){
            try {
                FileUtils.copyDirectory(new File(generated, File.separator + ConfigManager.Main.namespace), new File(Bukkit.getPluginManager().getPlugin("Oraxen").getDataFolder() + File.separator + "pack"+ File.separator + "assets" + File.separator + ConfigManager.Main.namespace));
            }
            catch (IOException e){
                e.printStackTrace();
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to copy files to Oraxen...</red>");
            }
        }
    }

    public NameplateConfig getNameplateConfig(String nameplate) {
        return NAMEPLATES.get(nameplate);
    }

    public BubbleConfig getBubbleConfig(String bubble) {
        return BUBBLES.get(bubble);
    }

    public BackGround getBackground(String background) {
        return BACKGROUNDS.get(background);
    }
}