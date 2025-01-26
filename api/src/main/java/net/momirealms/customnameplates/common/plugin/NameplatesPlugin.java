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

package net.momirealms.customnameplates.common.plugin;

import net.momirealms.customnameplates.common.dependency.DependencyManager;
import net.momirealms.customnameplates.common.event.EventManager;
import net.momirealms.customnameplates.common.locale.TranslationManager;
import net.momirealms.customnameplates.common.plugin.classpath.ClassPathAppender;
import net.momirealms.customnameplates.common.plugin.config.ConfigLoader;
import net.momirealms.customnameplates.common.plugin.logging.PluginLogger;
import net.momirealms.customnameplates.common.plugin.scheduler.SchedulerAdapter;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * Interface representing the main CustomNameplates plugin.
 */
public interface NameplatesPlugin {

    /**
     * Retrieves an input stream for a resource file within the plugin.
     *
     * @param filePath the path to the resource file
     * @return an {@link InputStream} for the resource file
     */
    InputStream getResourceStream(String filePath);

    /**
     * Retrieves the plugin logger.
     *
     * @return the {@link PluginLogger} instance
     */
    PluginLogger getPluginLogger();

    /**
     * Retrieves the class path appender.
     *
     * @return the {@link ClassPathAppender} instance
     */
    ClassPathAppender getClassPathAppender();

    /**
     * Retrieves the scheduler adapter.
     *
     * @return the {@link SchedulerAdapter} instance
     */
    SchedulerAdapter<?> getScheduler();

    /**
     * Retrieves the data directory path.
     *
     * @return the {@link Path} to the data directory
     */
    Path getDataDirectory();

    /**
     * Retrieves the configuration directory path.
     * By default, this is the same as the data directory.
     *
     * @return the {@link Path} to the configuration directory
     */
    default Path getConfigDirectory() {
        return getDataDirectory();
    }

    default File getDataFolder() {
        return getDataDirectory().toFile();
    }

    /**
     * Retrieves the dependency manager.
     *
     * @return the {@link DependencyManager} instance
     */
    DependencyManager getDependencyManager();

    /**
     * Retrieves the translation manager.
     *
     * @return the {@link TranslationManager} instance
     */
    TranslationManager getTranslationManager();

    /**
     * Retrieves the configuration manager.
     *
     * @return the {@link ConfigLoader} instance
     */
    ConfigLoader getConfigManager();

    /**
     * Retrieves the server version.
     *
     * @return the server version as a string
     */
    String getServerVersion();

    /**
     * Retrieves the plugin version.
     *
     * @return the plugin version as a string
     */
    String getPluginVersion();

    /**
     * Loads the plugin.
     * This method is called during the plugin's loading phase.
     */
    void load();

    /**
     * Enables the plugin.
     * This method is called during the plugin's enabling phase.
     */
    void enable();

    /**
     * Disables the plugin.
     * This method is called during the plugin's disabling phase.
     */
    void disable();

    /**
     * Reloads the plugin.
     */
    void reload();

    /**
     * Debug
     *
     * @param supplier debug message supplier
     */
    void debug(Supplier<String> supplier);

    /**
     * Check if the plugin is the latest
     *
     * @return is latest or not
     */
    boolean isUpToDate();

    /**
     * Gets the event manager
     *
     * @return the event manager
     */
    EventManager getEventManager();
}
