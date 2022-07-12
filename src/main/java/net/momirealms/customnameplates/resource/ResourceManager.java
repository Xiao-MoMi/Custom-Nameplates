package net.momirealms.customnameplates.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.AdventureManager;
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

public class ResourceManager {

    public final HashMap<String, FontCache> caches;
    private final CustomNameplates plugin;

    public ResourceManager(CustomNameplates plugin) {
        this.caches = new HashMap<>();
        this.plugin = plugin;
    }

    /*
    此方法用于生成资源包
    */
    public void generateResourcePack() {

        File r_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "resources");
        File g_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "generated");
        //如果资源文件夹不存在则创建
        if (!r_file.exists()) {
            AdventureManager.consoleMessage("<gradient:#DDE4FF:#8DA2EE>[CustomNameplates]</gradient> <color:#F5F5F5>Failed to detect resources folder! Generating default resources...");
            if (!r_file.mkdir()) {
                AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to create resources folder...</red>");
                return;
            }
            saveDefaultResources();
        }
        //获取资源文件夹下的所有png文件
        File[] pngFiles = r_file.listFiles(file -> file.getName().endsWith(".png"));
        if (pngFiles == null) {
            AdventureManager.consoleMessage("<red>[CustomNameplates] Error! No png files detected in resource folder...</red>");
            return;
        }
        Arrays.sort(pngFiles); //将png文件按照首字母进行排序
        deleteDirectory(g_file); //删除文件夹以重置自动生成的资源

        File f_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "generated" + File.separatorChar + ConfigManager.MainConfig.namespace + File.separatorChar + "font");
        File t_file = new File(CustomNameplates.instance.getDataFolder() + File.separator + "generated" + File.separatorChar + ConfigManager.MainConfig.namespace + File.separatorChar + "textures");

        if (!f_file.mkdirs() || !t_file.mkdirs()) {
            AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to generate resource pack folders...</red>");
            return;
        }
        char start = ConfigManager.MainConfig.start_char.charAt(0); //获取起始字符
        JsonObject jsonObject_1 = new JsonObject(); //新建json对象
        JsonArray jsonArray_1 = new JsonArray();
        jsonObject_1.add("providers", jsonArray_1);
        for (File png : pngFiles) {
            JsonObject jsonObject_2 = new JsonObject();
            char left = start;
            char middle;
            char right;
            start = (char)((right = (char)((middle = (char)(start + '\u0001')) + '\u0001')) + '\u0001'); //依次+1
            FontChar fontChar = new FontChar(left, middle, right);
            String pngName = png.getName().substring(0, png.getName().length() - 4); //删除.png后缀
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
        CustomNameplates.instance.saveResource("space_split.png", false); //复制space_split.png
        try{
            FileUtils.copyFile(new File(CustomNameplates.instance.getDataFolder(),"space_split.png"), new File(t_file.getPath()  + File.separatorChar + ConfigManager.MainConfig.folder_path + "space_split.png"));
        }catch (IOException e){
            e.printStackTrace();
            AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to copy space_split.png to resource pack...</red>");
            return;
        }
        new File(CustomNameplates.instance.getDataFolder(),"space_split.png").delete(); //删除拷贝出的默认文件
        this.getNegativeFontEnums().forEach(jsonArray_1::add); //添加负空格
        //存储default.json
        try (FileWriter fileWriter = new FileWriter(f_file.getPath() + File.separatorChar + ConfigManager.MainConfig.font + ".json")) {
            fileWriter.write(jsonObject_1.toString().replace("\\\\", "\\"));
        } catch (IOException e) {
            e.printStackTrace();
            AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to generate font json...</red>");
            return;
        }
        //资源包生成成功提示
        AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>资源包已成功生成! 已载入 <white>" + (this.caches.size() -1) + " <color:#baffd1>个铭牌!");
        if (this.plugin.getHookManager().hasItemsAdder()){
            try{
                FileUtils.copyDirectory(g_file, new File(Bukkit.getPluginManager().getPlugin("ItemsAdder").getDataFolder() + File.separator + "data"+ File.separator + "resource_pack" + File.separator + "assets") );
                AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>检测到 <color:#90EE90>ItemsAdder <color:#baffd1>已自动转移生成的资源包!");
            }catch (IOException e){
                e.printStackTrace();
                AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to copy files to ItemsAdder...</red>");
            }
        }
    }

    /*
    保存插件预设资源
    */
    private void saveDefaultResources() {
        List<String> list = Arrays.asList("cat", "egg", "cheems", "wither", "xmas", "halloween","hutao","starsky","trident","rabbit");
        list.forEach(name -> CustomNameplates.instance.saveResource("resources" + File.separatorChar + name + ".png", false));
    }

    /*
    删除文件夹
    */
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

    /*
    获取铭牌的config
    */
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

    /*
    获取负空格并返回list
    */
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
        jsonObject.add("file", new JsonPrimitive(ConfigManager.MainConfig.namespace + ":" + ConfigManager.MainConfig.folder_path.replaceAll("\\\\","/") +"space_split.png"));
        jsonObject.add("ascent", new JsonPrimitive(-5000));
        jsonObject.add("height", new JsonPrimitive(height));
        final JsonArray jsonArray = new JsonArray();
        jsonArray.add(native2ascii(character));
        jsonObject.add("chars", jsonArray);
        return jsonObject;
    }

    /*
    根据铭牌名获取铭牌的FontCache
     */
    public FontCache getNameplateInfo(String nameplate) {
        return caches.get(nameplate);
    }

    /*
    字符转换
     */
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
}