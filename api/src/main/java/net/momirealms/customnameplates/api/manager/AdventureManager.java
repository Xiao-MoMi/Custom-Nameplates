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

package net.momirealms.customnameplates.api.manager;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface AdventureManager {

    /**
     * Strip all the tags from text
     *
     * @param text text
     * @return stripped
     */
    String stripTags(String text);

    /**
     * Get component from text
     *
     * @param text text
     * @return component
     */
    Component getComponentFromMiniMessage(String text);

    /**
     * Send a message to a command sender
     *
     * @param sender sender
     * @param msg message
     */
    void sendMessage(CommandSender sender, String msg);

    /**
     * Send a message with prefix to a command sender
     *
     * @param sender sender
     * @param msg message
     */
    void sendMessageWithPrefix(CommandSender sender, String msg);

    /**
     * Send a message to console
     *
     * @param msg message
     */
    void sendConsoleMessage(String msg);

    /**
     * Send a message to a player
     *
     * @param player player
     * @param msg message
     */
    void sendPlayerMessage(Player player, String msg);

    /**
     * Send a title
     *
     * @param player player
     * @param title title
     * @param subtitle subtitle
     * @param in in (ms)
     * @param duration duration (ms)
     * @param out out (ms)
     */
    void sendTitle(Player player, String title, String subtitle, int in, int duration, int out);

    /**
     * Send a title
     *
     * @param player player
     * @param title title
     * @param subtitle subtitle
     * @param in in (ms)
     * @param duration duration (ms)
     * @param out out (ms)
     */
    void sendTitle(Player player, Component title, Component subtitle, int in, int duration, int out);

    /**
     * Send an actionbar
     *
     * @param player player
     * @param msg msg
     */
    void sendActionbar(Player player, String msg);

    /**
     * Send an actionbar
     *
     * @param player player
     * @param component component
     */
    void sendActionbar(Player player, Component component);

    /**
     * Play a sound to a player
     *
     * @param player player
     * @param source sound source
     * @param key sound key
     * @param volume volume
     * @param pitch pitch
     */
    void sendSound(Player player, Sound.Source source, Key key, float volume, float pitch);

    /**
     * Play a sound to a player
     *
     * @param player player
     * @param sound sound
     */
    void sendSound(Player player, Sound sound);

    /**
     * Replace legacy color codes to MiniMessage format
     *
     * @param legacy legacy text
     * @return MiniMessage format text
     */
    String legacyToMiniMessage(String legacy);

    /**
     * If a char is legacy color code
     *
     * @param c char
     * @return is legacy color
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isColorCode(char c);

    int colorToDecimal(ChatColor color);

    /**
     * Get legacy format text
     *
     * @param component component
     * @return legacy format text
     */
    String componentToLegacy(Component component);

    /**
     * Get json by component
     *
     * @param component component
     * @return json
     */
    String componentToJson(Component component);

    /**
     * Get MiniMessage format text from component
     *
     * @param component component
     * @return text
     */
    String getMiniMessageFormat(Component component);

    Object getIChatComponent(String json);
}
