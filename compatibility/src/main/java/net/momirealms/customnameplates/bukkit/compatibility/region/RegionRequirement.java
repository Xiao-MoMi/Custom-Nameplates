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

package net.momirealms.customnameplates.bukkit.compatibility.region;

import com.google.common.base.Objects;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.backend.requirement.AbstractRequirement;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RegionRequirement extends AbstractRequirement {

    private final List<String> regions;
    private final int mode;

    public RegionRequirement(int refreshInterval, int mode, Collection<String> regions) {
        super(refreshInterval);
        this.regions = new ArrayList<>(regions);
        this.mode = mode;
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        Location location = ((Player) p1.player()).getLocation();
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
        if (regionManager != null) {
            ApplicableRegionSet set = regionManager.getApplicableRegions(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            if (mode == 1) {
                for (ProtectedRegion region : set) {
                    if (regions.contains(region.getId())) {
                        return true;
                    }
                }
            } else if (mode == 2) {
                outer: {
                    Set<String> ids = set.getRegions().stream().map(ProtectedRegion::getId).collect(Collectors.toSet());
                    for (String region : regions) {
                        if (!ids.contains(region)) {
                            break outer;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String type() {
        return "region";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegionRequirement that)) return false;
        return mode == that.mode && Objects.equal(regions, that.regions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(regions, mode);
    }
}
