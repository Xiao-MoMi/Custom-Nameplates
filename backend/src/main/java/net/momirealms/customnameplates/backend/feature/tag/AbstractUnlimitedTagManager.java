/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.backend.feature.tag;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.utils.format.NodeRole;
import net.momirealms.customnameplates.api.AbstractCNPlayer;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.feature.PlayerListener;
import net.momirealms.customnameplates.api.feature.tag.NameTagConfig;
import net.momirealms.customnameplates.api.feature.tag.TagRenderer;
import net.momirealms.customnameplates.api.feature.tag.UnlimitedTagManager;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.api.network.Tracker;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.Alignment;
import net.momirealms.customnameplates.api.util.ConfigUtils;
import net.momirealms.customnameplates.api.util.Vector3;
import net.momirealms.customnameplates.common.util.Tristate;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class AbstractUnlimitedTagManager implements UnlimitedTagManager, PlayerListener {
    protected final CustomNameplates plugin;
    protected final LinkedHashMap<String, NameTagConfig> configs = new LinkedHashMap<>();
    protected final ConcurrentHashMap<Integer, TagRendererImpl> tagRenderers = new ConcurrentHashMap<>();
    protected NameTagConfig[] configArray = new NameTagConfig[0];
    protected int previewDuration;
    protected boolean alwaysShow;

    public AbstractUnlimitedTagManager(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setTempPreviewing(CNPlayer player, boolean preview) {
        boolean isPreviewing = player.isTempPreviewing();
        if (isPreviewing) {
            if (preview) return;
            onRemovePlayer(player, player);
            player.removePlayerFromTracker(player);
            ((AbstractCNPlayer<?>) player).setTempPreviewing(false);
        } else {
            if (!preview) return;
            Tracker tracker = player.addPlayerToTracker(player);
            tracker.setScale(player.scale());
            tracker.setCrouching(player.isCrouching());
            tracker.setSpectator(player.isSpectator());
            onAddPlayer(player, player);
            ((AbstractCNPlayer<?>) player).setTempPreviewing(true);
        }
    }

    @Override
    public void togglePreviewing(CNPlayer player, boolean preview) {
        boolean isPreviewing = player.isToggleablePreviewing();
        if (isPreviewing) {
            if (preview) return;
            onRemovePlayer(player, player);
            player.removePlayerFromTracker(player);
            ((AbstractCNPlayer<?>) player).setToggleablePreviewing(false);
        } else {
            if (!preview) return;
            Tracker tracker = player.addPlayerToTracker(player);
            tracker.setScale(player.scale());
            tracker.setCrouching(player.isCrouching());
            tracker.setSpectator(player.isSpectator());
            onAddPlayer(player, player);
            ((AbstractCNPlayer<?>) player).setToggleablePreviewing(true);
        }
    }

    @Override
    public void onChangeWorld(CNPlayer player) {
        plugin.getScheduler().asyncLater(() -> {
            if (player.isOnline() && (player.isTempPreviewing() || player.isToggleablePreviewing())) {
                onRemovePlayer(player, player);
                onAddPlayer(player, player);
            }
        }, VersionHelper.isFolia() ? 500 : 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onRespawn(CNPlayer player) {
        plugin.getScheduler().asyncLater(() -> {
            if (player.isOnline() && (player.isTempPreviewing() || player.isToggleablePreviewing())) {
                onRemovePlayer(player, player);
                onAddPlayer(player, player);
            }
        }, VersionHelper.isFolia() ? 500 : 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onTeleport(CNPlayer player) {
        plugin.getScheduler().asyncLater(() -> {
            if (player.isOnline() && (player.isTempPreviewing() || player.isToggleablePreviewing())) {
                onRemovePlayer(player, player);
                onAddPlayer(player, player);
            }
        }, VersionHelper.isFolia() ? 500 : 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public int previewDuration() {
        return previewDuration;
    }

    @Override
    public void onPlayerJoin(CNPlayer player) {
        plugin.debug(() -> player.name() + " joined the server");
        TagRendererImpl renderer = new TagRendererImpl(this, player);
        TagRendererImpl previous = tagRenderers.put(player.entityID(), renderer);
        if (previous != null) {
            previous.destroy();
        }
        if (isAlwaysShow()) {
            setTempPreviewing(player, isAlwaysShow());
        } else if (player.isLoaded() && player.isTempPreviewing()) {
            setTempPreviewing(player, false);
        }
    }

    @Override
    public void onPlayerQuit(CNPlayer player) {
        TagRendererImpl renderer = tagRenderers.remove(player.entityID());
        if (renderer != null) {
            renderer.destroy();
        }
    }

    @Override
    public void load() {
        this.loadConfig();
        this.resetArray();
        for (CNPlayer online : plugin.getOnlinePlayers()) {
            onPlayerJoin(online);
        }
    }

    @Override
    public void unload() {
        for (TagRendererImpl sender : tagRenderers.values()) {
            sender.destroy();
        }
        this.tagRenderers.clear();
        this.configs.clear();
        this.resetArray();
    }

    @Override
    public void onTick() {
        for (TagRendererImpl sender : tagRenderers.values()) {
            sender.onTick();
        }
    }

    @Override
    public boolean isAlwaysShow() {
        return alwaysShow;
    }

    private void resetArray() {
        configArray = configs.values().toArray(new NameTagConfig[0]);
    }

    @Override
    public NameTagConfig configById(String name) {
        return configs.get(name);
    }

    @Override
    public NameTagConfig[] nameTagConfigs() {
        return configArray;
    }

    @Override
    public void onAddPlayer(CNPlayer owner, CNPlayer added) {
        TagRendererImpl controller = tagRenderers.get(owner.entityID());
        if (controller != null) {
            controller.handlePlayerAdd(added);
        }
    }

    @Override
    public TagRenderer getTagRender(CNPlayer owner) {
        return tagRenderers.get(owner.entityID());
    }

    @Override
    public void onRemovePlayer(CNPlayer owner, CNPlayer removed) {
        TagRendererImpl controller = tagRenderers.get(owner.entityID());
        if (controller != null) {
            controller.handlePlayerRemove(removed);
        }
    }

    @Override
    public void onPlayerDataSet(CNPlayer owner, CNPlayer viewer, boolean isCrouching) {
        TagRendererImpl controller = tagRenderers.get(owner.entityID());
        if (controller != null) {
            controller.handleEntityDataChange(viewer, isCrouching);
        }
    }

    @Override
    public void onPlayerAttributeSet(CNPlayer owner, CNPlayer viewer, double scale) {
        TagRendererImpl controller = tagRenderers.get(owner.entityID());
        if (controller != null) {
            controller.handleAttributeChange(viewer, scale);
        }
    }

    @Override
    public void onPlayerGameModeChange(CNPlayer owner, CNPlayer viewer, boolean isSpectator) {
        TagRendererImpl controller = tagRenderers.get(owner.entityID());
        if (controller != null) {
            controller.handleGameModeChange(viewer, isSpectator);
        }
    }

    private void loadConfig() {
        YamlDocument document = plugin.getConfigManager().loadConfig("configs" + File.separator + "nameplate.yml",
                GeneralSettings.builder()
                        .setRouteSeparator('.')
                        .setUseDefaults(false)
                        .build(),
                LoaderSettings
                        .builder()
                        .setAutoUpdate(true)
                        .build(),
                DumperSettings.builder()
                        .setEscapeUnprintable(false)
                        .setScalarFormatter((tag, value, role, def) -> {
                            if (role == NodeRole.KEY) {
                                return ScalarStyle.PLAIN;
                            } else {
                                return tag == Tag.STR ? ScalarStyle.DOUBLE_QUOTED : ScalarStyle.PLAIN;
                            }
                        })
                        .build(),
                UpdaterSettings
                        .builder()
                        .setVersioning(new BasicVersioning("config-version"))
                        .addIgnoredRoute(ConfigManager.configVersion(), "unlimited", '.')
                        .build());
        try {
            document.save(plugin.getConfigManager().resolveConfig("configs" + File.separator + "nameplate.yml").toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        previewDuration = document.getInt("preview-duration", 5);
        alwaysShow = document.getBoolean("always-show", false);
        Section unlimitedSection = document.getSection("unlimited");
        if (!ConfigManager.nametagModule() || unlimitedSection == null) return;
        for (Map.Entry<String, Object> entry : unlimitedSection.getStringRouteMappedValues(false).entrySet()) {
            if (!(entry.getValue() instanceof Section section))
                return;
            Vector3 translation = ConfigUtils.vector3(section.getString("translation", "0,0,0"));
            this.configs.put(entry.getKey(),
                    NameTagConfig.builder()
                            .id(entry.getKey())
                            .ownerRequirement(plugin.getRequirementManager().parseRequirements(section.getSection("owner-conditions")))
                            .viewerRequirement(plugin.getRequirementManager().parseRequirements(section.getSection("viewer-conditions")))
                            .translation(VersionHelper.isVersionNewerThan1_20_2() ? translation : translation.add(0,0.5,0))
                            .scale(ConfigUtils.vector3(section.getString("scale", "1,1,1")))
                            .alignment(Alignment.valueOf(section.getString("alignment", "CENTER")))
                            .viewRange(section.getFloat("view-range", 1f))
                            .shadowRadius(section.getFloat("shadow-radius", 0f))
                            .shadowStrength(section.getFloat("shadow-strength", 1f))
                            .lineWidth(section.getInt("line-width", 200))
                            .hasShadow(section.getBoolean("has-shadow", false))
                            .seeThrough(Tristate.fromBoolean((Boolean) section.get("is-see-through")))
                            .opacity(section.getByte("opacity", (byte) -1))
                            .useDefaultBackgroundColor(section.getBoolean("use-default-background-color", false))
                            .backgroundColor(ConfigUtils.argb(section.getString("background-color", "64,0,0,0")))
                            .affectedByCrouching(section.getBoolean("affected-by-crouching", true))
                            .affectedBySpectator(section.getBoolean("affected-by-spectator", true))
                            .affectedByScaling(section.getBoolean("affected-by-scale-attribute", true))
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
