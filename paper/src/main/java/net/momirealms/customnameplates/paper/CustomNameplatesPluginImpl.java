package net.momirealms.customnameplates.paper;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.event.CustomNameplatesReloadEvent;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.customnameplates.paper.command.CommandManager;
import net.momirealms.customnameplates.paper.helper.LibraryLoader;
import net.momirealms.customnameplates.paper.mechanic.actionbar.ActionBarManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.background.BackGroundManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.bossbar.BossBarManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.bubble.BubbleManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.font.WidthManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.image.ImageManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.misc.CoolDownManager;
import net.momirealms.customnameplates.paper.mechanic.misc.PacketManager;
import net.momirealms.customnameplates.paper.mechanic.misc.VersionManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.nameplate.NameplateManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.pack.ResourcePackManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.placeholder.PlaceholderManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.requirement.RequirementManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.team.TeamManagerImpl;
import net.momirealms.customnameplates.paper.scheduler.SchedulerImpl;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import net.momirealms.customnameplates.paper.setting.CNLocale;
import net.momirealms.customnameplates.paper.storage.StorageManagerImpl;
import net.momirealms.customnameplates.paper.util.ReflectionUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.TimeZone;

public class CustomNameplatesPluginImpl extends CustomNameplatesPlugin {

    private CoolDownManager coolDownManager;
    private PacketManager packetManager;

    @Override
    public void onLoad() {
        this.loadLibraries();
        ReflectionUtils.load();
    }

    @Override
    public void onEnable() {
        this.adventureManager = new AdventureManagerImpl(this);
        this.versionManager = new VersionManagerImpl(this);
        this.scheduler = new SchedulerImpl(this);
        this.storageManager = new StorageManagerImpl(this);
        this.requirementManager = new RequirementManagerImpl(this);
        this.bossBarManager = new BossBarManagerImpl(this);
        this.imageManager = new ImageManagerImpl(this);
        this.placeholderManager = new PlaceholderManagerImpl(this);
        this.backGroundManager = new BackGroundManagerImpl(this);
        this.resourcePackManager = new ResourcePackManagerImpl(this);
        this.nameplateManager = new NameplateManagerImpl(this);
        this.teamManager = new TeamManagerImpl(this);
        this.widthManager = new WidthManagerImpl(this);
        this.bubbleManager = new BubbleManagerImpl(this);
        this.actionBarManager = new ActionBarManagerImpl(this);
        this.coolDownManager = new CoolDownManager(this);
        this.packetManager = new PacketManager(this);
        this.reload(CNConfig.generatePackOnStart);
        new CommandManager(this).load();
        this.versionManager.checkUpdate().thenAccept(outDated -> {
            if (!outDated) this.getAdventure().sendConsoleMessage("[CustomNameplates] You are using the latest version.");
            else this.getAdventure().sendConsoleMessage("[CustomNameplates] Update is available: <u>https://polymart.org/resource/2543<!u>");
        });
    }

    @Override
    public void onDisable() {
        ((SchedulerImpl) this.scheduler).shutdown();
        ((ActionBarManagerImpl) actionBarManager).unload();
        ((NameplateManagerImpl) this.nameplateManager).disable();
        ((TeamManagerImpl) this.teamManager).unload();
        ((BossBarManagerImpl) this.bossBarManager).unload();
        ((ImageManagerImpl) this.imageManager).unload();
        ((BackGroundManagerImpl) this.backGroundManager).unload();
        ((PlaceholderManagerImpl) this.placeholderManager).unload();
        ((BubbleManagerImpl) this.bubbleManager).unload();
        ((RequirementManagerImpl) this.requirementManager).unload();
        ((ResourcePackManagerImpl) this.resourcePackManager).unload();
        ((WidthManagerImpl) this.widthManager).unload();
        ((StorageManagerImpl) this.storageManager).disable();
        ((AdventureManagerImpl) this.adventureManager).close();
    }

    @Override
    public void reload(boolean generatePack) {
        CNConfig.load();
        CNLocale.load();
        ((SchedulerImpl) this.scheduler).reload();
        ((AdventureManagerImpl) this.adventureManager).reload();
        ((NameplateManagerImpl) this.nameplateManager).reload();
        ((BubbleManagerImpl) this.bubbleManager).reload();
        ((BackGroundManagerImpl) this.backGroundManager).reload();
        ((TeamManagerImpl) this.teamManager).reload();
        ((StorageManagerImpl) this.storageManager).reload();
        ((RequirementManagerImpl) this.requirementManager).reload();
        ((BossBarManagerImpl) this.bossBarManager).reload();
        ((ActionBarManagerImpl) actionBarManager).reload();
        ((ImageManagerImpl) this.imageManager).reload();
        ((PlaceholderManagerImpl) this.placeholderManager).reload();
        ((WidthManagerImpl) this.widthManager).reload();
        ((ResourcePackManagerImpl) this.resourcePackManager).reload();
        this.resourcePackManager.generateResourcePack();
        CustomNameplatesReloadEvent event = new CustomNameplatesReloadEvent(this);
        this.getServer().getPluginManager().callEvent(event);
    }

    @Override
    public YamlConfiguration getConfig(String file) {
        File config = new File(this.getDataFolder(), file);
        if (!config.exists()) this.saveResource(file, false);
        return YamlConfiguration.loadConfiguration(config);
    }

    @Override
    public void debug(String s) {
        if (CNConfig.debug) {
            LogUtils.info(s);
        }
    }

    private void loadLibraries() {
        String mavenRepo = TimeZone.getDefault().getID().startsWith("Asia") ?
                "https://maven.aliyun.com/repository/public/" : "https://repo.maven.apache.org/maven2/";
        LibraryLoader.loadDependencies(
                "org.apache.commons:commons-pool2:2.12.0", mavenRepo,
                "redis.clients:jedis:5.1.0", mavenRepo,
                "dev.dejvokep:boosted-yaml:1.3.1", mavenRepo,
                "com.zaxxer:HikariCP:5.0.1", mavenRepo,
                "org.mariadb.jdbc:mariadb-java-client:3.3.0", mavenRepo,
                "com.mysql:mysql-connector-j:8.2.0", mavenRepo,
                "commons-io:commons-io:2.14.0", mavenRepo,
                "com.google.code.gson:gson:2.10.1", mavenRepo,
                "com.h2database:h2:2.2.224", mavenRepo,
                "org.mongodb:mongodb-driver-sync:4.11.1", mavenRepo,
                "org.mongodb:mongodb-driver-core:4.11.1", mavenRepo,
                "org.mongodb:bson:4.11.1", mavenRepo,
                "org.xerial:sqlite-jdbc:3.43.2.2", mavenRepo,
                "dev.jorel:commandapi-bukkit-shade:9.3.0", mavenRepo
        );
    }

    public CoolDownManager getCoolDownManager() {
        return coolDownManager;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }
}
