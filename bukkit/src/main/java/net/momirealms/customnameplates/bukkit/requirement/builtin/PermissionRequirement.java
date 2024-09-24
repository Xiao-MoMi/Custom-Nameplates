package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PermissionRequirement extends AbstractRequirement {

    private final Set<String> permissions;

    public PermissionRequirement(int refreshInterval, List<String> permissions) {
        super(refreshInterval);
        this.permissions = new HashSet<>(permissions);
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        for (String perm : permissions) {
            if (((Player) p1.player()).hasPermission(perm)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String type() {
        return "permission";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionRequirement that = (PermissionRequirement) o;
        return permissions.equals(that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(permissions);
    }
}
