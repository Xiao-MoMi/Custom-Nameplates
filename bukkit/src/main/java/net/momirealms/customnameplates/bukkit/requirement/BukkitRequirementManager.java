package net.momirealms.customnameplates.bukkit.requirement;

import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.requirement.AbstractRequirementManager;
import net.momirealms.customnameplates.bukkit.requirement.builtin.NotPermissionRequirement;
import net.momirealms.customnameplates.bukkit.requirement.builtin.PermissionRequirement;
import net.momirealms.customnameplates.common.util.ListUtils;

import java.util.List;

public class BukkitRequirementManager extends AbstractRequirementManager {

    public BukkitRequirementManager(CustomNameplates plugin) {
        super(plugin);
    }

    @Override
    protected void registerBuiltInRequirements() {
        this.registerPermission();
    }

    private void registerPermission() {
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new PermissionRequirement(interval, list);
        }, "permission");
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new NotPermissionRequirement(interval, list);
        }, "!permission");
    }
}
