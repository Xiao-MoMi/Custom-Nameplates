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

import net.momirealms.customnameplates.api.mechanic.bubble.Bubble;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface BubbleManager {

    boolean registerBubble(String key, Bubble bubble);

    boolean unregisterBubble(String key);

    @Nullable Bubble getBubble(String bubble);

    boolean hasBubble(Player player, String bubble);

    List<String> getAvailableBubbles(Player player);

    List<String> getAvailableBubblesDisplayNames(Player player);

    String[] getBlacklistChannels();

    Collection<Bubble> getBubbles();

    boolean containsBubble(String key);

    boolean equipBubble(Player player, String bubble);

    void unEquipBubble(Player player);

    String getDefaultBubble();

    Collection<String> getBubbleKeys();
}
