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

package net.momirealms.customnameplates.api.data;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.mechanic.bubble.Bubble;
import net.momirealms.customnameplates.api.mechanic.nameplate.Nameplate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OnlineUser {

    private final Player player;
    private String nameplate;
    private String bubble;

    public OnlineUser(Player player, PlayerData playerData) {
        this.player = player;
        this.nameplate = playerData.getNameplate();
        this.bubble = playerData.getBubble();
    }

    public PlayerData toPlayerData() {
        return PlayerData.builder()
                .setBubble(bubble)
                .setNameplate(nameplate)
                .build();
    }

    public UUID getUUID() {
        return player.getUniqueId();
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Get the original nameplate key from data
     */
    @NotNull
    public String getNameplateKey() {
        return nameplate;
    }

    /**
     * Get the original bubble key from data
     */
    @NotNull
    public String getBubbleKey() {
        return bubble;
    }

    /**
     * This value might be inconsistent with the key get by "getNameplateKey()"
     * Because if a player doesn't have a nameplate, his nameplate would be the default one
     */
    @Nullable
    public Nameplate getNameplate() {
        String temp = nameplate;
        if (temp.equals("none")) {
            temp = CustomNameplatesPlugin.get().getNameplateManager().getDefaultNameplate();
        }
        return CustomNameplatesPlugin.get().getNameplateManager().getNameplate(temp);
    }

    @Nullable
    public Bubble getBubble() {
        String temp = nameplate;
        if (temp.equals("none")) {
            temp = CustomNameplatesPlugin.get().getBubbleManager().getDefaultBubble();
        }
        return CustomNameplatesPlugin.get().getBubbleManager().getBubble(temp);
    }

    /**
     * Set nameplate for a player
     *
     * @param nameplate nameplate
     */
    public void setNameplate(String nameplate) {
        this.nameplate = nameplate;
    }

    /**
     * Set bubble for a player
     *
     * @param bubble bubble
     */
    public void setBubble(String bubble) {
        this.bubble = bubble;
    }
}
