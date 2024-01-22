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

import java.util.Vector;

public class UnlimitedEntity implements EntityTagEntity {

    protected final UnlimitedTagManagerImpl manager;
    protected final Entity entity;
    protected final Vector<Player> nearbyPlayers;
    protected final Vector<StaticTextEntity> staticTags;

    public UnlimitedEntity(UnlimitedTagManagerImpl manager, Entity entity) {
        this.manager = manager;
        this.entity = entity;
        this.nearbyPlayers = new Vector<>();
        this.staticTags = new Vector<>();
    }

    public Entity getEntity() {
        return entity;
    }

    public Vector<Player> getNearbyPlayers() {
        return nearbyPlayers;
    }

    public void addNearbyPlayerNaturally(Player player) {
        if (nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.add(player);
        for (StaticTextEntity tag : staticTags) {
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
        for (StaticTextEntity tag : staticTags) {
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
        for (Player all : nearbyPlayers) {
            tag.addPlayerToViewers(all);
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
        }
    }

    @Override
    public Vector<StaticTextEntity> getStaticTags() {
        return staticTags;
    }

    @Override
    public void forceAddNearbyPlayer(Player player) {
        if (nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.add(player);
        for (StaticTextEntity tag : staticTags) {
            tag.addPlayerToViewers(player);
        }
    }

    @Override
    public void forceRemoveNearbyPlayer(Player player) {
        if (!nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.remove(player);
        for (StaticTextEntity tag : staticTags) {
            tag.removePlayerFromViewers(player);
        }
    }


    public void move(Player receiver, short x, short y, short z, boolean onGround) {
        for (StaticTextEntity tag : staticTags) {
            tag.move(receiver, x, y, z, onGround);
        }
    }

    public void teleport(Player receiver, double x, double y, double z, boolean onGround) {
        for (StaticTextEntity tag : staticTags) {
            tag.teleport(receiver, x, y, z, onGround);
        }
    }

    public void teleport() {
        for (StaticTextEntity tag : staticTags) {
            tag.teleport();
        }
    }

    public void handlePose(Pose previous, Pose pose) {
        for (StaticTextEntity tag : staticTags) {
            tag.handlePose(previous, pose);
        }
    }

    @Override
    public void destroy() {
        manager.removeUnlimitedEntityFromMap(entity.getUniqueId());
        for (StaticTextEntity tag : staticTags) {
            tag.destroy();
        }
        nearbyPlayers.clear();
        staticTags.clear();
    }

    public void respawn() {
        for (StaticTextEntity tag : staticTags) {
            tag.respawn(entity.getPose());
        }
    }
}
