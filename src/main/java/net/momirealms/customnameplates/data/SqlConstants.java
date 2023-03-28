package net.momirealms.customnameplates.data;

public class SqlConstants {

    public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `%s` ( `uuid` VARCHAR(36) NOT NULL , `equipped` VARCHAR(32) NOT NULL , `bubble` VARCHAR(32) NOT NULL , PRIMARY KEY (`uuid`) )";
    public static final String SQL_INSERT = "INSERT INTO `%s`(`uuid`, `equipped`, `bubble`) VALUES (?, ?, ?)";
    public static final String SQL_UPDATE_BY_UUID = "UPDATE `%s` SET `equipped` = ?, `bubble` = ? WHERE `uuid` = ?";
    public static final String SQL_SELECT_BY_UUID = "SELECT * FROM `%s` WHERE `uuid` = ?";
}
