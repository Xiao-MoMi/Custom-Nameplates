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

package net.momirealms.customnameplates.paper.storage.method.database.nosql;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import net.momirealms.customnameplates.api.data.PlayerData;
import net.momirealms.customnameplates.api.data.StorageType;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.CustomNameplatesPluginImpl;
import net.momirealms.customnameplates.paper.storage.method.AbstractStorage;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * An implementation of AbstractStorage that uses MongoDB for player data storage.
 */
public class MongoDBImpl extends AbstractStorage {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private String collectionPrefix;

    public MongoDBImpl(CustomNameplatesPluginImpl plugin) {
        super(plugin);
    }

    /**
     * Initialize the MongoDB connection and configuration based on the plugin's YAML configuration.
     */
    @Override
    public void initialize() {
        YamlConfiguration config = plugin.getConfig("database.yml");
        ConfigurationSection section = config.getConfigurationSection("MongoDB");
        if (section == null) {
            LogUtils.warn("Failed to load database config. It seems that your config is broken. Please regenerate a new one.");
            return;
        }

        collectionPrefix = section.getString("collection-prefix", "nameplates");
        var settings = MongoClientSettings.builder().uuidRepresentation(UuidRepresentation.STANDARD);
        if (!section.getString("connection-uri", "").equals("")) {
            settings.applyConnectionString(new ConnectionString(section.getString("connection-uri", "")));
            mongoClient = MongoClients.create(settings.build());
            return;
        }

        if (section.contains("user")) {
            MongoCredential credential = MongoCredential.createCredential(
                    section.getString("user", "root"),
                    section.getString("database", "minecraft"),
                    section.getString("password", "password").toCharArray()
            );
            settings.credential(credential);
        }

        settings.applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(
                section.getString("host", "localhost"),
                section.getInt("port", 27017)
        ))));
        this.mongoClient = MongoClients.create(settings.build());
        this.database = mongoClient.getDatabase(section.getString("database", "minecraft"));
    }

    /**
     * Disable the MongoDB connection by closing the MongoClient.
     */
    @Override
    public void disable() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerData(UUID uuid, PlayerData playerData) {
        var future = new CompletableFuture<Boolean>();
        plugin.getScheduler().runTaskAsync(() -> {
        MongoCollection<Document> collection = database.getCollection(getCollectionName("data"));
        try {
            Document query = new Document("uuid", uuid);
            Bson updates = Updates.combine(
                    Updates.set(
                            "data",
                            new Binary(plugin.getStorageManager().toBytes(playerData))
                    )
            );
            UpdateOptions options = new UpdateOptions().upsert(true);
            UpdateResult result = collection.updateOne(query, updates, options);
            future.complete(result.wasAcknowledged());
        } catch (MongoException e) {
            future.completeExceptionally(e);
        }
        });
        return future;
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid) {
        var future = new CompletableFuture<Optional<PlayerData>>();
        plugin.getScheduler().runTaskAsync(() -> {
        MongoCollection<Document> collection = database.getCollection(getCollectionName("data"));
        Document doc = collection.find(Filters.eq("uuid", uuid)).first();
        if (doc == null) {
            future.complete(Optional.empty());
        } else if (Bukkit.getPlayer(uuid) != null) {
            Binary binary = (Binary) doc.get("data");
            future.complete(Optional.of(plugin.getStorageManager().fromBytes(binary.getData())));
        } else {
            future.complete(Optional.empty());
        }
        });
        return future;
    }

    /**
     * Get a set of unique player UUIDs from the MongoDB database.
     *
     * @param legacy Flag indicating whether to retrieve legacy data.
     * @return A set of unique player UUIDs.
     */
    @Override
    public Set<UUID> getUniqueUsers(boolean legacy) {
        // no legacy files
        Set<UUID> uuids = new HashSet<>();
        MongoCollection<Document> collection = database.getCollection(getCollectionName("data"));
        try {
            Bson projectionFields = Projections.fields(Projections.include("uuid"));
            try (MongoCursor<Document> cursor = collection.find().projection(projectionFields).iterator()) {
                while (cursor.hasNext()) {
                    uuids.add(cursor.next().get("uuid", UUID.class));
                }
            }
        } catch (MongoException e) {
            LogUtils.warn("Failed to get unique data.", e);
        }
        return uuids;
    }

    /**
     * Get the collection name for a specific subcategory of data.
     *
     * @param value The subcategory identifier.
     * @return The full collection name including the prefix.
     */
    public String getCollectionName(String value) {
        return getCollectionPrefix() + "_" + value;
    }

    /**
     * Get the collection prefix used for MongoDB collections.
     *
     * @return The collection prefix.
     */
    public String getCollectionPrefix() {
        return collectionPrefix;
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.MongoDB;
    }
}
