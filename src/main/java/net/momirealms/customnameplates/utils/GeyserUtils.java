package net.momirealms.customnameplates.utils;

import org.geysermc.api.Geyser;

import java.util.UUID;

public class GeyserUtils {

    public static boolean isBedrockPlayer(UUID uuid) {
        return Geyser.api().isBedrockPlayer(uuid);
    }
}
