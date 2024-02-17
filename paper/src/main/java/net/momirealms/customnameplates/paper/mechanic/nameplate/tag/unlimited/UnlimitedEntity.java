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

package net.momirealms.customnameplates.paper.mechanic.nameplate.tag.unlimited;

import net.momirealms.customnameplates.api.mechanic.tag.unlimited.EntityTagEntity;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.StaticTextEntity;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.StaticTextTagSetting;
import net.momirealms.customnameplates.api.util.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

public class UnlimitedEntity implements EntityTagEntity {

    protected final UnlimitedTagManagerImpl manager;
    protected final Entity entity;
    protected final Vector<Player> nearbyPlayers;
    protected Player[] nearbyPlayerArray;
    protected final Vector<StaticTextEntity> staticTags;
    protected StaticTextEntity[] staticTagArray;

    public UnlimitedEntity(UnlimitedTagManagerImpl manager, Entity entity) {
        this.manager = manager;
        this.entity = entity;
        this.nearbyPlayers = new Vector<>();
        this.staticTags = new Vector<>();
    }

    public Entity getEntity() {
        return entity;
    }

    public Player[] getNearbyPlayers() {
        return nearbyPlayerArray;
    }

    public void addNearbyPlayerNaturally(Player player) {
        if (nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.add(player);
        playerVectorToArray();
        for (StaticTextEntity tag : staticTagArray) {
            if (tag.getComeRule().isPassed(player, entity)) {
                tag.addPlayerToViewers(player);
            }
        }
    }

    public void removeNearbyPlayerNaturally(Player player) {
        if (!nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.remove(player);
        playerVectorToArray();
        for (StaticTextEntity tag : staticTagArray) {
            if (tag.getLeaveRule().isPassed(player, entity)) {
                tag.removePlayerFromViewers(player);
            }
        }
    }

    public void addNearByPlayerToMap(int range) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (       online == entity
                    || LocationUtils.getDistance(online, entity) > range
                    || online.getWorld() != entity.getWorld()
                    || online.isDead()
            ) continue;
            addNearbyPlayerNaturally(online);
        }
    }

    @Override
    public void addTag(StaticTextEntity tag) {
        if (staticTags.contains(tag)) {
            return;
        }
        staticTags.add(tag);
        staticTagVectorToArray();
        for (Player all : nearbyPlayers) {
            if (tag.getComeRule().isPassed(all, entity)) {
                tag.addPlayerToViewers(all);
            }
        }
    }

    @Override
    public StaticTextEntity addTag(StaticTextTagSetting setting) {
        var tag = manager.createNamedEntity(this, setting);
        addTag(tag);
        return tag;
    }

    @Override
    public void removeTag(StaticTextEntity tag) {
        if (staticTags.remove(tag)) {
            tag.destroy();
            staticTagVectorToArray();
        }
    }

    @Override
    public Collection<StaticTextEntity> getStaticTags() {
        return new ArrayList<>(staticTags);
    }

    @Override
    public void forceAddNearbyPlayer(Player player) {
        if (nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.add(player);
        playerVectorToArray();
        for (StaticTextEntity tag : staticTagArray) {
            tag.addPlayerToViewers(player);
        }
    }

    @Override
    public void forceRemoveNearbyPlayer(Player player) {
        if (!nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.remove(player);
        playerVectorToArray();
        for (StaticTextEntity tag : staticTagArray) {
            tag.removePlayerFromViewers(player);
        }
    }

    public void move(Player receiver, short x, short y, short z, boolean onGround) {
        for (StaticTextEntity tag : staticTagArray) {
            tag.move(receiver, x, y, z, onGround);
        }
    }

    public void teleport(Player receiver, double x, double y, double z, boolean onGround) {
        for (StaticTextEntity tag : staticTagArray) {
            tag.teleport(receiver, x, y, z, onGround);
        }
    }

    public void teleport() {
        for (StaticTextEntity tag : staticTagArray) {
            tag.teleport();
        }
    }

    public void handlePose(Pose previous, Pose pose) {
        for (StaticTextEntity tag : staticTagArray) {
            tag.handlePose(previous, pose);
        }
    }

    @Override
    public void destroy() {
        manager.removeUnlimitedEntityFromMap(entity.getUniqueId());
        for (StaticTextEntity tag : staticTagArray) {
            tag.destroy();
        }
        nearbyPlayers.clear();
        staticTags.clear();
        staticTagArray = null;
        nearbyPlayerArray = null;
    }

    public void respawn() {
        for (StaticTextEntity tag : staticTagArray) {
            tag.respawn(entity.getPose());
        }
    }

    protected void staticTagVectorToArray() {
        staticTagArray = staticTags.toArray(new StaticTextEntity[0]);
    }

    protected void playerVectorToArray() {
        nearbyPlayerArray = nearbyPlayers.toArray(new Player[0]);
    }
}
