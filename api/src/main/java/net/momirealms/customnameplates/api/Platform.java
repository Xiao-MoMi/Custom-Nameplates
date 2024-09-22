package net.momirealms.customnameplates.api;

import net.momirealms.customnameplates.api.packet.WrappedActionBarPacket;
import net.momirealms.customnameplates.api.placeholder.Placeholder;

public interface Platform {

    Object jsonToMinecraftComponent(String json);

    Placeholder registerPlatformPlaceholder(String id);

    void sendActionBar(CNPlayer<?> player, WrappedActionBarPacket packet);
}
