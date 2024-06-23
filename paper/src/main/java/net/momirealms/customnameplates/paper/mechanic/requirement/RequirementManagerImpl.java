/*
 *  Copyright (C) <2022> <XiaoMoMi>
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

package net.momirealms.customnameplates.paper.mechanic.requirement;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.manager.RequirementManager;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.requirement.RequirementExpansion;
import net.momirealms.customnameplates.api.requirement.RequirementFactory;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.common.Pair;
import net.momirealms.customnameplates.paper.CustomNameplatesPluginImpl;
import net.momirealms.customnameplates.paper.mechanic.requirement.papi.PapiCondition;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import net.momirealms.customnameplates.paper.util.ClassUtils;
import net.momirealms.customnameplates.paper.util.ConfigUtils;
import net.momirealms.customnameplates.paper.util.DisguiseUtils;
import net.momirealms.customnameplates.paper.util.GeyserUtils;
import net.momirealms.sparrow.heart.SparrowHeart;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class RequirementManagerImpl implements RequirementManager {

    private final CustomNameplatesPluginImpl plugin;
    private final String EXPANSION_FOLDER = "expansions/requirement";
    private final HashMap<String, RequirementFactory> requirementBuilderMap;

    public RequirementManagerImpl(CustomNameplatesPluginImpl plugin) {
        this.plugin = plugin;
        this.requirementBuilderMap = new HashMap<>(64);
        this.registerInbuiltRequirements();
    }

    public void load() {
        this.loadExpansions();
    }

    public void unload() {

    }

    public void reload() {
        unload();
        load();
    }

    public void disable() {
        this.requirementBuilderMap.clear();
    }

    /**
     * Registers a custom requirement type with its corresponding factory.
     *
     * @param type               The type identifier of the requirement.
     * @param requirementFactory The factory responsible for creating instances of the requirement.
     * @return True if registration was successful, false if the type is already registered.
     */
    @Override
    public boolean registerRequirement(String type, RequirementFactory requirementFactory) {
        if (this.requirementBuilderMap.containsKey(type)) return false;
        this.requirementBuilderMap.put(type, requirementFactory);
        return true;
    }

    /**
     * Unregisters a custom requirement type.
     *
     * @param type The type identifier of the requirement to unregister.
     * @return True if unregistration was successful, false if the type is not registered.
     */
    @Override
    public boolean unregisterRequirement(String type) {
        return this.requirementBuilderMap.remove(type) != null;
    }

    private void registerInbuiltRequirements() {
        this.registerTimeRequirement();
        this.registerYRequirement();
        this.registerContainRequirement();
        this.registerStartWithRequirement();
        this.registerEndWithRequirement();
        this.registerEqualsRequirement();
        this.registerBiomeRequirement();
        this.registerDateRequirement();
        this.registerPermissionRequirement();
        this.registerWorldRequirement();
        this.registerWeatherRequirement();
        this.registerGreaterThanRequirement();
        this.registerAndRequirement();
        this.registerOrRequirement();
        this.registerLevelRequirement();
        this.registerRandomRequirement();
        this.registerCoolDownRequirement();
        this.registerLessThanRequirement();
        this.registerNumberEqualRequirement();
        this.registerRegexRequirement();
        this.registerEnvironmentRequirement();
        this.registerPotionEffectRequirement();
        this.registerPapiRequirement();
        this.registerInListRequirement();
        this.registerGameModeRequirement();
        this.registerGeyserRequirement();
        this.registerDisguisedRequirement();
        this.registerDisguisedTypeRequirement();
    }

    /**
     * Retrieves an array of requirements based on a configuration section.
     *
     * @param section The configuration section containing requirement definitions.
     * @return An array of Requirement objects based on the configuration section
     */
    @NotNull
    @Override
    public Requirement[] getRequirements(ConfigurationSection section) {
        List<Requirement> requirements = new ArrayList<>();
        if (section == null) {
            return requirements.toArray(new Requirement[0]);
        }
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            String typeOrName = entry.getKey();
            if (hasRequirement(typeOrName)) {
                requirements.add(getRequirement(typeOrName, entry.getValue()));
            } else {
                requirements.add(getRequirement(section.getConfigurationSection(typeOrName)));
            }
        }
        return requirements.toArray(new Requirement[0]);
    }

    public boolean hasRequirement(String type) {
        return requirementBuilderMap.containsKey(type);
    }

    /**
     * Retrieves a Requirement object based on a configuration section and advanced flag.
     *
     * @param section  The configuration section containing requirement definitions.
     * @return A Requirement object based on the configuration section, or an EmptyRequirement if the section is null or invalid.
     */
    @NotNull
    @Override
    public Requirement getRequirement(ConfigurationSection section) {
        if (section == null) return EmptyRequirement.instance;
        String type = section.getString("type");
        if (type == null) {
            LogUtils.warn("No requirement type found at " + section.getCurrentPath());
            return EmptyRequirement.instance;
        }
        var builder = getRequirementFactory(type);
        if (builder == null) {
            return EmptyRequirement.instance;
        }
        return builder.build(section.get("value"));
    }

    /**
     * Gets a requirement based on the provided key and value.
     * If a valid RequirementFactory is found for the key, it is used to create the requirement.
     * If no factory is found, a warning is logged, and an empty requirement instance is returned.
     *
     * @param type   The key representing the requirement type.
     * @param value The value associated with the requirement.
     * @return A Requirement instance based on the key and value, or an empty requirement if not found.
     */
    @Override
    @NotNull
    public Requirement getRequirement(String type, Object value) {
        RequirementFactory factory = getRequirementFactory(type);
        if (factory == null) {
            LogUtils.warn("Requirement type: " + type + " doesn't exist.");
            return EmptyRequirement.instance;
        }
        return factory.build(value);
    }

    /**
     * Retrieves a RequirementFactory based on the specified requirement type.
     *
     * @param type The requirement type for which to retrieve a factory.
     * @return A RequirementFactory for the specified type, or null if no factory is found.
     */
    @Override
    @Nullable
    public RequirementFactory getRequirementFactory(String type) {
        return requirementBuilderMap.get(type);
    }

    private void registerTimeRequirement() {
        registerRequirement("time", (args) -> {
            List<Pair<Integer, Integer>> timePairs = ConfigUtils.stringListArgs(args).stream().map(it -> ConfigUtils.splitStringIntegerArgs(it, "~")).toList();
            return condition -> {
                long time = Objects.requireNonNull(condition.getOfflinePlayer().getPlayer()).getWorld().getTime();
                for (Pair<Integer, Integer> pair : timePairs)
                    if (time >= pair.left() && time <= pair.right())
                        return true;
                return false;
            };
        });
    }

    private void registerYRequirement() {
        registerRequirement("ypos", (args) -> {
            List<Pair<Integer, Integer>> timePairs = ConfigUtils.stringListArgs(args).stream().map(it -> ConfigUtils.splitStringIntegerArgs(it, "~")).toList();
            return condition -> {
                int y = Objects.requireNonNull(condition.getOfflinePlayer().getPlayer()).getLocation().getBlockY();
                for (Pair<Integer, Integer> pair : timePairs)
                    if (y >= pair.left() && y <= pair.right())
                        return true;
                return false;
            };
        });
    }

    private void registerOrRequirement() {
        registerRequirement("||", (args) -> {
            if (args instanceof ConfigurationSection section) {
                Requirement[] requirements = getRequirements(section);
                return condition -> {
                    for (Requirement requirement : requirements) {
                        if (requirement.isConditionMet(condition)) {
                            return true;
                        }
                    }
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at || requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerAndRequirement() {
        registerRequirement("&&", (args) -> {
            if (args instanceof ConfigurationSection section) {
                Requirement[] requirements = getRequirements(section);
                return condition -> {
                    outer: {
                        for (Requirement requirement : requirements) {
                            if (!requirement.isConditionMet(condition)) {
                                break outer;
                            }
                        }
                        return true;
                    }
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at && requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerLevelRequirement() {
        registerRequirement("level", (args) -> {
            int level = (int) args;
            return condition -> {
                int current = Objects.requireNonNull(condition.getOfflinePlayer().getPlayer()).getLevel();
                return current >= level;
            };
        });
    }

    private void registerRandomRequirement() {
        registerRequirement("random", (args) -> {
            double random = ConfigUtils.getDoubleValue(args);
            return condition -> Math.random() < random;
        });
    }

    private void registerGeyserRequirement() {
        registerRequirement("geyser", (args) -> {
            boolean arg = (boolean) args;
            return condition -> {
                if (arg) {
                    return GeyserUtils.isBedrockPlayer(condition.getOfflinePlayer().getUniqueId());
                } else {
                    return !GeyserUtils.isBedrockPlayer(condition.getOfflinePlayer().getUniqueId());
                }
            };
        });
    }

    private void registerDisguisedRequirement() {
        registerRequirement("self-disguised", (args) -> {
            boolean arg = (boolean) args;
            return condition -> {
                if (!CNConfig.hasLibsDisguise) return true;
                if (arg) {
                    return DisguiseUtils.isDisguised(condition.getOfflinePlayer().getPlayer());
                } else {
                    return !DisguiseUtils.isDisguised(condition.getOfflinePlayer().getPlayer());
                }
            };
        });
    }

    private void registerBiomeRequirement() {
        registerRequirement("biome", (args) -> {
            HashSet<String> biomes = new HashSet<>(ConfigUtils.stringListArgs(args));
            return condition -> {
                String currentBiome = SparrowHeart.getInstance().getBiomeResourceLocation(Objects.requireNonNull(condition.getOfflinePlayer().getPlayer()).getLocation());
                return biomes.contains(currentBiome);
            };
        });
        registerRequirement("!biome", (args) -> {
            HashSet<String> biomes = new HashSet<>(ConfigUtils.stringListArgs(args));
            return condition -> {
                String currentBiome = SparrowHeart.getInstance().getBiomeResourceLocation(Objects.requireNonNull(condition.getOfflinePlayer().getPlayer()).getLocation());
                return !biomes.contains(currentBiome);
            };
        });
    }

    private void registerDisguisedTypeRequirement() {
        registerRequirement("disguised-type", (args) -> {
            HashSet<String> types = new HashSet<>(ConfigUtils.stringListArgs(args));
            return condition -> {
                if (!CNConfig.hasLibsDisguise) return true;
                Player player = condition.getOfflinePlayer().getPlayer();
                if (!DisguiseUtils.isDisguised(player)) return false;
                return types.contains(DisguiseUtils.getDisguisedType(player).name());
            };
        });
        registerRequirement("!disguised-type", (args) -> {
            HashSet<String> types = new HashSet<>(ConfigUtils.stringListArgs(args));
            return condition -> {
                if (!CNConfig.hasLibsDisguise) return true;
                Player player = condition.getOfflinePlayer().getPlayer();
                if (!DisguiseUtils.isDisguised(player)) return false;
                return !types.contains(DisguiseUtils.getDisguisedType(player).name());
            };
        });
    }

    private void registerWorldRequirement() {
        registerRequirement("world", (args) -> {
            HashSet<String> worlds = new HashSet<>(ConfigUtils.stringListArgs(args));
            return condition -> worlds.contains(Objects.requireNonNull(condition.getOfflinePlayer().getPlayer()).getWorld().getName());
        });
        registerRequirement("!world", (args) -> {
            HashSet<String> worlds = new HashSet<>(ConfigUtils.stringListArgs(args));
            return condition -> !worlds.contains(Objects.requireNonNull(condition.getOfflinePlayer().getPlayer()).getWorld().getName());
        });
    }

    private void registerWeatherRequirement() {
        registerRequirement("weather", (args) -> {
            List<String> weathers = ConfigUtils.stringListArgs(args);
            return condition -> {
                String currentWeather;
                World world = Objects.requireNonNull(condition.getOfflinePlayer().getPlayer()).getWorld();
                if (world.isClearWeather()) currentWeather = "clear";
                else if (world.isThundering()) currentWeather = "thunder";
                else currentWeather = "rain";
                for (String weather : weathers)
                    if (weather.equalsIgnoreCase(currentWeather))
                        return true;
                return false;
            };
        });
    }

    private void registerCoolDownRequirement() {
        registerRequirement("cooldown", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String key = section.getString("key");
                int time = section.getInt("time");
                return condition -> !plugin.getCoolDownManager().isCoolDown(condition.getOfflinePlayer().getUniqueId(), key, time);
            } else {
                LogUtils.warn("Wrong value format found at cooldown requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerDateRequirement() {
        registerRequirement("date", (args) -> {
            HashSet<String> dates = new HashSet<>(ConfigUtils.stringListArgs(args));
            return condition -> {
                Calendar calendar = Calendar.getInstance();
                String current = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE);
                return dates.contains(current);
            };
        });
    }

    private void registerPermissionRequirement() {
        registerRequirement("permission", (args) -> {
            List<String> perms = ConfigUtils.stringListArgs(args);
            return condition -> {
                for (String perm : perms)
                    if (Objects.requireNonNull(condition.getOfflinePlayer().getPlayer()).hasPermission(perm))
                        return true;
                return false;
            };
        });
        registerRequirement("!permission", (args) -> {
            List<String> perms = ConfigUtils.stringListArgs(args);
            return condition -> {
                for (String perm : perms)
                    if (Objects.requireNonNull(condition.getOfflinePlayer().getPlayer()).hasPermission(perm))
                        return false;
                return true;
            };
        });
    }

    @SuppressWarnings("DuplicatedCode")
    private void registerGreaterThanRequirement() {
        registerRequirement(">=", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return Double.parseDouble(p1) >= Double.parseDouble(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at >= requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement(">", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return Double.parseDouble(p1) > Double.parseDouble(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at > requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerRegexRequirement() {
        registerRequirement("regex", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("papi", "");
                String v2 = section.getString("regex", "");
                return condition -> PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1).matches(v2);
            } else {
                LogUtils.warn("Wrong value format found at regex requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerNumberEqualRequirement() {
        registerRequirement("=", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return Double.parseDouble(p1) == Double.parseDouble(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at !startsWith requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("!=", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return Double.parseDouble(p1) != Double.parseDouble(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at !startsWith requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    @SuppressWarnings("DuplicatedCode")
    private void registerLessThanRequirement() {
        registerRequirement("<", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return Double.parseDouble(p1) < Double.parseDouble(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at < requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("<=", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return Double.parseDouble(p1) <= Double.parseDouble(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at <= requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerStartWithRequirement() {
        registerRequirement("startsWith", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return p1.startsWith(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at startsWith requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("!startsWith", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return !p1.startsWith(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at !startsWith requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerEndWithRequirement() {
        registerRequirement("endsWith", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return p1.endsWith(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at endsWith requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("!endsWith", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return !p1.endsWith(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at !endsWith requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerContainRequirement() {
        registerRequirement("contains", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return p1.contains(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at contains requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("!contains", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return !p1.contains(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at !contains requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerEqualsRequirement() {
        registerRequirement("equals", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return p1.equals(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at equals requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("!equals", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return condition -> {
                    String p1 = v1.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), v2) : v2;
                    return !p1.equals(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at !equals requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerPapiRequirement() {
        registerRequirement("papi-condition", (args) -> {
            if (args instanceof ConfigurationSection section) {
                return new PapiCondition(section.getValues(false));
            } else {
                LogUtils.warn("Wrong value format found at papi-condition requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerEnvironmentRequirement() {
        registerRequirement("environment", (args) -> {
            List<String> environments = ConfigUtils.stringListArgs(args);
            return condition -> {
                var name = condition.getOfflinePlayer().getPlayer().getWorld().getEnvironment().name().toLowerCase(Locale.ENGLISH);
                return environments.contains(name);
            };
        });
        registerRequirement("!environment", (args) -> {
            List<String> environments = ConfigUtils.stringListArgs(args);
            return condition -> {
                var name = condition.getOfflinePlayer().getPlayer().getWorld().getEnvironment().name().toLowerCase(Locale.ENGLISH);
                return !environments.contains(name);
            };
        });
    }

    private void registerGameModeRequirement() {
        registerRequirement("gamemode", (args) -> {
            List<String> modes = ConfigUtils.stringListArgs(args);
            return condition -> {
                var name = condition.getOfflinePlayer().getPlayer().getGameMode().name().toLowerCase(Locale.ENGLISH);
                return modes.contains(name);
            };
        });
        registerRequirement("!gamemode", (args) -> {
            List<String> modes = ConfigUtils.stringListArgs(args);
            return condition -> {
                var name = condition.getOfflinePlayer().getPlayer().getGameMode().name().toLowerCase(Locale.ENGLISH);
                return !modes.contains(name);
            };
        });
    }


    private void registerPotionEffectRequirement() {
        registerRequirement("potion-effect", (args) -> {
            String potions = (String) args;
            String[] split = potions.split("(<=|>=|<|>|==)", 2);
            PotionEffectType type = PotionEffectType.getByName(split[0]);
            if (type == null) {
                LogUtils.warn("Potion effect doesn't exist: " + split[0]);
                return EmptyRequirement.instance;
            }
            int required = Integer.parseInt(split[1]);
            String operator = potions.substring(split[0].length(), potions.length() - split[1].length());
            return condition -> {
                int level = -1;
                PotionEffect potionEffect = Objects.requireNonNull(condition.getOfflinePlayer().getPlayer()).getPotionEffect(type);
                if (potionEffect != null) {
                    level = potionEffect.getAmplifier();
                }
                boolean result = false;
                switch (operator) {
                    case ">=" -> {
                        if (level >= required) result = true;
                    }
                    case ">" -> {
                        if (level > required) result = true;
                    }
                    case "==" -> {
                        if (level == required) result = true;
                    }
                    case "!=" -> {
                        if (level != required) result = true;
                    }
                    case "<=" -> {
                        if (level <= required) result = true;
                    }
                    case "<" -> {
                        if (level < required) result = true;
                    }
                }
                return result;
            };
        });
    }

    private void registerInListRequirement() {
        registerRequirement("in-list", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String papi = section.getString("papi", "");
                List<String> values = ConfigUtils.stringListArgs(section.get("values"));
                return condition -> {
                    String p1 = PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), papi);
                    return values.contains(p1);
                };
            } else {
                LogUtils.warn("Wrong value format found at in-list requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("!in-list", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String papi = section.getString("papi", "");
                List<String> values = ConfigUtils.stringListArgs(section.get("values"));
                return condition -> {
                    String p1 = PlaceholderAPI.setPlaceholders(condition.getOfflinePlayer(), papi);
                    return !values.contains(p1);
                };
            } else {
                LogUtils.warn("Wrong value format found at in-list requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    /**
     * Loads requirement expansions from external JAR files located in the expansion folder.
     * Each expansion JAR should contain classes that extends the RequirementExpansion class.
     * Expansions are registered and used to create custom requirements.
     * If an error occurs while loading or initializing an expansion, a warning message is logged.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadExpansions() {
        File expansionFolder = new File(plugin.getDataFolder(), EXPANSION_FOLDER);
        if (!expansionFolder.exists())
            expansionFolder.mkdirs();

        List<Class<? extends RequirementExpansion>> classes = new ArrayList<>();
        File[] expansionJars = expansionFolder.listFiles();
        if (expansionJars == null) return;
        for (File expansionJar : expansionJars) {
            if (expansionJar.getName().endsWith(".jar")) {
                try {
                    Class<? extends RequirementExpansion> expansionClass = ClassUtils.findClass(expansionJar, RequirementExpansion.class);
                    classes.add(expansionClass);
                } catch (IOException | ClassNotFoundException e) {
                    LogUtils.warn("Failed to load expansion: " + expansionJar.getName(), e);
                }
            }
        }
        try {
            for (Class<? extends RequirementExpansion> expansionClass : classes) {
                RequirementExpansion expansion = expansionClass.getDeclaredConstructor().newInstance();
                unregisterRequirement(expansion.getRequirementType());
                registerRequirement(expansion.getRequirementType(), expansion.getRequirementFactory());
                LogUtils.info("Loaded requirement expansion: " + expansion.getRequirementType() + "[" + expansion.getVersion() + "]" + " by " + expansion.getAuthor());
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            LogUtils.warn("Error occurred when creating expansion instance.", e);
        }
    }
}
