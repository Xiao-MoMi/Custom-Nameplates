package net.momirealms.customnameplates.bukkit.requirement;

import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.requirement.AbstractRequirementManager;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.bukkit.requirement.builtin.*;
import net.momirealms.customnameplates.common.util.ListUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class BukkitRequirementManager extends AbstractRequirementManager {

    public BukkitRequirementManager(CustomNameplates plugin) {
        super(plugin);
    }

    @Override
    protected void registerBuiltInRequirements() {
        this.registerPermission();
        this.registerBiome();
        this.registerTime();
        this.registerY();
        this.registerWorld();
        this.registerWeather();
        this.registerDate();
        this.registerLevel();
        this.registerRandom();
        this.registerCooldown();
        this.registerEnvironment();
        this.registerPotionEffect();
        this.registerGameMode();
    }

    private void registerPermission() {
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new PermissionRequirement(interval, list);
        }, "permission");
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new NotPermissionRequirement(interval, list);
        }, "!permission");
    }

    private void registerBiome() {
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new BiomeRequirement(interval, list);
        }, "biome");
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new NotBiomeRequirement(interval, list);
        }, "!biome");
    }

    private void registerTime() {
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new TimeRequirement(interval, list);
        }, "time");
    }

    private void registerY() {
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new YRequirement(interval, list);
        }, "ypos");
    }

    private void registerWorld() {
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new WorldRequirement(interval, list);
        }, "world");
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new NotWorldRequirement(interval, list);
        }, "!world");
    }

    private void registerWeather() {
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new WeatherRequirement(interval, list);
        }, "weather");
    }

    private void registerDate() {
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new DateRequirement(interval, list);
        }, "date");
    }

    private void registerLevel() {
        this.registerRequirement((args, interval) -> {
            int level = (int) args;
            return new LevelRequirement(interval, level);
        }, "level");
    }

    private void registerRandom() {
        this.registerRequirement((args, interval) -> {
            double random = (double) args;
            return new RandomRequirement(interval, random);
        }, "random");
    }

    private void registerCooldown() {
        this.registerRequirement((args, interval) -> {
            if (args instanceof ConfigurationSection section) {
                String key = section.getString("key");
                int time = section.getInt("time");
                return new CooldownRequirement(interval, key, time);
            }
            else {
                return Requirement.empty();
            }
        }, "cooldown");
    }

    private void registerEnvironment() {
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new EnvironmentRequirement(interval, list);
        }, "environment");
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new NotEnvironmentRequirement(interval, list);
        }, "!environment");
    }

    private void registerPotionEffect() {
        this.registerRequirement((args, interval) -> {
            String potions = (String) args;
            String[] split = potions.split("(<=|>=|<|>|==)", 2);
            PotionEffectType type = PotionEffectType.getByName(split[0]);
            if (type == null) {
                plugin.getPluginLogger().warn("Potion effect doesn't exist: " + split[0]);
                return Requirement.empty();
            }
            int required = Integer.parseInt(split[1]);
            String operator = potions.substring(split[0].length(), potions.length() - split[1].length());
            return new PotionEffectRequirement(interval, type, operator, required);
        }, "potion");
    }

    private void registerGameMode() {
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new GameModeRequirement(interval, list);
        }, "gamemode");
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new NotGameModeRequirement(interval, list);
        }, "!gamemode");
    }
}
