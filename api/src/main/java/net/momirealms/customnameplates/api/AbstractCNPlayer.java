package net.momirealms.customnameplates.api;

import net.momirealms.customnameplates.api.feature.Feature;
import net.momirealms.customnameplates.api.placeholder.*;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.tracker.TrackedEntityView;

import java.util.*;

public abstract class AbstractCNPlayer<P> implements CNPlayer<P> {

    protected final CustomNameplates plugin;
    protected final P player;

    /**
     * 获取当前玩家被哪些玩家track了
     */
    protected TrackedEntityView<P> tracker;
    private boolean isLoaded = false;

    /**
     * 记录这个玩家被插件调度器tick了多少次，影响变量刷新速度
     */
    private int tickCounter = 0;

    /**
     * 此处缓存了所有解析过的变量，Relational则是缓存了与某个玩家的关系变量，仅更新当前处于active状态的变量。
     */
    private final HashMap<String, String> cachedValues = new HashMap<>();
    private final HashMap<String, WeakHashMap<CNPlayer<?>, String>> cachedRelationalValues = new HashMap<>();

    /**
     * 此处缓存了所有解析过的条件，Relational则是缓存了与某个玩家的关系条件
     */
    private final HashMap<Requirement, Boolean> cachedRequirements = new HashMap<>();
    private final HashMap<Requirement, WeakHashMap<CNPlayer<?>, Boolean>> cachedRelationalRequirements = new HashMap<>();

    /*
     * 这里维护了一个双向的Map以方便更新对应的Feature。
     * 插件会先获取当前处于活跃状态的变量（由Feature提供），根据变量的更新情况，判断是否需要反馈到对应的Feature以便只在必要的时刻进行更新
     */
    private final Set<Feature> activeFeatures = new HashSet<>();
    private final HashMap<Placeholder, Set<Feature>> placeholder2Features = new HashMap<>();
    private final HashMap<Feature, Set<Placeholder>> feature2Placeholders = new HashMap<>();

    protected AbstractCNPlayer(CustomNameplates plugin, P player) {
        this.plugin = plugin;
        this.player = player;
    }

    /**
     * 将所有处于激活状态的变量统筹起来并返回一个更新任务
     *
     * @return 更新任务
     */
    @Override
    public PlaceholderUpdateTask getRefreshValueTask() {
        tickCounter++;
        Placeholder[] activePlaceholders = activePlaceholders();
        List<Placeholder> placeholdersToUpdate = new ArrayList<>();
        for (Placeholder placeholder : activePlaceholders) {
            int interval = placeholder.refreshInterval();
            if (interval > 0 && tickCounter % interval == 0) {
                placeholdersToUpdate.add(placeholder);
            }
        }
        if (placeholdersToUpdate.isEmpty()) return null;
        return new PlaceholderUpdateTask(this, placeholdersToUpdate);
    }

    public void reload() {
        clearValueCache();
        activeFeatures.clear();
        placeholder2Features.clear();
        feature2Placeholders.clear();
    }

    private void clearValueCache() {
        cachedValues.clear();
        cachedRelationalValues.clear();
    }

    @Override
    public Set<Feature> getUsedFeatures(Placeholder placeholder) {
        return placeholder2Features.getOrDefault(placeholder, Collections.emptySet());
    }

    @Override
    public P player() {
        return player;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void addFeature(Feature feature) {
        activeFeatures.add(feature);
        Set<Placeholder> allPlaceholdersUsedInFeature = feature.allPlaceholders();
        feature2Placeholders.put(feature, allPlaceholdersUsedInFeature);
        for (Placeholder placeholder : allPlaceholdersUsedInFeature) {
            Set<Feature> featureSet = placeholder2Features.computeIfAbsent(placeholder, k -> {
                forceUpdate(Set.of(placeholder));
                return new HashSet<>();
            });
            featureSet.add(feature);
        }
    }

    @Override
    public void removeFeature(Feature feature) {
        activeFeatures.remove(feature);
        Set<Placeholder> placeholders = feature2Placeholders.remove(feature);
        for (Placeholder placeholder : placeholders) {
            Set<Feature> featureSet = placeholder2Features.get(placeholder);
            featureSet.remove(feature);
            if (featureSet.isEmpty()) placeholder2Features.remove(placeholder);
        }
    }

    @Override
    public String setValue(String id, String value) {
        return cachedValues.put(id, value);
    }

    @Override
    public String setRelationalValue(String id, CNPlayer<?> another, String value) {
        WeakHashMap<CNPlayer<?>, String> map = cachedRelationalValues.computeIfAbsent(id, k -> new WeakHashMap<>());
        return map.put(another, value);
    }

    @Override
    public String getValue(String id) {
        return cachedValues.getOrDefault(id, id);
    }

    @Override
    public String getRelationalValue(String id, CNPlayer<?> another) {
        WeakHashMap<CNPlayer<?>, String> map = cachedRelationalValues.get(id);
        if (map == null) {
            return id;
        }
        return map.getOrDefault(another, "");
    }

    @Override
    public Placeholder[] activePlaceholders() {
        HashSet<Placeholder> placeholders = new HashSet<>();
        for (Feature feature : activeFeatures) {
            placeholders.addAll(feature.activePlaceholders());
        }
        return placeholders.toArray(new Placeholder[0]);
    }

    @Override
    public boolean isMet(Requirement[] requirements) {
        for (Requirement requirement : requirements) {
            boolean finalResult = Objects.requireNonNullElseGet(cachedRequirements.get(requirement), () -> requirement.isSatisfied(this, null));
            if (!finalResult) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isMet(CNPlayer<?> another, Requirement[] requirements) {
        for (Requirement requirement : requirements) {
            WeakHashMap<CNPlayer<?>, Boolean> innerMap = cachedRelationalRequirements.get(requirement);
            if (innerMap != null) {
                boolean finalResult = Objects.requireNonNullElseGet(innerMap.get(another), () -> requirement.isSatisfied(this, another));
                if (!finalResult) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void forceUpdate(Set<Placeholder> placeholders) {
        for (Placeholder placeholder : placeholders) {
            if (placeholder instanceof SharedPlaceholder sharedPlaceholder) {
                setValue(placeholder.id(), sharedPlaceholder.getLatestValue());
            } else if (placeholder instanceof PlayerPlaceholder playerPlaceholder) {
                setValue(placeholder.id(), playerPlaceholder.request(this));
            } else if (placeholder instanceof RelationalPlaceholder relational) {
                for (CNPlayer<?> player : CustomNameplates.getInstance().getOnlinePlayers()) {
                    setRelationalValue(placeholder.id(), player, relational.request(this, player));
                }
            }
        }
    }

    @Override
    public TrackedEntityView<P> getTracker() {
        return tracker;
    }
}
