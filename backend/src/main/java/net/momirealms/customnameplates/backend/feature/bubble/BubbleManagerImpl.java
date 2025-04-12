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

package net.momirealms.customnameplates.backend.feature.bubble;

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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.ChatListener;
import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;
import net.momirealms.customnameplates.api.feature.OffsetFont;
import net.momirealms.customnameplates.api.feature.bubble.Bubble;
import net.momirealms.customnameplates.api.feature.bubble.BubbleConfig;
import net.momirealms.customnameplates.api.feature.bubble.BubbleManager;
import net.momirealms.customnameplates.api.feature.bubble.ChannelMode;
import net.momirealms.customnameplates.api.feature.tag.TagRenderer;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.ConfigUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BubbleManagerImpl implements BubbleManager, ChatListener {

    private final CustomNameplates plugin;
    private final Map<String, Bubble> bubbles = new Object2ObjectOpenHashMap<>();
    private Requirement[] sendBubbleRequirements;
    private Requirement[] viewBubbleRequirements;
    private String defaultBubbleId;
    private double yOffset;
    private int stayDuration;
    private int appearDuration;
    private int disappearDuration;
    private float viewRange;
    private Set<String> blacklistChannels;
    private ChannelMode channelMode;
    private final Map<String, BubbleConfig> bubbleConfigs = new Object2ObjectOpenHashMap<>();

    public BubbleManagerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void unload() {
        this.bubbles.clear();
        this.bubbleConfigs.clear();
    }

    @Override
    public void load() {
        if (!ConfigManager.bubbleModule()) return;
        this.loadConfigs();
        this.loadConfig();
    }

    @Nullable
    @Override
    public Bubble bubbleById(String id) {
        return this.bubbles.get(id);
    }

    @Override
    public @Nullable BubbleConfig bubbleConfigById(String id) {
        return this.bubbleConfigs.get(id);
    }

    @Override
    public Collection<Bubble> bubbles() {
        return new ObjectArrayList<>(bubbles.values());
    }

    @Override
    public Collection<BubbleConfig> bubbleConfigs() {
        return new ObjectArrayList<>(bubbleConfigs.values());
    }

    @Override
    public boolean hasBubble(CNPlayer player, String id) {
        if (!this.bubbleConfigs.containsKey(id)) {
            return false;
        }
        return player.hasPermission("bubbles.equip." + id);
    }

    @Override
    public Collection<BubbleConfig> availableBubbles(CNPlayer player) {
        ArrayList<BubbleConfig> available = new ArrayList<>();
        for (BubbleConfig bubble : bubbleConfigs.values()) {
            if (player.hasPermission("bubbles.equip." + bubble.id())) {
                available.add(bubble);
            }
        }
        return available;
    }

    @Override
    public Set<String> blacklistChannels() {
        return blacklistChannels;
    }

    @Override
    public String defaultBubbleId() {
        return defaultBubbleId;
    }

    @Override
    public Requirement[] sendBubbleRequirements() {
        return sendBubbleRequirements;
    }

    @Override
    public Requirement[] viewBubbleRequirements() {
        return viewBubbleRequirements;
    }

    @Override
    public double verticalOffset() {
        return yOffset;
    }

    @Override
    public int stayDuration() {
        return stayDuration;
    }

    @Override
    public int appearDuration() {
        return appearDuration;
    }

    @Override
    public int disappearDuration() {
        return disappearDuration;
    }

    @Override
    public float viewRange() {
        return viewRange;
    }

    @Override
    public ChannelMode channelMode() {
        return channelMode;
    }

    private void loadConfig() {
        YamlDocument document = plugin.getConfigManager().loadConfig("configs" + File.separator + "bubble.yml",
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
                        .addIgnoredRoute(ConfigManager.configVersion(), "sender-requirements", '.')
                        .addIgnoredRoute(ConfigManager.configVersion(), "viewer-requirements", '.')
                        .addIgnoredRoute(ConfigManager.configVersion(), "bubble-settings", '.')
                        .build());
        try {
            document.save(plugin.getConfigManager().resolveConfig("configs" + File.separator + "bubble.yml").toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sendBubbleRequirements = plugin.getRequirementManager().parseRequirements(document.getSection("sender-requirements"));
        viewBubbleRequirements = plugin.getRequirementManager().parseRequirements(document.getSection("viewer-requirements"));
        defaultBubbleId = document.getString("default-bubble", "chat");
        yOffset = document.getDouble("y-offset", 0.2);
        stayDuration = document.getInt("stay-duration", 160);
        appearDuration = document.getInt("appear-duration", 20);
        disappearDuration = document.getInt("disappear-duration", 10);
        viewRange = document.getFloat("view-range", 0.5f);
        blacklistChannels = new HashSet<>(document.getStringList("blacklist-channels"));
        channelMode = ChannelMode.valueOf(document.getString("channel-mode", "ALL").toUpperCase(Locale.ENGLISH));
        Section bubbleSettings = document.getSection("bubble-settings");
        if (bubbleSettings != null) {
            for (Map.Entry<String, Object> entry : bubbleSettings.getStringRouteMappedValues(false).entrySet()) {
                String key = entry.getKey();
                if (entry.getValue() instanceof Section inner) {
                    int maxLines = inner.getInt("max-lines", 1);
                    Bubble[] bubbleArray = new Bubble[maxLines];
                    for (int i = 0; i < maxLines; i++) {
                        bubbleArray[i] = bubbleById(inner.getString("lines." + (i+1)));
                    }
                    this.bubbleConfigs.put(key, BubbleConfig.builder()
                            .id(key)
                            .maxLines(maxLines)
                            .bubbles(bubbleArray)
                            .displayName(inner.getString("display-name", key))
                            .lineWidth(inner.getInt("line-width", 100))
                            .backgroundColor(ConfigUtils.argb(inner.getString("background-color", "0,0,0,0")))
                            .textPrefix(inner.getString("text-prefix", "").replace("{namespace}", ConfigManager.namespace()))
                            .textSuffix(inner.getString("text-suffix", ""))
                            .scale(ConfigUtils.vector3(inner.getString("scale", "1,1,1")))
                            .build());
                }
            }
        }
    }

    private void loadConfigs() {
        File bubbleFolder = new File(plugin.getDataDirectory().toFile(), "contents" + File.separator + "bubbles");
        if (!bubbleFolder.exists() && bubbleFolder.mkdirs()) {
            saveDefaultBubbles();
        }
        List<File> configFiles = ConfigUtils.getConfigsDeeply(bubbleFolder);
        for (File configFile : configFiles) {
            YamlDocument config = plugin.getConfigManager().loadData(configFile);
            String id = configFile.getName().substring(0, configFile.getName().lastIndexOf("."));
            Bubble bubble = Bubble.builder()
                    .id(id)
                    .left(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("left.image") + ".png"),
                            config.getInt("left.ascent", 12),
                            config.getInt("left.height", 16)
                    ))
                    .middle(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("middle.image") + ".png"),
                            config.getInt("middle.ascent", 12),
                            config.getInt("middle.height", 16)
                    ))
                    .tail(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("tail.image") + ".png"),
                            config.getInt("tail.ascent", 12),
                            config.getInt("tail.height", 16)
                    ))
                    .right(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("right.image") + ".png"),
                            config.getInt("right.ascent", 12),
                            config.getInt("right.height", 16)
                    ))
                    .build();
            this.bubbles.put(id, bubble);
        }
    }

    private void saveDefaultBubbles() {
        String[] png_list = new String[]{"chat_1", "chat_2", "chat_3"};
        String[] part_list = new String[]{"_left.png", "_middle.png", "_right.png", "_tail.png", ".yml"};
        for (String name : png_list) {
            for (String part : part_list) {
                plugin.getConfigManager().saveResource("contents" + File.separator + "bubbles" + File.separatorChar + name + part);
            }
        }
    }

    @Override
    public void onPlayerChat(CNPlayer player, String message, String channel) {
        if (!ConfigManager.bubbleModule()) return;
        // ignore blacklist channels
        if (blacklistChannels().contains(channel)) return;
        // check requirements
        if (!player.isMet(sendBubbleRequirements())) return;

        String equippedBubble = player.currentBubble();
        if (equippedBubble.equals("none")) equippedBubble = defaultBubbleId;

        BubbleConfig config = bubbleConfigs.get(equippedBubble);
        if (config == null) {
            return;
        }

        String fullText = config.textPrefix().fastCreate(player).render(player) + message.replace("\\", "\\\\") + config.textSuffix().fastCreate(player).render(player);
        int lines = plugin.getAdvanceManager().getLines(fullText, config.lineWidth());
        if (lines > config.maxLines()) return;
        if (lines <= 0) return;
        int space = (int) plugin.getAdvanceManager().getLineAdvance(" ");
        String fakeSpace = AdventureHelper.surroundWithNameplatesFont(OffsetFont.createOffsets(space));
        fullText = fullText.replace(" ", fakeSpace);

        TagRenderer renderer = plugin.getUnlimitedTagManager().getTagRender(player);
        if (renderer == null) return;
        int removed = renderer.removeTagIf(tag -> tag.id().equals("bubble"));
        int delay = 0;
        if (removed != 0) {
            delay += disappearDuration;
        }

        Bubble bubble = config.bubbles()[lines - 1];
        float advance;
        if (lines == 1) {
            advance = plugin.getAdvanceManager().getLineAdvance(fullText);
        } else {
            advance = config.lineWidth();
        }

        BubbleTag bubbleTag = new BubbleTag(player, renderer, channel, config,
                AdventureHelper.miniMessageToMinecraftComponent(fullText),
                bubble == null ? null : AdventureHelper.miniMessageToMinecraftComponent(AdventureHelper.surroundWithNameplatesFont(bubble.createImage(advance, 1,1))), this);
        renderer.addTag(bubbleTag);
        if (delay != 0) {
            plugin.getScheduler().asyncLater(() -> bubbleTag.setCanShow(true), delay * 50L, TimeUnit.MILLISECONDS);
        } else {
            bubbleTag.setCanShow(true);
        }
    }
}
