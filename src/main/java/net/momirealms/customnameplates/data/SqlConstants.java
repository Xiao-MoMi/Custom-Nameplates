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

public class SqlConstants {

    public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `%s` ( `uuid` VARCHAR(36) NOT NULL , `equipped` VARCHAR(32) NOT NULL , `bubble` VARCHAR(32) NOT NULL , PRIMARY KEY (`uuid`) )";
    public static final String SQL_INSERT = "INSERT INTO `%s`(`uuid`, `equipped`, `bubble`) VALUES (?, ?, ?)";
    public static final String SQL_UPDATE_BY_UUID = "UPDATE `%s` SET `equipped` = ?, `bubble` = ? WHERE `uuid` = ?";
    public static final String SQL_SELECT_BY_UUID = "SELECT * FROM `%s` WHERE `uuid` = ?";
}
