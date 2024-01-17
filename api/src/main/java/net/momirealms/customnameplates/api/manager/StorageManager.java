package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.data.OnlineUser;
import net.momirealms.customnameplates.api.data.PlayerData;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageManager {

    /**
     * Get online users
     *
     * @return online users
     */
    Collection<OnlineUser> getOnlineUsers();

    /**
     * Get a player's data by uuid
     * The player can be an offline one
     *
     * @param uuid uuid
     * @return player data
     */
    CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid);

    /**
     * Save online players' data
     *
     * @param uuid uuid
     * @return success or not
     */
    CompletableFuture<Boolean> saveOnlinePlayerData(UUID uuid);

    /**
     * Save specified data
     *
     * @param uuid uuid
     * @param playerData playerData
     * @return success or not
     */
    CompletableFuture<Boolean> savePlayerData(UUID uuid, PlayerData playerData);

    /**
     * Get an online user by uuid
     *
     * @param uuid uuid
     * @return online user
     */
    Optional<OnlineUser> getOnlineUser(UUID uuid);

    /**
     * Get player data from json
     *
     * @param json json
     * @return data
     */
    @NotNull
    PlayerData fromJson(String json);

    /**
     * Get player data from bytes
     *
     * @param data data
     * @return data
     */
    PlayerData fromBytes(byte[] data);

    /**
     * Convert player data to bytes
     *
     * @param playerData playerData
     * @return bytes
     */
    byte[] toBytes(PlayerData playerData);

    /**
     * Convert player data to json
     *
     * @param playerData playerData
     * @return json
     */
    @NotNull
    String toJson(@NotNull PlayerData playerData);
}
