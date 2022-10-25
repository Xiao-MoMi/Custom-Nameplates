package net.momirealms.customnameplates.manager;

import codecrafter47.bungeetablistplus.config.MainConfig;
import net.momirealms.customnameplates.objects.Function;
import net.momirealms.customnameplates.objects.SimpleChar;
import net.momirealms.customnameplates.objects.font.FontOffset;
import net.momirealms.customnameplates.objects.font.FontUtil;
import net.momirealms.customnameplates.objects.nameplates.BubbleConfig;
import net.momirealms.customnameplates.objects.nameplates.NameplateConfig;
import net.momirealms.customnameplates.objects.nameplates.NameplateMode;
import net.momirealms.customnameplates.objects.nameplates.mode.rd.RidingTag;
import net.momirealms.customnameplates.objects.nameplates.mode.tm.TeamTag;
import net.momirealms.customnameplates.objects.nameplates.mode.tp.TeleportingTag;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class NameplateManager extends Function {

    public static String defaultNameplate;
    public static String player_prefix;
    public static String player_suffix;
    public static String player_name;
    public static long preview;
    public static boolean update;
    public static int refresh;
    public static String mode;
    public static boolean hidePrefix;
    public static boolean hideSuffix;
    public static boolean tryHook;
    public static boolean removeTag;
    public static boolean smallSize;
    public static boolean fakeTeam;
    private final HashMap<String, Double> textMap = new HashMap<>();
    private NameplateMode nameplateMode;
    private final TeamManager teamManager;

    public NameplateManager() {
        this.teamManager = new TeamManager();
    }

    @Override
    public void load() {
        YamlConfiguration config = ConfigUtil.getConfig("nameplate.yml");
        defaultNameplate = config.getString("nameplate.default-nameplate");

        if (!ConfigUtil.isModuleEnabled("nameplate")) return;

        player_name = config.getString("nameplate.player-name", "%player_name%");
        preview = config.getLong("nameplate.preview-duration");
        mode = config.getString("nameplate.mode","team");
        update = config.getBoolean("nameplate.update.enable",true);
        fakeTeam = config.getBoolean("nameplate.create-fake-team",true);
        refresh = config.getInt("nameplate.update.ticks",20);
        player_prefix = config.getString("nameplate.prefix","");
        player_suffix = config.getString("nameplate.suffix","");
        hidePrefix = config.getBoolean("nameplate.team.hide-prefix-when-equipped",true);
        hideSuffix = config.getBoolean("nameplate.team.hide-suffix-when-equipped",true);

        if (ConfigManager.tab_hook || ConfigManager.tab_BC_hook) fakeTeam = true;
        teamManager.load();

        if (mode.equalsIgnoreCase("team")) {
            removeTag = false;
            nameplateMode = new TeamTag(teamManager);
        }
        else if (mode.equalsIgnoreCase("riding")) {
            tryHook = config.getBoolean("nameplate.riding.try-to-hook-cosmetics-plugin", false);
            List<String> texts = config.getStringList("nameplate.riding.text");
            textMap.clear();
            for (String text : texts) {
                textMap.put(text, -0.1);
            }
            smallSize = config.getBoolean("nameplate.riding.small-height", true);
            removeTag = config.getBoolean("nameplate.riding.remove-nametag");
            nameplateMode = new RidingTag(teamManager);
        }
        else if (mode.equalsIgnoreCase("teleporting")) {
            removeTag = config.getBoolean("nameplate.teleporting.remove-nametag");
            smallSize = config.getBoolean("nameplate.teleporting.small-height", true);
            textMap.clear();
            for (String key : config.getConfigurationSection("nameplate.teleporting.text").getKeys(false)) {
                textMap.put(config.getString("nameplate.teleporting.text." + key + ".content"), config.getDouble("nameplate.teleporting.text." + key + ".offset"));
            }
            nameplateMode = new TeleportingTag(teamManager);
        }
        nameplateMode.load();
    }

    @Override
    public void unload() {
        if (nameplateMode != null) nameplateMode.unload();
        teamManager.unload();
    }

    public HashMap<String, Double> getTextMap() {
        return textMap;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }


    public String makeCustomNameplate(String prefix, String name, String suffix, NameplateConfig nameplate) {
        int totalWidth = FontUtil.getTotalWidth(ChatColor.stripColor(prefix + name + suffix));
        char middle = nameplate.middle().getChars();
        char neg_1 = FontOffset.NEG_1.getCharacter();
        int offset_2 = nameplate.right().getWidth() - nameplate.middle().getWidth();
        int left_offset = totalWidth + (nameplate.left().getWidth() + nameplate.right().getWidth())/2 + 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FontOffset.getShortestNegChars(totalWidth % 2 == 0 ? left_offset : left_offset + 1 ));
        stringBuilder.append(nameplate.left().getChars()).append(neg_1);
        int mid_amount = (totalWidth + 1 + offset_2) / (nameplate.middle().getWidth());
        if (mid_amount == 0) {
            stringBuilder.append(middle).append(neg_1);
        }
        else {
            for (int i = 0; i < mid_amount; i++) {
                stringBuilder.append(middle).append(neg_1);
            }
        }
        return getString(totalWidth, middle, neg_1, offset_2, left_offset, stringBuilder, nameplate.right(), nameplate.middle());
    }

    public String getSuffixChar(String name) {
        int totalWidth = FontUtil.getTotalWidth(ChatColor.stripColor(name));
        return FontOffset.getShortestNegChars(totalWidth + totalWidth % 2 + 1);
    }

    public String makeCustomBubble(String prefix, String name, String suffix, BubbleConfig bubble) {
        int totalWidth = FontUtil.getTotalWidth(ChatColor.stripColor(prefix + name + suffix));
        char middle = bubble.middle().getChars();
        char tail = bubble.tail().getChars();
        char neg_1 = FontOffset.NEG_1.getCharacter();
        int offset = bubble.middle().getWidth() - bubble.tail().getWidth();
        int left_offset = totalWidth + bubble.left().getWidth() + 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FontOffset.getShortestNegChars(totalWidth % 2 == 0 ? left_offset - offset : left_offset + 1 - offset));
        stringBuilder.append(bubble.left().getChars()).append(neg_1);
        int mid_amount = (totalWidth + 1 - bubble.tail().getWidth()) / (bubble.middle().getWidth());
        if (mid_amount == 0) {
            stringBuilder.append(tail).append(neg_1);
        }
        else {
            for (int i = 0; i <= mid_amount; i++) {
                if (i == mid_amount/2) {
                    stringBuilder.append(tail).append(neg_1);
                }
                else {
                    stringBuilder.append(middle).append(neg_1);
                }
            }
        }
        return getString(totalWidth, middle, neg_1, offset, left_offset, stringBuilder, bubble.right(), bubble.middle());
    }

    @NotNull
    protected String getString(int totalWidth, char middle, char neg_1, int offset, int left_offset, StringBuilder stringBuilder, SimpleChar right, SimpleChar middle2) {
        stringBuilder.append(FontOffset.getShortestNegChars(right.getWidth() - ((totalWidth + 1 + offset) % middle2.getWidth() + (totalWidth % 2 == 0 ? 0 : -1))));
        stringBuilder.append(middle).append(neg_1);
        stringBuilder.append(right.getChars()).append(neg_1);
        stringBuilder.append(FontOffset.getShortestNegChars(left_offset - 1));
        return stringBuilder.toString();
    }

    public NameplateMode getNameplateMode() {
        return nameplateMode;
    }
}
