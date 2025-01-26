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

package net.momirealms.customnameplates.api.storage;

/**
 * Enum representing the various types of storage systems supported for data persistence.
 * <p>
 * Each enum constant corresponds to a different type of storage backend that can be used
 * for saving and retrieving data. This includes file-based storage (JSON, YAML), relational
 * databases (H2, SQLite, MySQL, MariaDB), NoSQL databases (MongoDB), and caching systems (Redis).
 * </p>
 */
public enum StorageType {

	/**
	 * Represents storage in JSON format.
	 * This format is typically used for lightweight data storage and easy serialization.
	 */
	JSON,

	/**
	 * Represents storage in YAML format.
	 * YAML is often used for configuration files and is human-readable.
	 */
	YAML,

	/**
	 * Represents storage in an H2 database.
	 * H2 is an in-memory SQL database that can also be used in file-based mode.
	 */
	H2,

	/**
	 * Represents storage in an SQLite database.
	 * SQLite is a serverless, self-contained SQL database engine.
	 */
	SQLITE,

	/**
	 * Represents storage in a MySQL database.
	 * MySQL is a widely used relational database management system (RDBMS).
	 */
	MYSQL,

	/**
	 * Represents storage in a MariaDB database.
	 * MariaDB is a community-developed, open-source RDBMS that is a fork of MySQL.
	 */
	MARIADB,

	/**
	 * Represents storage in a MongoDB database.
	 * MongoDB is a NoSQL database that stores data in flexible, JSON-like documents.
	 */
	MONGODB,

	/**
	 * Represents storage in Redis.
	 * Redis is an in-memory data structure store, used as a database, cache, and message broker.
	 */
	Redis,

	/**
	 * Represents the absence of a storage type.
	 * This is used when no storage system is configured or required.
	 */
	NONE
}
