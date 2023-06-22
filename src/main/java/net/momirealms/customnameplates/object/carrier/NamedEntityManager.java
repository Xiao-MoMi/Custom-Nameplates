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

package net.momirealms.customnameplates.object.carrier;

import net.momirealms.customnameplates.object.ConditionalText;
import net.momirealms.customnameplates.object.DisplayMode;
import net.momirealms.customnameplates.object.DynamicText;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class NamedEntityManager {

    private final ConcurrentHashMap<UUID, NamedEntity> namedEntities = new ConcurrentHashMap<>();
    private NamedEntity[] namedEntityArray;
    private final Vector<Player> nearbyPlayers;
    private Player[] nearbyPlayerArray ;
    private final Player owner;
    private double hatOffset;
    private final NamedEntityCarrier namedEntityCarrier;
    private double highestTextHeight;

    public NamedEntityManager(NamedEntityCarrier namedEntityCarrier, Player owner) {
        this.owner = owner;
        this.nearbyPlayers = new Vector<>();
        this.namedEntityArray = new NamedEntityImpl[0];
        this.nearbyPlayerArray = new Player[0];
        this.namedEntityCarrier = namedEntityCarrier;
        this.init();
    }

    public void init() {
        Set<Map.Entry<ConditionalText, Double>> entries = namedEntityCarrier.getPersistentText().entrySet();
        for (Map.Entry<ConditionalText, Double> entry : entries) {
            addNamedEntity(UUID.randomUUID(), new NamedEntityImpl(
                    this,
                    owner,
                    new DynamicText(owner, entry.getKey().text()),
                    entry.getValue(),
                    entry.getKey().requirements(),
                    entry.getKey().textDisplayMeta()
            ));
        }
    }

    public void addNamedEntity(UUID uuid, NamedEntity namedEntity) {
        this.namedEntities.put(uuid, namedEntity);
        this.namedEntityArray = namedEntities.values().toArray(new NamedEntity[0]);
        for (Player p : nearbyPlayerArray) namedEntity.spawn(p);
    }

    public Player[] getNearbyPlayers(){
        return nearbyPlayerArray;
    }

    public boolean isNearby(Player viewer) {
        return nearbyPlayers.contains(viewer);
    }

    public void spawn(Player viewer) {
        nearbyPlayers.add(viewer);
        nearbyPlayerArray = nearbyPlayers.toArray(new Player[0]);
        for (NamedEntity fakeArmorStand : namedEntityArray)
            if (fakeArmorStand.canShow())
                fakeArmorStand.spawn(viewer);
    }

    public void refresh(boolean force) {
        highestTextHeight = -2;
        for (NamedEntity fakeArmorStand : namedEntityArray) {
            boolean canShow = fakeArmorStand.canShow();
            if (!canShow) {
                if (fakeArmorStand.isShown()) {
                    fakeArmorStand.destroy();
                }
            } else {
                if (fakeArmorStand.isShown()) {
                    if (fakeArmorStand.getDynamicText() != null && (fakeArmorStand.getDynamicText().update() || force))
                        fakeArmorStand.refresh();
                } else {
                    fakeArmorStand.spawn();
                }
                highestTextHeight = Math.max(fakeArmorStand.getOffset(), highestTextHeight);
            }
        }
    }

    public void destroy() {
        for (NamedEntity entity : namedEntityArray) {
            entity.destroy();
        }
        nearbyPlayers.clear();
        nearbyPlayerArray = new Player[0];
    }

    public void teleport(Player viewer) {
        for (NamedEntity entity : namedEntityArray) {
            if (entity.isShown())
                entity.teleport(viewer);
        }
    }

    public void teleport() {
        for (NamedEntity entity : namedEntityArray) {
            if (entity.isShown())
                entity.teleport();
        }
    }

    public void setSneak(boolean sneaking, boolean respawn) {
        for (NamedEntity entity : namedEntityArray) {
            if (entity.isShown())
                entity.setSneak(sneaking, respawn);
        }
    }

    public void respawn() {
        for (NamedEntity entity : namedEntityArray) {
            if (entity.isShown())
                for (Player viewer : nearbyPlayerArray)
                    entity.respawn(viewer);
        }
    }

    public void destroy(Player viewer) {
        for (NamedEntity entity : namedEntityArray) {
            entity.destroy(viewer);
        }
        unregisterPlayer(viewer);
    }

    public void unregisterPlayer(Player viewer) {
        if (nearbyPlayers.remove(viewer)) {
            nearbyPlayerArray = nearbyPlayers.toArray(new Player[0]);
        }
    }

    public void removeArmorStand(UUID uuid) {
        NamedEntity entity = namedEntities.remove(uuid);
        if (entity != null) {
            entity.destroy();
            namedEntityArray = namedEntities.values().toArray(new NamedEntity[0]);
        }
    }

    public void ascent(double lineSpace) {
        for (NamedEntity entity : namedEntityArray) {
            entity.setOffset(entity.getOffset() + lineSpace);
            entity.teleport();
        }
    }

    public double getHatOffset() {
        return hatOffset;
    }

    public void setHatOffset(double hatOffset) {
        this.hatOffset = hatOffset;
        teleport();
    }

    public DisplayMode getDisplayMode() {
        return namedEntityCarrier.getDisplayMode();
    }

    public double getHighestTextHeight() {
        return highestTextHeight;
    }
}