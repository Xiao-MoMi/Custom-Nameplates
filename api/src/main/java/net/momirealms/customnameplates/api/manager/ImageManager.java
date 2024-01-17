package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.mechanic.character.ConfiguredChar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ImageManager {

    /**
     * Get an image by key
     *
     * @param key key
     * @return image
     */
    @Nullable
    ConfiguredChar getImage(@NotNull String key);

    Collection<ConfiguredChar> getImages();

    /**
     * Register am image into the plugin
     * This will fail if there already exists one with the same key
     *
     * @param key key
     * @param configuredChar image
     * @return success or not
     */
    boolean registerImage(@NotNull String key, @NotNull ConfiguredChar configuredChar);

    /**
     * Unregister an image by key
     * This will fail if the key doesn't exist
     *
     * @param key key
     * @return success or not
     */
    boolean unregisterImage(@NotNull String key);
}
