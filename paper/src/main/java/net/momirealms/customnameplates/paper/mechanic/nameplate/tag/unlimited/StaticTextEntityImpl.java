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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.NearbyRule;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.StaticTextEntity;
import net.momirealms.customnameplates.paper.mechanic.misc.PacketManager;
import net.momirealms.customnameplates.paper.util.FakeEntityUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class StaticTextEntityImpl implements StaticTextEntity {

    private final UUID uuid = UUID.randomUUID();
    private final UnlimitedEntity owner;
    private double yOffset;
    private final int entityId;
    private final Vector<Player> viewers;
    private final ConcurrentHashMap<UUID, String> textCache;
    private final NearbyRule comeRule;
    private final NearbyRule leaveRule;
    private String defaultText;
    private final String plugin;
    private final PacketContainer destroyPacket;

    public StaticTextEntityImpl (
            UnlimitedEntity owner,
            double yOffset,
            NearbyRule comeRule,
            NearbyRule leaveRule,
            String defaultText,
            String plugin
    ) {
        this.entityId = FakeEntityUtils.getAndIncrease();
        this.owner = owner;
        this.yOffset = yOffset;
        this.viewers = new Vector<>();
        this.textCache = new ConcurrentHashMap<>();
        this.comeRule = comeRule;
        this.leaveRule = leaveRule;
        this.defaultText = defaultText;
        this.plugin = plugin;
        this.destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        this.destroyPacket.getIntLists().write(0, List.of(entityId));
    }

    @Override
    public String getPlugin() {
        return plugin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticTextEntityImpl that = (StaticTextEntityImpl) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean isShownTo(Player viewer) {
        return viewers.contains(viewer);
    }

    @Override
    public NearbyRule getComeRule() {
        return comeRule;
    }

    @Override
    public NearbyRule getLeaveRule() {
        return leaveRule;
    }

    @Override
    public void removePlayerFromViewers(Player player) {
        if (!viewers.contains(player)) {
            return;
        }
        this.viewers.remove(player);
        destroy(player);
    }

    @Override
    public void addPlayerToViewers(Player player) {
        if (viewers.contains(player)) {
            return;
        }
        this.viewers.add(player);
        spawn(player, owner.getEntity().getPose());
    }

    @Override
    public double getOffset() {
        return yOffset;
    }

    @Override
    public void setOffset(double offset) {
        if (yOffset == offset) return;
        yOffset = offset;
        for (Player all : viewers) {
            PacketManager.getInstance().send(all, getTeleportPacket());
        }
    }

    @Override
    public void setText(String text) {
        if (text.equals(defaultText)) return;
        this.defaultText = text;
        for (Player viewer : viewers) {
            PacketManager.getInstance().send(viewer, FakeEntityUtils.getMetaPacket(entityId, getText(viewer), owner.getEntity().isSneaking()));
        }
    }

    @Override
    public String getText(Player viewer) {
        return textCache.getOrDefault(viewer.getUniqueId(), defaultText);
    }

    @Override
    public void setText(Player viewer, String text) {
        String previous = this.textCache.put(viewer.getUniqueId(), text);
        if (previous != null && previous.equals(text)) {
            return;
        }
        PacketManager.getInstance().send(viewer, FakeEntityUtils.getMetaPacket(entityId, text, owner.getEntity().isSneaking()));
    }

    @Override
    public void removeText(Player viewer) {
        String previous = this.textCache.remove(viewer.getUniqueId());
        if (previous != null && previous.equals(defaultText)) {
            return;
        }
        PacketManager.getInstance().send(viewer, FakeEntityUtils.getMetaPacket(entityId, defaultText, owner.getEntity().isSneaking()));
    }

    @Override
    public void spawn(Player viewer, Pose pose) {
        PacketManager.getInstance().send(viewer, getSpawnPackets(getText(viewer), pose));
    }

    @Override
    public void destroy() {
        for (Player all : viewers) {
            PacketManager.getInstance().send(all, destroyPacket);
        }
        viewers.clear();
        textCache.clear();
    }

    @Override
    public void destroy(Player viewer) {
        PacketManager.getInstance().send(viewer, destroyPacket);
        textCache.remove(viewer.getUniqueId());
    }

    @Override
    public void teleport(double x, double y, double z, boolean onGround) {
        PacketContainer packet = getTeleportPacket(x, y, z, onGround);
        for (Player all : viewers) {
            PacketManager.getInstance().send(all, packet);
        }
    }

    @Override
    public void teleport(Player viewer, double x, double y, double z, boolean onGround) {
        if (viewers.contains(viewer)) {
            PacketManager.getInstance().send(viewer, getTeleportPacket(x, y, z, onGround));
        }
    }

    @Override
    public void teleport() {
        PacketContainer packet = getTeleportPacket();
        for (Player viewer : viewers) {
            PacketManager.getInstance().send(viewer, packet);
        }
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void move(short x, short y, short z, boolean onGround) {
        PacketContainer packet = getMovePacket(x, y, z, onGround);
        for (Player viewer : viewers) {
            PacketManager.getInstance().send(viewer, packet);
        }
    }

    @Override
    public void move(Player viewer, short x, short y, short z, boolean onGround) {
        if (viewers.contains(viewer)) {
            PacketContainer packet = getMovePacket(x, y, z, onGround);
            PacketManager.getInstance().send(viewer, packet);
        }
    }

    @Override
    public void respawn(Player viewer, Pose pose) {
        destroy(viewer);
        spawn(viewer, pose);
    }

    @Override
    public void respawn(Pose pose) {
        for (Player viewer : viewers) {
            respawn(viewer, pose);
        }
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void handlePose(Pose previous, Pose pose) {
        // Add delay to prevent the tag from appearing earlier
        CustomNameplatesPlugin.get().getScheduler().runTaskAsyncLater(() -> {
            for (Player viewer : viewers) {
                respawn(viewer, pose);
            }
        }, 20, TimeUnit.MILLISECONDS);
    }

    public PacketContainer getMovePacket(short x, short y, short z, boolean onGround) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE);
        packet.getIntegers().write(0, entityId);
        packet.getShorts().write(0, x);
        packet.getShorts().write(1, y);
        packet.getShorts().write(2, z);
        packet.getBooleans().write(0, onGround);
        return packet;
    }

    public PacketContainer getTeleportPacket(double x, double y, double z, boolean onGround) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityId);
        packet.getDoubles().write(0, x);
        packet.getDoubles().write(1, y + yOffset);
        packet.getDoubles().write(2, z);
        packet.getBooleans().write(0, onGround);
        return packet;
    }

    public PacketContainer getTeleportPacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityId);
        Location location = getEntityLocation();
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());
        return packet;
    }

    private Location getEntityLocation() {
        var entity = owner.getEntity();
        double x = entity.getLocation().getX();
        double y = entity.getLocation().getY();
        double z = entity.getLocation().getZ();
        y += yOffset;
        return new Location(null, x, y, z);
    }

    private PacketContainer[] getSpawnPackets(String text, Pose pose) {
        PacketContainer entityPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        entityPacket.getModifier().write(0, entityId);
        entityPacket.getModifier().write(1, uuid);
        entityPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        Location location = getEntityLocation();
        entityPacket.getDoubles().write(0, location.getX());
        entityPacket.getDoubles().write(1, location.getY() + (pose == Pose.SNEAKING ? -0.3 : 0));
        entityPacket.getDoubles().write(2, location.getZ());
        PacketContainer metaPacket = FakeEntityUtils.getMetaPacket(entityId, text, pose == Pose.SNEAKING);
        return new PacketContainer[] {entityPacket, metaPacket};
    }
}