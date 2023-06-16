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

package net.momirealms.customnameplates.data;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.ConfigManager;
import net.momirealms.customnameplates.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileStorageImpl implements DataStorageInterface {

    private final CustomNameplates plugin;

    public FileStorageImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        //empty
    }

    @Override
    public void disable() {
        //empty
    }

    @Override
    public PlayerData loadData(UUID uuid) {
        YamlConfiguration data = ConfigUtils.readData(new File(plugin.getDataFolder(), "player_data" + File.separator + uuid + ".yml"));
        String bubbles = data.getString("bubbles", ConfigManager.enableBubbles ? plugin.getChatBubblesManager().getDefaultBubble() : "none");
        String nameplate = data.getString("nameplate", ConfigManager.enableNameplates ? plugin.getNameplateManager().getDefault_nameplate() : "none");
        PlayerData playerData = new PlayerData(uuid, nameplate, bubbles);
        saveData(playerData);
        return playerData;
    }

    @Override
    public void saveData(PlayerData playerData) {
        YamlConfiguration data = new YamlConfiguration();
        data.set("bubbles", playerData.getBubble());
        data.set("nameplate", playerData.getNameplate());
        try {
            data.save(new File(plugin.getDataFolder(), "player_data" + File.separator + playerData.getUuid() + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.YAML;
    }
}
