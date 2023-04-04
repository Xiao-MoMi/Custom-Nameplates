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

package net.momirealms.customnameplates.object.armorstand;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.ConditionalText;
import net.momirealms.customnameplates.object.DynamicText;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ArmorStandManager {

    private final ConcurrentHashMap<UUID, FakeArmorStand> armorStands = new ConcurrentHashMap<>();
    private FakeArmorStand[] armorStandArray;
    private final Vector<Player> nearbyPlayers;
    private Player[] nearbyPlayerArray ;
    private final Player owner;

    public ArmorStandManager(Player owner) {
        this.owner = owner;
        this.nearbyPlayers = new Vector<>();
        this.armorStandArray = new FakeArmorStand[0];
        this.nearbyPlayerArray = new Player[0];
    }

    public void initNameplateArmorStands() {
        Set<Map.Entry<ConditionalText, Double>> entries = CustomNameplates.getInstance().getNameplateManager().getContentMap().entrySet();
        for (Map.Entry<ConditionalText, Double> entry : entries) {
            addArmorStand(UUID.randomUUID(), new FakeArmorStand(
                    this,
                    owner,
                    new DynamicText(owner, entry.getKey().text()),
                    entry.getValue(),
                    entry.getKey().requirements()
            ));
        }
    }

    public void addArmorStand(UUID uuid, FakeArmorStand fakeArmorStand) {
        this.armorStands.put(uuid, fakeArmorStand);
        this.armorStandArray = armorStands.values().toArray(new FakeArmorStand[0]);
        for (Player p : nearbyPlayerArray) fakeArmorStand.spawn(p);
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
        for (FakeArmorStand fakeArmorStand : armorStandArray)
            if (fakeArmorStand.canShow())
                fakeArmorStand.spawn(viewer);
    }

    public void refresh(boolean force) {
        for (FakeArmorStand fakeArmorStand : armorStandArray) {
            fakeArmorStand.refresh();
            boolean canShow = fakeArmorStand.canShow();
            if (!canShow) {
                if (fakeArmorStand.isShown()) {
                    fakeArmorStand.destroy();
                }
            }
            else {
                if (fakeArmorStand.isShown()) {
                    if (fakeArmorStand.getDynamicText().update() || force)
                        fakeArmorStand.refresh();
                }
                else {
                    fakeArmorStand.spawn();
                }
            }
        }
    }

    public void destroy() {
        for (FakeArmorStand fakeArmorStand : armorStandArray) {
            fakeArmorStand.destroy();
        }
        nearbyPlayers.clear();
        nearbyPlayerArray = new Player[0];
    }

    public void teleport(Player viewer) {
        for (FakeArmorStand fakeArmorStand : armorStandArray) {
            if (fakeArmorStand.isShown())
                fakeArmorStand.teleport(viewer);
        }
    }

    public void teleport() {
        for (FakeArmorStand fakeArmorStand : armorStandArray) {
            if (fakeArmorStand.isShown())
                fakeArmorStand.teleport();
        }
    }

    public void setSneak(boolean sneaking, boolean respawn) {
        for (FakeArmorStand fakeArmorStand : armorStandArray) {
            if (fakeArmorStand.isShown())
                fakeArmorStand.setSneak(sneaking, respawn);
        }
    }

    public void respawn() {
        for (FakeArmorStand fakeArmorStand : armorStandArray) {
            if (fakeArmorStand.isShown())
                for (Player viewer : nearbyPlayerArray)
                    fakeArmorStand.respawn(viewer);
        }
    }

    public void destroy(Player viewer) {
        for (FakeArmorStand fakeArmorStand : armorStandArray) {
            fakeArmorStand.destroy(viewer);
        }
        unregisterPlayer(viewer);
    }

    public void unregisterPlayer(Player viewer) {
        if (nearbyPlayers.remove(viewer)) {
            nearbyPlayerArray = nearbyPlayers.toArray(new Player[0]);
        }
    }

    public void removeArmorStand(UUID uuid) {
        FakeArmorStand fakeArmorStand = armorStands.remove(uuid);
        if (fakeArmorStand != null) {
            fakeArmorStand.destroy();
            armorStandArray = armorStands.values().toArray(new FakeArmorStand[0]);
        }
    }

    public void addBubble(UUID uuid, FakeArmorStand fakeArmorStand, int stayTime, double lineSpace) {
        ascent(lineSpace);
        addArmorStand(uuid, fakeArmorStand);
        Bukkit.getScheduler().runTaskLater(CustomNameplates.getInstance(), () -> removeArmorStand(uuid), stayTime * 20L);
    }

    public void ascent(double lineSpace) {
        for (FakeArmorStand fakeArmorStand : armorStandArray) {
            fakeArmorStand.setOffset(fakeArmorStand.getOffset() + lineSpace);
            fakeArmorStand.teleport();
        }
    }
}