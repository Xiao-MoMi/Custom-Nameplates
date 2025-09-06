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

package net.momirealms.customnameplates.bukkit;

import io.netty.channel.Channel;
import net.momirealms.customnameplates.api.AbstractCNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.util.Vector3;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BukkitCNPlayer extends AbstractCNPlayer<Player> {
    private static final Attribute scaleAttribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("generic.scale"));

    public BukkitCNPlayer(CustomNameplates plugin, Channel channel) {
        super(plugin, channel);
    }

    public void setPlayer(Player player) {
        super.setPlayer(player);
        super.entityId = player.getEntityId();
        super.uuid = player.getUniqueId();
        super.name = player.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isInitialized() {
        return this.player != null;
    }

    @Override
    public Player player() {
        return super.player;
    }

    @Override
    public Vector3 position() {
        Location location = player().getLocation();
        return new Vector3(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public String world() {
        return player().getWorld().getName();
    }

    @Override
    public double scale() {
        if (scaleAttribute == null) return 1;
        return Optional.ofNullable(player().getAttribute(scaleAttribute)).map(AttributeInstance::getValue).orElse(1d);
    }

    @Override
    public boolean isCrouching() {
        return player().isSneaking();
    }

    @Override
    public boolean hasPermission(String permission) {
        return player().hasPermission(permission);
    }

    @Override
    public long playerTime() {
        return player().getPlayerTime();
    }

    @Override
    public boolean isOnline() {
        return player().isOnline();
    }

    @Override
    public boolean isSpectator() {
        return player().getGameMode() == GameMode.SPECTATOR;
    }

    @Override
    public Set<Integer> passengers() {
        return player().getPassengers().stream().map(Entity::getEntityId).collect(Collectors.toSet());
    }

    @Override
    public boolean isFlying() {
        return player().isFlying();
    }

    @Override
    public int remainingAir() {
        return player().getRemainingAir();
    }
}
