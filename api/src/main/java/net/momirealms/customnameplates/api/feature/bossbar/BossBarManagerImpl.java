package net.momirealms.customnameplates.api.feature.bossbar;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.JoinQuitListener;
import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.ConfigUtils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarManagerImpl implements BossBarManager, JoinQuitListener {

    private final CustomNameplates plugin;
    private final LinkedHashMap<String, BossBarConfig> configs = new LinkedHashMap<>();
    private final ConcurrentHashMap<UUID, BossBarDisplayController> senders = new ConcurrentHashMap<>();
    private BossBarConfig[] configArray = new BossBarConfig[0];

    public BossBarManagerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        // ignore disabled modules
        if (!ConfigManager.bossbarModule()) return;
        this.loadConfig();
        this.resetArray();
        for (CNPlayer<?> online : plugin.getOnlinePlayers()) {
            onPlayerJoin(online);
        }
    }

    @Override
    public void unload() {
        for (BossBarDisplayController sender : senders.values()) {
            sender.destroy();
        }
        this.senders.clear();
        this.configs.clear();
        this.resetArray();
    }

    @Override
    public void onTick() {
        for (BossBarDisplayController sender : senders.values()) {
            sender.onTick();
        }
    }

    private void resetArray() {
        configArray = configs.values().toArray(new BossBarConfig[0]);
    }

    @Override
    public void onPlayerJoin(CNPlayer<?> player) {
        if (!ConfigManager.bossbarModule()) return;
        BossBarDisplayController sender = new BossBarDisplayController(this, player);
        BossBarDisplayController previous = senders.put(player.uuid(), sender);
        if (previous != null) {
            previous.destroy();
        }
    }

    @Override
    public void onPlayerQuit(CNPlayer<?> player) {
        BossBarDisplayController sender = senders.remove(player.uuid());
        if (sender != null) {
            sender.destroy();
        }
    }

    @Override
    public BossBarConfig getConfig(String name) {
        return configs.get(name);
    }

    @Override
    public BossBarConfig[] allConfigs() {
        return configArray;
    }

    private void loadConfig() {
        plugin.getConfigManager().saveResource("configs" + File.separator + "bossbar.yml");
        YamlDocument document = plugin.getConfigManager().loadData(new File(plugin.getDataDirectory().toFile(), "configs" + File.separator + "bossbar.yml"));
        for (Map.Entry<String, Object> entry : document.getStringRouteMappedValues(false).entrySet()) {
            if (!(entry.getValue() instanceof Section section))
                return;
            this.configs.put(entry.getKey(),
                    BossBarConfig.builder()
                            .id(entry.getKey())
                            .overlay(BossBar.Overlay.valueOf(section.getString("overlay", "PROGRESS").toUpperCase(Locale.ENGLISH)))
                            .color(BossBar.Color.valueOf(section.getString("color", "YELLOW").toUpperCase(Locale.ENGLISH)))
                            .requirement(plugin.getRequirementManager().parseRequirements(section.getSection("conditions")))
                            .carouselText(
                                section.contains("text") ?
                                new CarouselText[]{new CarouselText(-1, new Requirement[0], section.getString("text"), false)} :
                                ConfigUtils.carouselTexts(section.getSection("text-display-order"))
                            )
                            .progress(section.getFloat("progress", 0f))
                            .build()
            );
        }
    }
}
