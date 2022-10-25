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

package net.momirealms.customnameplates.objects.data;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.ChatBubblesManager;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileStorageImpl implements DataStorageInterface {

    @Override
    public void initialize() {

    }

    @Override
    public void disable() {

    }

    @Override
    public PlayerData loadData(OfflinePlayer player) {
        YamlConfiguration data = ConfigUtil.readData(new File(CustomNameplates.plugin.getDataFolder(), "player_data" + File.separator + player.getUniqueId() + ".yml"));
        String bubbles = data.getString("bubbles", ChatBubblesManager.defaultBubble);
        String nameplate = data.getString("nameplate", NameplateManager.defaultNameplate);
        PlayerData playerData = new PlayerData(player, nameplate, bubbles);
        saveData(playerData);
        return playerData;
    }

    @Override
    public void saveData(PlayerData playerData) {
        YamlConfiguration data = new YamlConfiguration();
        data.set("bubbles", playerData.getBubbles());
        data.set("nameplate", playerData.getEquippedNameplate());
        try {
            data.save(new File(CustomNameplates.plugin.getDataFolder(), "player_data" + File.separator + playerData.getPlayer().getUniqueId() + ".yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
