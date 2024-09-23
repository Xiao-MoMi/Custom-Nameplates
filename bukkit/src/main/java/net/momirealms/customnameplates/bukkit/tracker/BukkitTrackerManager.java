package net.momirealms.customnameplates.bukkit.tracker;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.JoinQuitListener;
import net.momirealms.customnameplates.api.tracker.TrackedEntityView;
import net.momirealms.customnameplates.api.tracker.TrackerManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BukkitTrackerManager implements TrackerManager, JoinQuitListener {

    private CustomNameplates plugin;
    private HashMap<UUID, TrackedEntityView<Player>> trackedViews = new HashMap<>();

    public BukkitTrackerManager(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerJoin(CNPlayer<?> player) {

    }

    @Override
    public void onPlayerQuit(CNPlayer<?> player) {

    }
}
