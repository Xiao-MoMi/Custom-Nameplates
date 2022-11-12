package net.momirealms.customnameplates.manager;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.listener.SimpleListener;
import net.momirealms.customnameplates.objects.Function;
import net.momirealms.customnameplates.objects.data.DataStorageInterface;
import net.momirealms.customnameplates.objects.data.FileStorageImpl;
import net.momirealms.customnameplates.objects.data.MySQLStorageImpl;
import net.momirealms.customnameplates.objects.data.PlayerData;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager extends Function {

    private final ConcurrentHashMap<UUID, PlayerData> playerDataCache;
    private final SimpleListener simpleListener;
    private final DataStorageInterface dataStorageInterface;

    public DataManager() {
        this.playerDataCache = new ConcurrentHashMap<>();
        this.simpleListener = new SimpleListener(this);
        ConfigUtil.update("database.yml");
        YamlConfiguration config = ConfigUtil.getConfig("database.yml");
        if (config.getString("data-storage-method","YAML").equalsIgnoreCase("YAML")) {
            this.dataStorageInterface = new FileStorageImpl();
            AdventureUtil.consoleMessage("[CustomNameplates] Storage Mode: YAML");
        } else this.dataStorageInterface = new MySQLStorageImpl();
        this.dataStorageInterface.initialize();
    }

    @NotNull
    public PlayerData getPlayerData(OfflinePlayer player) {
        PlayerData playerData = playerDataCache.get(player.getUniqueId());
        if (playerData != null) {
            return playerData;
        }
        return new PlayerData(player, NameplateManager.defaultNameplate, ChatBubblesManager.defaultBubble);
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(simpleListener, CustomNameplates.plugin);
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(simpleListener);
    }

    public void disable() {
        unload();
        this.playerDataCache.clear();
        this.dataStorageInterface.disable();
    }

    @Override
    public void onJoin(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.plugin, () -> {
            PlayerData playerData = dataStorageInterface.loadData(player);
            if (playerData == null) return;
            playerDataCache.put(player.getUniqueId(), playerData);
            //wait
            if (ConfigUtil.isModuleEnabled("nameplate")) {
                CustomNameplates.plugin.getNameplateManager().getTeamManager().createTeam(player);
            }
        });
    }

    public void saveData(Player player) {
        this.dataStorageInterface.saveData(getPlayerData(player));
    }
}
