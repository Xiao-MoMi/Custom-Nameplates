package net.momirealms.customnameplates.api.placeholder;

import net.momirealms.customnameplates.api.CNPlayer;

public interface PlayerPlaceholder extends Placeholder {

    String request(CNPlayer<?> player);
}
