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

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.api.CustomNameplatesAPI;
import net.momirealms.customnameplates.object.ConditionalText;
import net.momirealms.customnameplates.object.DisplayMode;
import net.momirealms.customnameplates.object.Function;
import net.momirealms.customnameplates.object.SimpleChar;
import net.momirealms.customnameplates.object.carrier.*;
import net.momirealms.customnameplates.object.font.OffsetFont;
import net.momirealms.customnameplates.object.nameplate.NameplateConfig;
import net.momirealms.customnameplates.object.requirements.Requirement;
import net.momirealms.customnameplates.utils.AdventureUtils;
import net.momirealms.customnameplates.utils.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NameplateManager extends Function {

    private String default_nameplate;
    private String player_prefix;
    private String player_suffix;
    private String player_name_papi;
    private long preview_time;
    private DisplayMode mode;
    private boolean fakeTeam;
    private final HashMap<String, NameplateConfig> nameplateConfigMap;
    private final CustomNameplates plugin;
    private AbstractTextCarrier textCarrier;
    protected HashMap<UUID, Long> previewCoolDown = new HashMap<>();
    private boolean hidePrefix;
    private boolean hideSuffix;

    public NameplateManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.nameplateConfigMap = new HashMap<>();
    }

    @Override
    public void load() {
        if (!ConfigManager.enableNameplates) return;
        YamlConfiguration config = ConfigUtils.getConfig("configs" + File.separator + "nameplate.yml");
        loadConfig(config);
        loadNameplates();
        loadMode(config);
    }

    @Override
    public void unload() {
        if (this.textCarrier != null) {
            this.textCarrier.unload();
            this.textCarrier = null;
        }
        this.nameplateConfigMap.clear();
    }

    private void loadConfig(ConfigurationSection config) {
        this.default_nameplate = config.getString("default-nameplate", "none");
        this.player_name_papi = config.getString("player-name", "%player_name%");
        this.preview_time = config.getLong("preview-duration", 5);
        this.fakeTeam = config.getBoolean("create-fake-team",true);
        this.player_prefix = config.getString("prefix","");
        this.player_suffix = config.getString("suffix","");
        this.hidePrefix = config.getBoolean("hide-prefix-when-equipping-nameplate", false);
        this.hideSuffix = config.getBoolean("hide-suffix-when-equipping-nameplate", false);
    }

    private void loadNameplates() {
        File np_file = new File(plugin.getDataFolder(), "contents" + File.separator + "nameplates");
        if (!np_file.exists() && np_file.mkdirs()) {
            saveDefaultNameplates();
        }
        File[] np_config_files = np_file.listFiles(file -> file.getName().endsWith(".yml"));
        if (np_config_files == null) return;
        Arrays.sort(np_config_files, Comparator.comparing(File::getName));
        for (File np_config_file : np_config_files) {
            char left = ConfigManager.start_char;
            char middle;
            char right;
            ConfigManager.start_char = (char) ((right = (char) ((middle = (char) (ConfigManager.start_char + '\u0001')) + '\u0001')) + '\u0001');
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
            try {
                config.save(np_config_file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SimpleChar leftChar = new SimpleChar(config.getInt("left.height"), config.getInt("left.ascent"), config.getInt("left.width"), left, config.getString("left.image") + ".png");
            SimpleChar middleChar = new SimpleChar(config.getInt("middle.height"), config.getInt("middle.ascent"), config.getInt("middle.width"), middle, config.getString("middle.image") + ".png");
            SimpleChar rightChar = new SimpleChar(config.getInt("right.height"), config.getInt("right.ascent"), config.getInt("right.width"), right, config.getString("right.image") + ".png");
            ChatColor color = ChatColor.valueOf(Objects.requireNonNull(config.getString("color", "WHITE")).toUpperCase(Locale.ENGLISH));
            nameplateConfigMap.put(key, new NameplateConfig(color, config.getString("display-name"), leftChar, middleChar, rightChar));
        }
        AdventureUtils.consoleMessage("[CustomNameplates] Loaded <green>" + nameplateConfigMap.size() + " <gray>nameplates");
    }

    private void saveDefaultNameplates() {
        String[] png_list = new String[]{"cat", "egg", "cheems", "wither", "xmas", "halloween", "hutao", "starsky", "trident", "rabbit"};
        String[] part_list = new String[]{"_left.png", "_middle.png", "_right.png", ".yml"};
        for (String name : png_list) {
            for (String part : part_list) {
                plugin.saveResource("contents" + File.separator + "nameplates" + File.separator + name + part, false);
            }
        }
    }

    private void loadMode(ConfigurationSection config) {
        this.mode = DisplayMode.valueOf(config.getString("mode","Team").toUpperCase(Locale.ENGLISH));
        if (mode == DisplayMode.TEAM) {
            this.textCarrier = new TeamInfoCarrier(plugin);
        } else if (mode == DisplayMode.ARMOR_STAND) {
            HashMap<ConditionalText, Double> contentMap = new HashMap<>();
            ConfigurationSection armorStandSection = config.getConfigurationSection("armor_stand");
            if (armorStandSection != null) {
                for (String key : armorStandSection.getKeys(false)) {
                    String text = armorStandSection.getString(key + ".text");
                    double offset = armorStandSection.getDouble(key + ".vertical-offset");
                    Requirement[] requirements = ConfigUtils.getRequirements(armorStandSection.getConfigurationSection(key + ".conditions"));
                    contentMap.put(new ConditionalText(requirements, text, null), offset);
                }
            }
            this.textCarrier = new NamedEntityCarrier(plugin, mode, contentMap);
        } else if (mode == DisplayMode.TEXT_DISPLAY) {
            HashMap<ConditionalText, Double> contentMap = new HashMap<>();
            ConfigurationSection textDisplaySection = config.getConfigurationSection("text_display");
            if (textDisplaySection != null) {
                for (String key :textDisplaySection.getKeys(false)) {
                    String text = textDisplaySection.getString(key + ".text");
                    double offset = textDisplaySection.getDouble(key + ".vertical-offset") + 1.2;
                    Requirement[] requirements = ConfigUtils.getRequirements(textDisplaySection.getConfigurationSection(key + ".conditions"));
                    TextDisplayMeta textDisplayMeta = ConfigUtils.getTextDisplayMeta(textDisplaySection.getConfigurationSection("options"));
                    contentMap.put(new ConditionalText(requirements, text, textDisplayMeta), offset);
                }
            }
            this.textCarrier = new NamedEntityCarrier(plugin, mode, contentMap);
        } else if (mode == DisplayMode.DISABLE) {
            this.textCarrier = new DisableNameplate(plugin);
        }
        plugin.getTeamManager().setTeamPacketInterface();
        if (this.textCarrier != null) {
            this.textCarrier.load();
        }
    }

    public String getNameplatePrefixWithFont(String text, NameplateConfig nameplate) {
        return ConfigManager.surroundWithFont(getNameplatePrefix(text, nameplate));
    }

    public String getNameplatePrefix(String text, NameplateConfig nameplate) {
        int totalWidth = plugin.getFontManager().getTotalPlayerNameWidth(text);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(plugin.getFontManager().getShortestNegChars(totalWidth % 2 == 0 ? totalWidth + nameplate.left().getWidth() : totalWidth + nameplate.left().getWidth() + 1))
                .append(nameplate.left().getChars()).append(OffsetFont.NEG_1.getCharacter());
        int mid_amount = (totalWidth - 1) / (nameplate.middle().getWidth());
        if (mid_amount == 0) {
            stringBuilder.append(nameplate.middle().getChars()).append(OffsetFont.NEG_1.getCharacter());
        }
        else {
            for (int i = 0; i < mid_amount; i++) {
                stringBuilder.append(nameplate.middle().getChars()).append(OffsetFont.NEG_1.getCharacter());
            }
            stringBuilder.append(
                    plugin.getFontManager().getShortestNegChars(
                            nameplate.middle().getWidth() - (totalWidth - 1) % nameplate.middle().getWidth()
                    )
            );
            stringBuilder.append(nameplate.middle().getChars()).append(OffsetFont.NEG_1.getCharacter());
        }
        stringBuilder.append(nameplate.right().getChars());
        stringBuilder.append(plugin.getFontManager().getShortestNegChars(totalWidth + nameplate.right().getWidth()));
        return stringBuilder.toString();
    }

    public List<String> getAvailableNameplates(Player player) {
        List<String> nameplates = new ArrayList<>();
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String permission = info.getPermission().toLowerCase();
            if (permission.startsWith("nameplates.equip.")) {
                permission = permission.substring(17);
                if (nameplateConfigMap.get(permission) != null) {
                    nameplates.add(permission);
                }
            }
        }
        return nameplates;
    }

    public boolean isInCoolDown(Player player) {
        long time = System.currentTimeMillis();
        if (time - (previewCoolDown.getOrDefault(player.getUniqueId(), time - this.getPreview_time() * 1050)) < this.getPreview_time() * 1050) {
            return true;
        }
        previewCoolDown.put(player.getUniqueId(), time);
        return false;
    }

    public void showPlayerArmorStandTags(Player player) {
        NamedEntityCarrier namedEntityCarrier = (NamedEntityCarrier) this.getTextCarrier();
        NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
        asm.spawn(player);
        for (int i = 1; i <= this.getPreview_time() * 20; i++) {
            plugin.getScheduler().runTaskAsyncLater(()-> {
                asm.teleport(player);
            }, i);
        }
        plugin.getScheduler().runTaskAsyncLater(()-> asm.destroy(player), this.getPreview_time() * 20);
    }

    public void showPlayerArmorStandTags(Player player, String nameplate) {
        String current = getEquippedNameplate(player);
        if (!nameplate.equals(current)) {
            plugin.getDataManager().equipNameplate(player, nameplate);
            CustomNameplatesAPI.getInstance().updateNameplateTeam(player);
            plugin.getScheduler().runTaskAsyncLater(()-> {
                plugin.getDataManager().equipNameplate(player, current);
                CustomNameplatesAPI.getInstance().updateNameplateTeam(player);
            },this.getPreview_time() * 20);
        }
        showPlayerArmorStandTags(player);
    }

    public boolean existNameplate(String nameplate) {
        return nameplateConfigMap.containsKey(nameplate);
    }

    public AbstractTextCarrier getTextCarrier() {
        return textCarrier;
    }

    public HashMap<String, NameplateConfig> getNameplateConfigMap() {
        return nameplateConfigMap;
    }

    public boolean isFakeTeam() {
        return fakeTeam;
    }

    public DisplayMode getMode() {
        return mode;
    }

    public String getEquippedNameplate(Player player) {
        return plugin.getDataManager().getEquippedNameplate(player);
    }

    public String getDefault_nameplate() {
        return default_nameplate;
    }

    public String getPrefix() {
        return player_prefix;
    }

    public String getSuffix() {
        return player_suffix;
    }

    public NameplateConfig getNameplateConfig(String nameplate) {
        return nameplateConfigMap.get(nameplate);
    }

    public String getPlayerNamePapi() {
        return player_name_papi;
    }

    public long getPreview_time() {
        return preview_time;
    }

    public boolean isPrefixHidden() {
        return hidePrefix;
    }

    public boolean isSuffixHidden() {
        return hideSuffix;
    }
}
