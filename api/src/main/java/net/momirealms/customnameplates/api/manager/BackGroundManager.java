package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.mechanic.background.BackGround;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface BackGroundManager {

    /**
     * Get a background's config by key
     *
     * @param key key
     * @return background
     */
    @Nullable
    BackGround getBackGround(@NotNull String key);

    Collection<BackGround> getBackGrounds();

    /**
     * Register a background into the plugin
     * This will fail if there already exists one with the same key
     *
     * @param key key
     * @param backGround background
     * @return success or not
     */
    boolean registerBackGround(@NotNull String key, @NotNull BackGround backGround);

    /**
     * Unregister a background by key
     * This will fail if the key doesn't exist
     *
     * @param key key
     * @return success or not
     */
    boolean unregisterBackGround(@NotNull String key);
}
