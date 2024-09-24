package net.momirealms.customnameplates.api.util;

import net.momirealms.customnameplates.api.CustomNameplates;

public record Vector3(double x, double y, double z) {

    public Object toVec3() {
        return CustomNameplates.getInstance().getPlatform().vec3(x, y, z);
    }

    public Vector3 add(double x, double y, double z) {
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }
}
