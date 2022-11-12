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

import net.momirealms.customnameplates.manager.ChatBubblesManager;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLStorageImpl implements DataStorageInterface {

    public static SqlConnection sqlConnection = new SqlConnection();

    @Override
    public void initialize() {
        sqlConnection.createNewHikariConfiguration();
        createTableIfNotExist(sqlConnection.getTable_name());
    }

    @Override
    public void disable() {
        sqlConnection.close();
    }

    @Override
    public PlayerData loadData(OfflinePlayer player) {
        PlayerData playerData = null;
        try {
            Connection connection = sqlConnection.getConnectionAndCheck();
            if (connection == null) return new PlayerData(player, NameplateManager.defaultNameplate, ChatBubblesManager.defaultBubble);
            String sql = String.format(SqlConstants.SQL_SELECT_BY_UUID, sqlConnection.getTable_name());
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, player.getUniqueId().toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String nameplate = rs.getString(2);
                String bubbles = rs.getString(3);
                playerData = new PlayerData(player, nameplate, bubbles);
            }
            else {
                playerData = new PlayerData(player, NameplateManager.defaultNameplate, ChatBubblesManager.defaultBubble);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerData;
    }

    @Override
    public void saveData(PlayerData playerData) {
        UUID uuid = playerData.getPlayer().getUniqueId();
        if (exists(uuid)) {
            updateData(uuid, playerData.getEquippedNameplate(), playerData.getBubbles());
        }
        else {
            insertData(uuid, playerData.getEquippedNameplate(), playerData.getBubbles());
        }
    }

    private void createTableIfNotExist(String table) {
        String sql = String.format(SqlConstants.SQL_CREATE_TABLE, table);
        try {
            Connection connection = sqlConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            connection.close();
        } catch (SQLException ex) {
            AdventureUtil.consoleMessage("[CustomNameplates] Failed to create table");
        }
    }

    private void insertData(UUID uuid, String nameplate, String bubbles) {
        String sql = String.format(SqlConstants.SQL_INSERT, sqlConnection.getTable_name());
        try {
            Connection connection = sqlConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, uuid.toString());
            statement.setString(2, nameplate);
            statement.setString(3, bubbles);
            statement.executeUpdate();
            connection.close();
        } catch (SQLException ex) {
            AdventureUtil.consoleMessage("[CustomNameplates] Failed to insert data for " + uuid);
        }
    }

    private void updateData(UUID uuid, String nameplate, String bubbles) {
        String sql = String.format(SqlConstants.SQL_UPDATE_BY_UUID, sqlConnection.getTable_name());
        try {
            Connection connection = sqlConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nameplate);
            statement.setString(2, bubbles);
            statement.setString(3, uuid.toString());
            statement.executeUpdate();
            connection.close();
        } catch (SQLException ex) {
            AdventureUtil.consoleMessage("[CustomNameplates] Failed to update data for " + uuid);
        }
    }

    public boolean exists(UUID uuid) {
        String sql = String.format(SqlConstants.SQL_SELECT_BY_UUID, sqlConnection.getTable_name());
        boolean exist;
        try {
            Connection connection = sqlConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            exist = rs.next();
            connection.close();
        } catch (SQLException ex) {
            AdventureUtil.consoleMessage("[CustomNameplates] Failed to select data for " + uuid);
            return false;
        }
        return exist;
    }
}
