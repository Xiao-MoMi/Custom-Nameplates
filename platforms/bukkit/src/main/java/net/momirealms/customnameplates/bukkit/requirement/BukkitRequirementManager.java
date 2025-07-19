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

package net.momirealms.customnameplates.bukkit.requirement;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.ConfigUtils;
import net.momirealms.customnameplates.backend.requirement.AbstractRequirementManager;
import net.momirealms.customnameplates.bukkit.requirement.builtin.*;
import net.momirealms.customnameplates.common.util.ListUtils;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;

public class BukkitRequirementManager extends AbstractRequirementManager {

    public BukkitRequirementManager(CustomNameplates plugin) {
        super(plugin);
    }

    @Override
    protected void registerPlatformRequirements() {
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
        this.registerBedrock();
        this.registerDisguise();
        this.registerPassenger();
        this.registerTeammates();
    }

    private void registerTeammates() {
        this.registerRequirement((args, interval) -> {
            boolean is = (boolean) args;
            return new TeammatesRequirement(interval, is);
        }, "teammates");
    }

    private void registerPassenger() {
        this.registerRequirement((args, interval) -> {
            boolean is = (boolean) args;
            return new PassengerRequirement(interval, is);
        }, "is-passenger");
    }

    private void registerDisguise() {
        this.registerRequirement((args, interval) -> {
            boolean is = (boolean) args;
            return new DisguiseRequirement(interval, is);
        }, "self-disguised");
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new DisguiseTypeRequirement(interval, new HashSet<>(list));
        }, "disguised-type");
        this.registerRequirement((args, interval) -> {
            List<String> list = ListUtils.toList(args);
            return new NotDisguiseTypeRequirement(interval, new HashSet<>(list));
        }, "!disguised-type");
    }

    private void registerBedrock() {
        this.registerRequirement((args, interval) -> {
            boolean is = (boolean) args;
            return new BedrockPlayerRequirement(interval, is);
        }, "is-bedrock-player");
        this.registerRequirement((args, interval) -> {
            boolean is = (boolean) args;
            return new GeyserRequirement(interval, is);
        }, "geyser");
        this.registerRequirement((args, interval) -> {
            boolean is = (boolean) args;
            return new FloodGateRequirement(interval, is);
        }, "floodgate");
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
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            String key = section.getString("key");
            int time = section.getInt("time");
            return new CooldownRequirement(interval, key, time);
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
        }, "potion-effect");
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
