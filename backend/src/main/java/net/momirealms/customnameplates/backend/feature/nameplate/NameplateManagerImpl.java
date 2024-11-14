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

package net.momirealms.customnameplates.backend.feature.nameplate;

import dev.dejvokep.boostedyaml.YamlDocument;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;
import net.momirealms.customnameplates.api.feature.nameplate.Nameplate;
import net.momirealms.customnameplates.api.feature.nameplate.NameplateManager;
import net.momirealms.customnameplates.api.util.ConfigUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class NameplateManagerImpl implements NameplateManager {

    private final CustomNameplates plugin;
    private final Map<String, Nameplate> nameplates = new HashMap<>();
    private String defaultNameplateId;
    private String nameTag;

    public NameplateManagerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void unload() {
        this.nameplates.clear();
    }

    @Override
    public void load() {
        this.loadConfig();
        if (!ConfigManager.nameplateModule()) return;
        this.loadConfigs();
    }

    @Nullable
    @Override
    public Nameplate nameplateById(String id) {
        return this.nameplates.get(id);
    }

    @Override
    public Collection<Nameplate> nameplates() {
        return new ObjectArrayList<>(nameplates.values());
    }

    @Override
    public boolean hasNameplate(CNPlayer player, String id) {
        if (!this.nameplates.containsKey(id)) {
            return false;
        }
        return player.hasPermission("nameplates.equip." + id);
    }

    @Override
    public Collection<Nameplate> availableNameplates(CNPlayer player) {
        ArrayList<Nameplate> available = new ArrayList<>();
        for (Nameplate nameplate : nameplates.values()) {
            if (player.hasPermission("nameplates.equip." + nameplate.id())) {
                available.add(nameplate);
            }
        }
        return available;
    }

    @Override
    public String defaultNameplateId() {
        return defaultNameplateId;
    }

    @Override
    public String playerNameTag() {
        return nameTag;
    }

    private void loadConfig() {
        plugin.getConfigManager().saveResource("configs" + File.separator + "nameplate.yml");
        YamlDocument document = plugin.getConfigManager().loadData(new File(plugin.getDataDirectory().toFile(), "configs" + File.separator + "nameplate.yml"));
        defaultNameplateId = document.getString("default-nameplate", "none");
        String prefix = document.getString("nameplate.prefix", "");
        String name = document.getString("nameplate.player-name", "%player_name%");
        String suffix = document.getString("nameplate.suffix", "");
        nameTag = prefix + name + suffix;
    }

    private void loadConfigs() {
        File nameplateFolder = new File(plugin.getDataDirectory().toFile(), "contents" + File.separator + "nameplates");
        if (!nameplateFolder.exists() && nameplateFolder.mkdirs()) {
            saveDefaultNameplates();
        }
        List<File> configFiles = ConfigUtils.getConfigsDeeply(nameplateFolder);
        for (File configFile : configFiles) {
            YamlDocument config = plugin.getConfigManager().loadData(configFile);
            String id = configFile.getName().substring(0, configFile.getName().lastIndexOf("."));
            Nameplate nameplate = Nameplate.builder()
                    .id(id)
                    .displayName(config.getString("display-name", id))
                    .minWidth(config.getInt("min-width", 0))
                    .left(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("left.image") + ".png"),
                            config.getInt("left.ascent", 12),
                            config.getInt("left.height", 16)
                    ))
                    .middle(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("middle.image") + ".png"),
                            config.getInt("middle.ascent", 12),
                            config.getInt("middle.height", 16)
                    ))
                    .right(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("right.image") + ".png"),
                            config.getInt("right.ascent", 12),
                            config.getInt("right.height", 16)
                    ))
                    .build();
            this.nameplates.put(id, nameplate);
        }
    }

    private void saveDefaultNameplates() {
        String[] png_list = new String[]{"egg", "xmas", "halloween", "hutao", "starsky", "trident", "rabbit", "sign", "game", "enter", "panda", "angel", "banner"};
        String[] part_list = new String[]{"_left.png", "_middle.png", "_right.png", ".yml"};
        for (String name : png_list) {
            for (String part : part_list) {
                plugin.getConfigManager().saveResource("contents" + File.separator + "nameplates" + File.separator + name + part);
            }
        }
    }
}
