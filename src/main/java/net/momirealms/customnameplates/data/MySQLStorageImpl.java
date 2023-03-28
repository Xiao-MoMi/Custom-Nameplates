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
import net.momirealms.customnameplates.utils.AdventureUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLStorageImpl implements DataStorageInterface {

    private final SqlConnection sqlConnection;
    private final CustomNameplates plugin;

    public MySQLStorageImpl(CustomNameplates plugin) {
        this.plugin = plugin;
        this.sqlConnection = new SqlConnection(this);
    }

    @Override
    public void initialize() {
        sqlConnection.createNewHikariConfiguration();
        createTableIfNotExist(sqlConnection.getTable());
    }

    @Override
    public void disable() {
        sqlConnection.close();
    }

    @Override
    public PlayerData loadData(UUID uuid) {
        PlayerData playerData = null;
        String sql = String.format(SqlConstants.SQL_SELECT_BY_UUID, sqlConnection.getTable());
        try (Connection connection = sqlConnection.getConnectionAndCheck(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String nameplate = rs.getString(2);
                String bubbles = rs.getString(3);
                playerData = new PlayerData(uuid, nameplate, bubbles);
            }
            else {
                playerData = new PlayerData(uuid,
                        ConfigManager.enableNameplates ? plugin.getNameplateManager().getDefault_nameplate() : "none",
                        ConfigManager.enableBubbles ? plugin.getChatBubblesManager().getDefaultBubble() : "none");
                insertData(uuid, playerData.getNameplate(), playerData.getBubble());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerData;
    }

    @Override
    public void saveData(PlayerData playerData) {
        String sql = String.format(SqlConstants.SQL_UPDATE_BY_UUID, sqlConnection.getTable());
        try (Connection connection = sqlConnection.getConnectionAndCheck(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerData.getNameplate());
            statement.setString(2, playerData.getBubble());
            statement.setString(3, playerData.getUuid().toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            AdventureUtils.consoleMessage("[CustomNameplates] Failed to update data for " + playerData.getUuid());
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.SQL;
    }

    private void createTableIfNotExist(String table) {
        String sql = String.format(SqlConstants.SQL_CREATE_TABLE, table);
        try (Connection connection = sqlConnection.getConnectionAndCheck(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            AdventureUtils.consoleMessage("[CustomNameplates] Failed to create table");
        }
    }

    private void insertData(UUID uuid, String nameplate, String bubbles) {
        String sql = String.format(SqlConstants.SQL_INSERT, sqlConnection.getTable());
        try (Connection connection = sqlConnection.getConnectionAndCheck(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.setString(2, nameplate);
            statement.setString(3, bubbles);
            statement.executeUpdate();
        } catch (SQLException ex) {
            AdventureUtils.consoleMessage("[CustomNameplates] Failed to insert data for " + uuid);
        }
    }
}