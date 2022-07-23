package net.momirealms.customnameplates;

import com.comphenix.protocol.ProtocolLibrary;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.momirealms.customnameplates.commands.Execute;
import net.momirealms.customnameplates.commands.TabComplete;
import net.momirealms.customnameplates.data.DataManager;
import net.momirealms.customnameplates.data.SqlHandler;
import net.momirealms.customnameplates.helper.LibraryLoader;
import net.momirealms.customnameplates.hook.HookManager;
import net.momirealms.customnameplates.listener.PlayerListener;
import net.momirealms.customnameplates.listener.PacketsListener;
import net.momirealms.customnameplates.resource.ResourceManager;
import net.momirealms.customnameplates.scoreboard.ScoreBoardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CustomNameplates extends JavaPlugin {

    public static JavaPlugin instance;
    public static BukkitAudiences adventure;

    private ResourceManager resourceManager;
    private DataManager dataManager;
    private HookManager hookManager;
    private ScoreBoardManager scoreBoardManager;

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }
    public DataManager getDataManager() { return this.dataManager; }
    public HookManager getHookManager() { return this.hookManager; }
    public ScoreBoardManager getScoreBoardManager() { return this.scoreBoardManager; }

    @Override
    public void onLoad(){

        instance = this;
        LibraryLoader.load("commons-io","commons-io","2.11.0","https://repo.maven.apache.org/maven2/");
        LibraryLoader.load("com.zaxxer","HikariCP","5.0.1","https://repo.maven.apache.org/maven2/");
    }

    @Override
    public void onEnable() {

        adventure = BukkitAudiences.create(this);
        AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates] </gradient><color:#baffd1>Running on " + Bukkit.getVersion());
        //重载插件
        ConfigManager.MainConfig.ReloadConfig();
        ConfigManager.Message.ReloadConfig();
        ConfigManager.DatabaseConfig.LoadConfig();
        //指令注册
        Objects.requireNonNull(Bukkit.getPluginCommand("customnameplates")).setExecutor(new Execute(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("customnameplates")).setTabCompleter(new TabComplete(this));
        //事件注册
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this),this);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketsListener(this));
        //新建单例
        this.resourceManager = new ResourceManager(this);
        this.dataManager = new DataManager(this);
        this.hookManager = new HookManager(this);
        this.scoreBoardManager = new ScoreBoardManager(this);
        //生成资源包
        resourceManager.generateResourcePack();
        if (!DataManager.create()) {
            AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to enable Data Manager! Disabling plugin...</red>");
            instance.getPluginLoader().disablePlugin(instance);
            return;
        }
        //启动完成
        AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        SqlHandler.saveAll();
        SqlHandler.close();
        //清除缓存实体
        Execute.pCache.forEach(Entity::remove);
        //关闭adventure
        if(adventure != null) {
            adventure.close();
            adventure = null;
        }
    }
}
