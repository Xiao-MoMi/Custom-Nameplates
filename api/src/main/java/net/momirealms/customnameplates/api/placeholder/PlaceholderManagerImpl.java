package net.momirealms.customnameplates.api.placeholder;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.Feature;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderManagerImpl implements PlaceholderManager {

    private static final Pattern PATTERN = Pattern.compile("%([^%]*)%");

    private final CustomNameplates plugin;
    private final HashMap<String, Integer> refreshIntervals = new HashMap<>();

    private final Map<String, Placeholder> placeholders = new HashMap<>();
    private final Map<String, SharedPlaceholder> sharedPlaceholders = new HashMap<>();

    public PlaceholderManagerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        YamlDocument document = ConfigManager.getMainConfig();
        Section section = document.getSection("other-settings.placeholder-refresh-interval");
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                String id = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof Integer) {
                    refreshIntervals.put(id, (Integer) value);
                }
            }
        }
    }

    @Override
    public void unload() {
        this.refreshIntervals.clear();
        this.placeholders.clear();
    }

    @Override
    public void refreshPlaceholders() {

        // updates all the shared value
        for (SharedPlaceholder placeholder : sharedPlaceholders.values()) {
            placeholder.update();
        }

        for (CNPlayer<?> player : plugin.getOnlinePlayers()) {

            if (!player.isOnline()) continue;

            PlaceholderUpdateTask task = player.getRefreshValueTask();
            if (task == null) continue;
            task.run();

            Set<Feature> featuresToNotifyUpdates = new HashSet<>();
            for (Map.Entry<SharedPlaceholder, String> entry : task.getSharedResults().entrySet()) {
                SharedPlaceholder placeholder = entry.getKey();
                String value = entry.getValue();
                String previous = player.setValue(placeholder.id(), value);
                if (!value.equals(previous)) {
                    featuresToNotifyUpdates.addAll(player.getUsedFeatures(placeholder));
                }
            }

            for (Map.Entry<PlayerPlaceholder, String> entry : task.getPlayerResults().entrySet()) {
                PlayerPlaceholder placeholder = entry.getKey();
                String value = entry.getValue();
                String previous = player.setValue(placeholder.id(), value);
                if (!value.equals(previous)) {
                    featuresToNotifyUpdates.addAll(player.getUsedFeatures(placeholder));
                }
            }

            // Update only for players with a mutual relationship
            Map<Feature, List<CNPlayer<?>>> relationalFeaturesToNotifyUpdates = new HashMap<>();
            for (Map.Entry<RelationalPlaceholder, Map<CNPlayer<?>, String>> entry : task.getRelationalResults().entrySet()) {
                RelationalPlaceholder placeholder = entry.getKey();
                Map<CNPlayer<?>, String> value = entry.getValue();
                for (Map.Entry<CNPlayer<?>, String> relationalEntry : value.entrySet()) {
                    String newValue = relationalEntry.getValue();
                    String previous = player.setRelationalValue(placeholder.id(), relationalEntry.getKey(), newValue);
                    if (!newValue.equals(previous)) {
                        for (Feature feature : player.getUsedFeatures(placeholder)) {
                            // Filter features that will not be updated for all players
                            if (!featuresToNotifyUpdates.contains(feature)) {
                                List<CNPlayer<?>> players = relationalFeaturesToNotifyUpdates.computeIfAbsent(feature, k -> new ArrayList<>());
                                players.add(relationalEntry.getKey());
                            }
                        }
                    }
                }
            }

            // Switch to another thread for updating
            plugin.getScheduler().async().execute(() -> {
                // Async task takes time and the player might have been offline
                if (!player.isOnline()) return;
                for (Feature feature : featuresToNotifyUpdates) {
                    feature.notifyPlaceholderUpdates(player, false);
                }
                for (Map.Entry<Feature, List<CNPlayer<?>>> innerEntry : relationalFeaturesToNotifyUpdates.entrySet()) {
                    Feature feature = innerEntry.getKey();
                    for (CNPlayer<?> other : innerEntry.getValue()) {
                        feature.notifyPlaceholderUpdates(player, other, false);
                    }
                }
            });
        }
    }

    @Override
    public int getRefreshInterval(String id) {
        return refreshIntervals.getOrDefault(id, ConfigManager.defaultRefreshInterval());
    }

    @Override
    public <T extends Placeholder> T registerPlaceholder(T placeholder) {
        placeholders.put(placeholder.id(), placeholder);
        if (placeholder instanceof SharedPlaceholder sharedPlaceholder) {
            sharedPlaceholders.put(placeholder.id(), sharedPlaceholder);
        }
        return placeholder;
    }

    @Override
    public PlayerPlaceholder registerPlayerPlaceholder(String id, int refreshInterval, Function<CNPlayer<?>, String> function) {
        PlayerPlaceholderImpl impl = new PlayerPlaceholderImpl(this, id, refreshInterval, function);
        return registerPlaceholder(impl);
    }

    @Override
    public RelationalPlaceholder registerRelationalPlaceholder(String id, int refreshInterval, BiFunction<CNPlayer<?>, CNPlayer<?>, String> function) {
        RelationalPlaceholderImpl impl = new RelationalPlaceholderImpl(this, id, refreshInterval, function);
        return registerPlaceholder(impl);
    }

    @Override
    public SharedPlaceholder registerSharedPlaceholder(String id, int refreshInterval, Supplier<String> contextSupplier) {
        SharedPlaceholderImpl impl = new SharedPlaceholderImpl(this, id, refreshInterval, contextSupplier);
        return registerPlaceholder(impl);
    }

    @Override
    public Placeholder getPlaceholder(String id) {
        Placeholder placeholder = placeholders.get(id);
        if (placeholder != null) return placeholder;
        return plugin.getPlatform().registerPlatformPlaceholder(id);
    }

    @Override
    public void unregisterPlaceholder(String id) {
        placeholders.remove(id);
    }

    @Override
    public void unregisterPlaceholder(Placeholder placeholder) {
        placeholders.remove(placeholder.id());
    }

    @Override
    public List<String> detectPlaceholders(String text) {
        int firstPercent = text.indexOf('%');
        if (firstPercent == -1) return Collections.emptyList();
        if (firstPercent == 0 && text.charAt(text.length() - 1) == '%' && text.indexOf('%', 1) == text.length() - 1) {
            return Collections.singletonList(text);
        }
        List<String> placeholders = new ArrayList<>();
        Matcher m = PATTERN.matcher(text);
        while (m.find()) {
            placeholders.add(m.group());
        }
        return placeholders;
    }
}
