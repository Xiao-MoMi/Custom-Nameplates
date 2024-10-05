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

package net.momirealms.customnameplates.api.feature.bubble;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public interface BubbleManager extends Reloadable {

    Collection<BubbleConfig> getBubbleConfigs();

    @Nullable
    Bubble getBubble(String id);

    @Nullable BubbleConfig getBubbleConfig(String id);

    Collection<Bubble> getBubbles();

    boolean hasBubble(CNPlayer player, String id);

    Collection<BubbleConfig> availableBubbles(CNPlayer player);

    Set<String> blacklistChannels();

    String defaultBubbleId();

    Requirement[] sendBubbleRequirements();

    Requirement[] viewBubbleRequirements();

    String omittedText();

    double verticalOffset();

    int stayDuration();

    int appearDuration();

    int disappearDuration();

    float viewRange();

    ChannelMode channelMode();
}
