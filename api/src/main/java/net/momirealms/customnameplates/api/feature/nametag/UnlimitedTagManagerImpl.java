package net.momirealms.customnameplates.api.feature.nametag;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.JoinQuitListener;
import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.Alignment;
import net.momirealms.customnameplates.api.util.ConfigUtils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UnlimitedTagManagerImpl implements UnlimitedTagManager, JoinQuitListener {

    private final CustomNameplates plugin;
    private final LinkedHashMap<String, TagConfig> configs = new LinkedHashMap<>();
    private final ConcurrentHashMap<UUID, TagDisplayController> tagControllers = new ConcurrentHashMap<>();
    private TagConfig[] configArray = new TagConfig[0];

    public UnlimitedTagManagerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerJoin(CNPlayer player) {
        if (!ConfigManager.nameplateModule()) return;
        TagDisplayController sender = new TagDisplayController(this, player);
        TagDisplayController previous = tagControllers.put(player.uuid(), sender);
        if (previous != null) {
            previous.destroy();
        }
    }

    @Override
    public void onPlayerQuit(CNPlayer player) {
        TagDisplayController sender = tagControllers.remove(player.uuid());
        if (sender != null) {
            sender.destroy();
        }
    }

    @Override
    public void load() {
        if (!ConfigManager.nameplateModule()) return;
        this.loadConfig();
        this.resetArray();
        for (CNPlayer online : plugin.getOnlinePlayers()) {
            onPlayerJoin(online);
        }
    }

    @Override
    public void unload() {
        for (TagDisplayController sender : tagControllers.values()) {
            sender.destroy();
        }
        this.tagControllers.clear();
        this.configs.clear();
        this.resetArray();
    }

    @Override
    public void onTick() {
        for (TagDisplayController sender : tagControllers.values()) {
            sender.onTick();
        }
    }

    private void resetArray() {
        configArray = configs.values().toArray(new TagConfig[0]);
    }

    @Override
    public TagConfig getConfig(String name) {
        return configs.get(name);
    }

    @Override
    public TagConfig[] allConfigs() {
        return configArray;
    }

    @Override
    public void onAddPlayer(CNPlayer player, CNPlayer added) {
        TagDisplayController controller = tagControllers.get(player.uuid());
        if (controller != null) {
            controller.handlePlayerAdd(added);
        }
    }

    @Override
    public void onRemovePlayer(CNPlayer player, CNPlayer removed) {
        TagDisplayController controller = tagControllers.get(player.uuid());
        if (controller != null) {
            controller.handlePlayerRemove(removed);
        }
    }

    private void loadConfig() {
        plugin.getConfigManager().saveResource("configs" + File.separator + "nameplate.yml");
        YamlDocument document = plugin.getConfigManager().loadData(new File(plugin.getDataDirectory().toFile(), "configs" + File.separator + "nameplate.yml"));
        Section unlimitedSection = document.getSection("unlimited");
        if (unlimitedSection == null) return;
        for (Map.Entry<String, Object> entry : unlimitedSection.getStringRouteMappedValues(false).entrySet()) {
            if (!(entry.getValue() instanceof Section section))
                return;
            this.configs.put(entry.getKey(),
                    TagConfig.builder()
                            .id(entry.getKey())
                            .ownerRequirement(plugin.getRequirementManager().parseRequirements(section.getSection("owner-conditions")))
                            .viewerRequirement(plugin.getRequirementManager().parseRequirements(section.getSection("viewer-conditions")))
                            .translation(ConfigUtils.vector3(section.getString("translation", "0,1,0")))
                            .scale(ConfigUtils.vector3(section.getString("scale", "1,1,1")))
                            .alignment(Alignment.valueOf(section.getString("alignment", "CENTER")))
                            .viewRange(section.getFloat("view-range", 1f))
                            .shadowRadius(section.getFloat("shadow-radius", 0f))
                            .shadowStrength(section.getFloat("shadow-strength", 1f))
                            .lineWidth(section.getInt("line-width", 200))
                            .hasShadow(section.getBoolean("has-shadow", false))
                            .seeThrough(section.getBoolean("is-see-through", false))
                            .useDefaultBackgroundColor(section.getBoolean("use-default-background-color", false))
                            .carouselText(
                                    section.contains("text") ?
                                            new CarouselText[]{new CarouselText(-1, new Requirement[0], section.getString("text"), false)} :
                                            ConfigUtils.carouselTexts(section.getSection("text-display-order"))
                            )
                            .build()
            );
        }
    }
}
