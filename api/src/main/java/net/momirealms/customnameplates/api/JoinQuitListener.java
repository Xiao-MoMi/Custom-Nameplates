package net.momirealms.customnameplates.api;

public interface JoinQuitListener {

    void onPlayerJoin(CNPlayer<?> player);

    void onPlayerQuit(CNPlayer<?> player);
}
