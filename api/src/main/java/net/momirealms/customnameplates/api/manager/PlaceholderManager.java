package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.mechanic.placeholder.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PlaceholderManager {

    /**
     * Detect all the placeholders
     *
     * @param text text
     * @return placeholder
     */
    @NotNull
    List<String> detectPlaceholders(String text);

    /**
     * Get a static text instance
     *
     * @param key key
     * @return static text
     */
    @Nullable
    StaticText getStaticText(String key);

    /**
     * Get a switch text instance
     *
     * @param key key
     * @return switch text
     */
    @Nullable
    SwitchText getSwitchText(String key);

    /**
     * Get a descent text instance
     *
     * @param key key
     * @return descent text
     */
    @Nullable
    DescentText getDescentText(String key);

    /**
     * Get a conditional text
     *
     * @param key key
     * @return conditional text
     */
    @Nullable
    ConditionalText getConditionalText(String key);

    /**
     * Get a nameplate text
     *
     * @param key key
     * @return nameplate text
     */
    @Nullable
    NameplateText getNameplateText(String key);

    /**
     * Get a background text
     *
     * @param key key
     * @return background text
     */
    @Nullable
    BackGroundText getBackGroundText(String key);
}
