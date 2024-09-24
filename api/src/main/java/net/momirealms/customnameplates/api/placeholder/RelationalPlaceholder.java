package net.momirealms.customnameplates.api.placeholder;

import net.momirealms.customnameplates.api.CNPlayer;

public interface RelationalPlaceholder extends Placeholder {

    String request(CNPlayer p1, CNPlayer p2);
}
