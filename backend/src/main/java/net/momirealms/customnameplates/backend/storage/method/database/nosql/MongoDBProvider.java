package net.momirealms.customnameplates.backend.storage.method.database.nosql;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.backend.storage.method.AbstractStorage;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.bson.types.Binary;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MongoDBProvider extends AbstractStorage {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private String collectionPrefix;

    public MongoDBProvider(CustomNameplates plugin) {
        super(plugin);
    }

    @Override
    public void initialize(YamlDocument config) {
        Section section = config.getSection("MongoDB");
        if (section == null) {
            plugin.getPluginLogger().warn("Failed to load database config. It seems that your config is broken. Please regenerate a new one.");
            return;
        }

        collectionPrefix = section.getString("collection-prefix", "nameplates");
        var settings = MongoClientSettings.builder().uuidRepresentation(UuidRepresentation.STANDARD);
        if (!section.getString("connection-uri", "").equals("")) {
            settings.applyConnectionString(new ConnectionString(section.getString("connection-uri", "")));
            this.mongoClient = MongoClients.create(settings.build());
            this.database = mongoClient.getDatabase(section.getString("database", "minecraft"));
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

    @Override
    public void disable() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
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
    public StorageType storageType() {
        return StorageType.MONGODB;
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid, Executor executor) {
        CompletableFuture<Optional<PlayerData>> future = new CompletableFuture<>();
        if (executor == null) executor = plugin.getScheduler().async();
        executor.execute(() -> {
            MongoCollection<Document> collection = database.getCollection(getCollectionName("data"));
            Document doc = collection.find(Filters.eq("uuid", uuid)).first();
            if (doc == null) {
                future.complete(Optional.empty());
            } else if (plugin.getPlayer(uuid) != null) {
                Binary binary = (Binary) doc.get("data");
                future.complete(Optional.of(plugin.getStorageManager().fromBytes(uuid, binary.getData())));
            } else {
                future.complete(Optional.empty());
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerData(PlayerData playerData, Executor executor) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (executor == null) executor = plugin.getScheduler().async();
        executor.execute(() -> {
            MongoCollection<Document> collection = database.getCollection(getCollectionName("data"));
            try {
                Document query = new Document("uuid", playerData.uuid());
                Bson updates = Updates.combine(Updates.set("data", new Binary(plugin.getStorageManager().toBytes(playerData))));
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
    public Set<UUID> getUniqueUsers() {
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
            plugin.getPluginLogger().warn("Failed to get unique data.", e);
        }
        return uuids;
    }
}
