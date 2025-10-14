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

package net.momirealms.customnameplates.bukkit;

import it.unimi.dsi.fastutil.ints.IntList;
import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.Platform;
import net.momirealms.customnameplates.api.feature.bossbar.BossBar;
import net.momirealms.customnameplates.api.feature.tag.NameTagConfig;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.api.network.PacketEvent;
import net.momirealms.customnameplates.api.network.Tracker;
import net.momirealms.customnameplates.api.placeholder.DummyPlaceholder;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.api.util.Alignment;
import net.momirealms.customnameplates.api.util.Vector3;
import net.momirealms.customnameplates.backend.feature.actionbar.ActionBarManagerImpl;
import net.momirealms.customnameplates.bukkit.util.BiomeUtils;
import net.momirealms.customnameplates.bukkit.util.EntityData;
import net.momirealms.customnameplates.bukkit.util.Reflections;
import net.momirealms.customnameplates.common.util.TriConsumer;
import net.momirealms.customnameplates.common.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class BukkitPlatform implements Platform {

    private static final Pattern LANG_PATTERN = Pattern.compile("<lang:[a-zA-Z0-9._/':-]+/?>");

    private final CustomNameplates plugin;
    private final boolean placeholderAPI;
    private final boolean geyser;
    private final boolean floodGate;
    private final boolean libsDisguises;
    private static Object serializer;

    private static final HashMap<String, TriConsumer<CNPlayer, PacketEvent, Object>> packetFunctions = new HashMap<>();

    private static void registerPacketConsumer(final TriConsumer<CNPlayer, PacketEvent, Object> functions, String... packet) {
        for (String s : packet) {
            packetFunctions.put(s, functions);
        }
    }

    public boolean hasPlaceholderAPI() {
        return placeholderAPI;
    }

    public boolean hasGeyser() {
        return geyser;
    }

    public boolean hasFloodGate() {
        return floodGate;
    }

    public boolean hasLibsDisguises() {
        return libsDisguises;
    }

    public BukkitPlatform(CustomNameplates plugin) {
        this.plugin = plugin;
        this.placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        this.geyser = Bukkit.getPluginManager().getPlugin("Geyser-Spigot") != null;
        this.floodGate = Bukkit.getPluginManager().getPlugin("floodgate") != null;
        this.libsDisguises = Bukkit.getPluginManager().getPlugin("LibsDisguises") != null;
        try {
            Object builder = Reflections.method$GsonComponentSerializer$builder.invoke(null);
            serializer = Reflections.method$GsonComponentSerializer$Builder$build.invoke(builder);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    static {
//        ThrowableFunction<Object, String> scoreContentNameFunction = VersionHelper.isVersionNewerThan1_21_2() ? (o -> {
//            @SuppressWarnings("unchecked")
//            Optional<String> optional = (Optional<String>) Reflections.method$Either$right.invoke(o);
//            return optional.orElse(null);
//        }) : (o -> (String) o);
        registerPacketConsumer((player, event, packet) -> {
            if (!ConfigManager.actionbarModule()) return;
            if (!ConfigManager.catchOtherActionBar()) return;
            if (!player.shouldCNTakeOverActionBar()) return;
            try {
                // some plugins would send null to clear the actionbar, what a bad solution
                Object component = Reflections.field$ClientboundSetActionBarTextPacket$text.get(packet);
                if (component == null && !VersionHelper.isVersionNewerThan1_20_5()) {
                    // paper api, must be from other plugins
                    Object adventureComponent = Reflections.field$ClientboundSetActionBarTextPacket$adventure$text.get(packet);
                    if (adventureComponent != null) {
                        String json = (String) Reflections.method$ComponentSerializer$serialize.invoke(serializer, adventureComponent);
                        CustomNameplates.getInstance().getScheduler().async().execute(() -> {
                            ((ActionBarManagerImpl) CustomNameplates.getInstance().getActionBarManager()).handleActionBarPacket(player, AdventureHelper.jsonToMiniMessage(json));
                        });
                    } else {
                        // bungeecord components ?
                    }
                } else {
                    // mc components
                    Object contents = Reflections.method$Component$getContents.invoke(component);
                    if (contents == null) {
                        return;
                    }
                    if (Reflections.clazz$ScoreContents.isAssignableFrom(contents.getClass())) {
                        //String name = scoreContentNameFunction.apply(Reflections.field$ScoreContents$name.get(contents));
                        String objective = (String) Reflections.field$ScoreContents$objective.get(contents);
                        if ("actionbar".equals(objective)) return;
                    }
                    CustomNameplates.getInstance().getScheduler().async().execute(() -> {
                        ((ActionBarManagerImpl) CustomNameplates.getInstance().getActionBarManager()).handleActionBarPacket(player, AdventureHelper.minecraftComponentToMiniMessage(component));
                    });
                }
            } catch (ReflectiveOperationException e) {
                CustomNameplates.getInstance().getPluginLogger().severe("Failed to handle ClientboundSetActionBarTextPacket", e);
            }
            event.cancelled(true);
        }, "ClientboundSetActionBarTextPacket");

        registerPacketConsumer((player, event, packet) -> {
            try {
                Object gameProfile = Reflections.field$ClientboundLoginFinishedPacket$gameProfile.get(packet);
                if (gameProfile != null) {
                    String name = (String) Reflections.field$GameProfile$name.get(gameProfile);
                    BukkitCNPlayer bukkitCNPlayer = (BukkitCNPlayer) player;
                    bukkitCNPlayer.setName(name);
                }
            } catch (ReflectiveOperationException e) {
                CustomNameplates.getInstance().getPluginLogger().severe("Failed to handle ClientboundGameProfilePacket", e);
            }
        }, "PacketLoginOutSuccess", "ClientboundLoginFinishedPacket", "ClientboundGameProfilePacket");

        registerPacketConsumer((player, event, packet) -> {
            if (!ConfigManager.actionbarModule()) return;
            if (!ConfigManager.catchOtherActionBar()) return;
            if (!player.shouldCNTakeOverActionBar()) return;
            try {
            boolean actionBar = (boolean) Reflections.field$ClientboundSystemChatPacket$overlay.get(packet);
                if (actionBar) {
                    CustomNameplates.getInstance().getScheduler().async().execute(() -> {
                        try {
                            String miniMessage;
                            if (VersionHelper.isVersionNewerThan1_20_4()) {
                                // 1.20.4+
                                Object component = Reflections.field$ClientboundSystemChatPacket$component.get(packet);
                                if (component == null) return;
                                miniMessage = AdventureHelper.minecraftComponentToMiniMessage(component);
                            } else {
                                // 1.20.4-
                                String json = (String) Reflections.field$ClientboundSystemChatPacket$text.get(packet);
                                if (json == null) return;
                                miniMessage = AdventureHelper.jsonToMiniMessage(json);
                            }
                            if (LANG_PATTERN.matcher(miniMessage).find()) {
                                if (ConfigManager.displaySystemChat()) {
                                    ((ActionBarManagerImpl) CustomNameplates.getInstance().getActionBarManager()).temporarilyHideCustomActionBar(player);
                                    Object com = AdventureHelper.miniMessageToMinecraftComponent(miniMessage, "nameplates", "actionbar");
                                    Object pkt = CustomNameplates.getInstance().getPlatform().setActionBarTextPacket(com);
                                    CustomNameplates.getInstance().getPacketSender().sendPacket(player, pkt);
                                }
                                event.cancelled(true);
                                return;
                            }
                            ((ActionBarManagerImpl) CustomNameplates.getInstance().getActionBarManager()).handleActionBarPacket(player, miniMessage);
                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    event.cancelled(true);
                }
            } catch (ReflectiveOperationException e) {
                CustomNameplates.getInstance().getPluginLogger().severe("Failed to handle ClientboundSystemChatPacket", e);
            }
        }, "ClientboundSystemChatPacket");

        // 1.20.2+
        registerPacketConsumer((player, event, packet) -> {
            if (!VersionHelper.isVersionNewerThan1_20_2()) return;
            try {
                int entityID = (int) Reflections.field$ClientboundAddEntityPacket$entityId.get(packet);
                CNPlayer added = CustomNameplates.getInstance().getPlayer(entityID);
                if (added != null) {
                    Tracker tracker = added.addPlayerToTracker(player);
                    tracker.setSpectator(added.isSpectator());
                    CustomNameplates.getInstance().getUnlimitedTagManager().onAddPlayer(added, player);
                }
            } catch (ReflectiveOperationException e) {
                CustomNameplates.getInstance().getPluginLogger().severe("Failed to handle ClientboundAddEntityPacket", e);
            }
        }, "ClientboundAddEntityPacket", "PacketPlayOutSpawnEntity");

        // 1.19.4-1.20.1
        registerPacketConsumer((player, event, packet) -> {
            if (VersionHelper.isVersionNewerThan1_20_2()) return;
            try {
                int entityID = (int) Reflections.field$PacketPlayOutNamedEntitySpawn$entityId.get(packet);
                CNPlayer added = CustomNameplates.getInstance().getPlayer(entityID);
                if (added != null) {
                    Tracker tracker = added.addPlayerToTracker(player);
                    tracker.setSpectator(added.isSpectator());
                    CustomNameplates.getInstance().getUnlimitedTagManager().onAddPlayer(added, player);
                }
            } catch (ReflectiveOperationException e) {
                CustomNameplates.getInstance().getPluginLogger().severe("Failed to handle PacketPlayOutNamedEntitySpawn", e);
            }
        }, "PacketPlayOutNamedEntitySpawn");

        registerPacketConsumer((player, event, packet) -> {
            try {
                IntList intList = (IntList) Reflections.field$ClientboundRemoveEntitiesPacket$entityIds.get(packet);
                for (int i : intList) {
                    CNPlayer removed = CustomNameplates.getInstance().getPlayer(i);
                    if (removed != null) {
                        removed.removePlayerFromTracker(player);
                        CustomNameplates.getInstance().getUnlimitedTagManager().onRemovePlayer(removed, player);
                    }
                }
            } catch (ReflectiveOperationException e) {
                CustomNameplates.getInstance().getPluginLogger().severe("Failed to handle ClientboundRemoveEntitiesPacket", e);
            }
        }, "PacketPlayOutEntityDestroy", "ClientboundRemoveEntitiesPacket");

        // for skin plugin compatibility
        registerPacketConsumer((player, event, packet) -> {
            try {
                if (!player.isInitialized()) return;
                UUID pUUID = player.uuid();
                @SuppressWarnings("unchecked")
                List<UUID> uuids = (List<UUID>) Reflections.field$ClientboundPlayerInfoRemovePacket$profileIds.get(packet);
                for (UUID uuid : uuids) {
                    if (uuid.equals(pUUID)) {
                        CNPlayer removed = CustomNameplates.getInstance().getPlayer(uuid);
                        if (removed != null) {
                            removed.removePlayerFromTracker(player);
                            CustomNameplates.getInstance().getUnlimitedTagManager().onRemovePlayer(removed, player);
                        }
                    }
                }
            } catch (ReflectiveOperationException e) {
                CustomNameplates.getInstance().getPluginLogger().severe("Failed to handle ClientboundPlayerInfoRemovePacket", e);
            }
        }, "ClientboundPlayerInfoRemovePacket");

        registerPacketConsumer((player, event, packet) -> {
            try {
                EnumSet<?> enums = (EnumSet<?>) Reflections.field$ClientboundPlayerInfoUpdatePacket$actions.get(packet);
                if (enums == null) return;
                boolean add_player = enums.contains(Reflections.enum$ClientboundPlayerInfoUpdatePacket$Action$ADD_PLAYER);
                boolean update_gamemode = enums.contains(Reflections.enum$ClientboundPlayerInfoUpdatePacket$Action$UPDATE_GAME_MODE);
                if (add_player || update_gamemode) {
                    @SuppressWarnings("unchecked")
                    List<Object> entries = (List<Object>) Reflections.field$ClientboundPlayerInfoUpdatePacket$entries.get(packet);
                    for (Object entry : entries) {
                        UUID uuid = (UUID) Reflections.field$ClientboundPlayerInfoUpdatePacket$Entry$profileId.get(entry);
                        if (uuid == null) continue;

                        // for skin plugin compatibility
                        if (add_player && player.isInitialized()) {
                            UUID pUUID = player.uuid();
                            if (uuid.equals(pUUID) && (player.isTempPreviewing() || player.isToggleablePreviewing() || CustomNameplates.getInstance().getUnlimitedTagManager().isAlwaysShow())) {
                                Tracker tracker = player.addPlayerToTracker(player);
                                tracker.setScale(player.scale());
                                tracker.setCrouching(player.isCrouching());
                                tracker.setSpectator(player.isSpectator());
                                CustomNameplates.getInstance().getUnlimitedTagManager().onAddPlayer(player, player);
                            }
                        }

                        CNPlayer another = CustomNameplates.getInstance().getPlayer(uuid);
                        if (update_gamemode) {
                            Object gameType = Reflections.field$ClientboundPlayerInfoUpdatePacket$Entry$gameMode.get(entry);
                            if (gameType == null) continue;
                            int mode = (int) Reflections.method$GameType$getId.invoke(gameType);
                            boolean isSpectator = mode == 3;
                            if (another != null) {
                                CustomNameplates.getInstance().getUnlimitedTagManager().onPlayerGameModeChange(another, player, isSpectator);
                            }
                        }
                    }
                }
            } catch (ReflectiveOperationException e) {
                CustomNameplates.getInstance().getPluginLogger().severe("Failed to handle ClientboundPlayerInfoUpdatePacket", e);
            }
        }, "ClientboundPlayerInfoUpdatePacket");

        // for cosmetic plugin compatibility
        registerPacketConsumer((player, event, packet) -> {
            try {
                int[] passengers = (int[]) Reflections.field$ClientboundSetPassengersPacket$passengers.get(packet);
                int vehicle = (int) Reflections.field$ClientboundSetPassengersPacket$vehicle.get(packet);
                CNPlayer another = CustomNameplates.getInstance().getPlayer(vehicle);
                if (another != null) {
                    Set<Integer> otherEntities = another.getTrackedPassengerIds(player);
                    for (int passenger : passengers) {
                        otherEntities.add(passenger);
                    }
                    int[] merged = new int[otherEntities.size()];
                    int index = 0;
                    for (Integer element : otherEntities) {
                        merged[index++] = element;
                    }
                    Reflections.field$ClientboundSetPassengersPacket$passengers.set(packet, merged);
                }
            } catch (ReflectiveOperationException e) {
                CustomNameplates.getInstance().getPluginLogger().severe("Failed to handle ClientboundSetPassengersPacket", e);
            }
        }, "PacketPlayOutMount", "ClientboundSetPassengersPacket");

        registerPacketConsumer((player, event, packet) -> {
            if (!VersionHelper.isVersionNewerThan1_20_5()) return;
            try {
                int entityID = (int) Reflections.field$ClientboundUpdateAttributesPacket$id.get(packet);
                CNPlayer another = CustomNameplates.getInstance().getPlayer(entityID);
                if (another != null) {
                    @SuppressWarnings("unchecked")
                    List<Object> attributes = (List<Object>) Reflections.field$ClientboundUpdateAttributesPacket$attributes.get(packet);
                    for (Object attributeSnapshot : attributes) {
                        Object attributeHolder = Reflections.field$ClientboundUpdateAttributesPacket$AttributeSnapshot$attribute.get(attributeSnapshot);
                        Object attribute = Reflections.method$Holder$value.invoke(attributeHolder);
                        String id = (String) Reflections.field$Attribute$id.get(attribute);
                        if (id.endsWith("scale")) {
                            double baseValue = (double) Reflections.field$ClientboundUpdateAttributesPacket$AttributeSnapshot$base.get(attributeSnapshot);
                            @SuppressWarnings("unchecked")
                            Collection<Object> modifiers = (Collection<Object>) Reflections.field$ClientboundUpdateAttributesPacket$AttributeSnapshot$modifiers.get(attributeSnapshot);
                            int left = modifiers.size();
                            if (left > 0) {
                                for (Object modifier : modifiers) {
                                    Object operation = Reflections.field$AttributeModifier$operation.get(modifier);
                                    if (operation == Reflections.instance$AttributeModifier$Operation$ADD_VALUE) {
                                        double amount = (double) Reflections.field$AttributeModifier$amount.get(modifier);
                                        baseValue += amount;
                                        left--;
                                        if (left == 0) break;
                                    }
                                }
                            }
                            double finalValue = baseValue;
                            if (left > 0) {
                                for (Object modifier : modifiers) {
                                    Object operation = Reflections.field$AttributeModifier$operation.get(modifier);
                                    if (operation == Reflections.instance$AttributeModifier$Operation$ADD_MULTIPLIED_BASE) {
                                        double amount = (double) Reflections.field$AttributeModifier$amount.get(modifier);
                                        finalValue += amount * baseValue;
                                        left--;
                                        if (left == 0) break;
                                    }
                                }
                            }
                            if (left > 0) {
                                for (Object modifier : modifiers) {
                                    Object operation = Reflections.field$AttributeModifier$operation.get(modifier);
                                    if (operation == Reflections.instance$AttributeModifier$Operation$ADD_MULTIPLIED_TOTAL) {
                                        double amount = (double) Reflections.field$AttributeModifier$amount.get(modifier);
                                        finalValue *= 1.0 + amount;
                                        left--;
                                        if (left == 0) break;
                                    }
                                }
                            }
                            CustomNameplates.getInstance().getUnlimitedTagManager().onPlayerAttributeSet(another, player, finalValue);
                            return;
                        }
                    }
                }
            } catch (ReflectiveOperationException e) {
                CustomNameplates.getInstance().getPluginLogger().severe("Failed to handle ClientboundUpdateAttributesPacket", e);
            }
        }, "ClientboundUpdateAttributesPacket", "PacketPlayOutUpdateAttributes");

        registerPacketConsumer((player, event, packet) -> {
            try {
                int entityID = (int) Reflections.field$ClientboundSetEntityDataPacket$id.get(packet);
                CNPlayer another = CustomNameplates.getInstance().getPlayer(entityID);
                if (another != null) {
                    @SuppressWarnings("unchecked")
                    List<Object> dataValues = (List<Object>) Reflections.field$ClientboundSetEntityDataPacket$packedItems.get(packet);
                    for (Object dataValue : dataValues) {
                        int id = (int) Reflections.field$SynchedEntityData$DataValue$id.get(dataValue);
                        if (id == 0) {
                            byte value = (byte) Reflections.field$SynchedEntityData$DataValue$value.get(dataValue);
                            boolean isCrouching = EntityData.isCrouching(value);
                            CustomNameplates.getInstance().getUnlimitedTagManager().onPlayerDataSet(another, player, isCrouching);
                            return;
                        }
                    }
                }
            } catch (ReflectiveOperationException e) {
                CustomNameplates.getInstance().getPluginLogger().severe("Failed to handle ClientboundSetEntityDataPacket", e);
            }
        }, "ClientboundSetEntityDataPacket", "PacketPlayOutEntityMetadata");

        // not a perfect solution but would work in most cases
        registerPacketConsumer((player, event, packet) -> {
            if (!ConfigManager.nametagModule()) return;
            if (!ConfigManager.hideTeamNames()) return;
            try {
                int method = (int) Reflections.field$ClientboundSetPlayerTeamPacket$method.get(packet);
                String teamName = (String) Reflections.field$ClientboundSetPlayerTeamPacket$name.get(packet);
                switch (method) {
                    // create
                    case 0 -> {
                        @SuppressWarnings("unchecked")
                        Collection<String> entities = (Collection<String>) Reflections.field$ClientboundSetPlayerTeamPacket$players.get(packet);
                        player.teamView().addTeamMembers(teamName, entities);
                        // additional check for those teams with only one member
                        if (entities.size() <= 1) {
                            for (String entity : entities) {
                                // is a player
                                if (!UUIDUtils.isUUID(entity)) {
                                    Player p = Bukkit.getPlayer(entity);
                                    // it's a fake player
                                    if (p == null) {
                                        return;
                                    }
                                    // do not hide team name for the viewer
                                    if (player.name().equals(entity)) {
                                        return;
                                    }
                                }
                            }
                        }
                        @SuppressWarnings("unchecked")
                        Optional<Object> optionalParameters = (Optional<Object>) Reflections.field$ClientboundSetPlayerTeamPacket$parameters.get(packet);
                        if (optionalParameters.isPresent()) {
                            Object parameters = optionalParameters.get();
                            Reflections.field$ClientboundSetPlayerTeamPacket$Parameters$nametagVisibility.set(parameters, VersionHelper.isVersionNewerThan1_21_5() ? Reflections.instance$Team$Visibility$NEVER : "never");
                        }
                    }
                    // remove
                    case 1 -> {
                        player.teamView().removeTeam(teamName);
                    }
                    // update
                    case 2 -> {
                        Set<String> members = player.teamView().getTeamMembers(teamName);
                        if (members == null) return;
                        if (members.size() <= 1) {
                            for (String entity : members) {
                                // is a player
                                if (!UUIDUtils.isUUID(entity)) {
                                    Player p = Bukkit.getPlayer(entity);
                                    // it's a fake player
                                    if (p == null) {
                                        return;
                                    }
                                    if (player.name().equals(entity)) {
                                        return;
                                    }
                                }
                            }
                        }
                        @SuppressWarnings("unchecked")
                        Optional<Object> optionalParameters = (Optional<Object>) Reflections.field$ClientboundSetPlayerTeamPacket$parameters.get(packet);
                        if (optionalParameters.isPresent()) {
                            Object parameters = optionalParameters.get();
                            Reflections.field$ClientboundSetPlayerTeamPacket$Parameters$nametagVisibility.set(parameters, VersionHelper.isVersionNewerThan1_21_5() ? Reflections.instance$Team$Visibility$NEVER : "never");
                        }
                    }
                    // add members
                    case 3 -> {
                        @SuppressWarnings("unchecked")
                        Collection<String> entities = (Collection<String>) Reflections.field$ClientboundSetPlayerTeamPacket$players.get(packet);
                        player.teamView().addTeamMembers(teamName, entities);
                    }
                    // remove members
                    case 4 -> {
                        @SuppressWarnings("unchecked")
                        Collection<String> entities = (Collection<String>) Reflections.field$ClientboundSetPlayerTeamPacket$players.get(packet);
                        player.teamView().removeTeamMembers(teamName, entities);
                    }
                }
            } catch (ReflectiveOperationException e) {
                CustomNameplates.getInstance().getPluginLogger().severe("Failed to handle ClientboundSetPlayerTeamPacket", e);
            }
        }, "ClientboundSetPlayerTeamPacket", "PacketPlayOutScoreboardTeam");
    }

    @Override
    public Object jsonToMinecraftComponent(String json) {
        try {
            return Reflections.method$CraftChatMessage$fromJSON.invoke(null, json);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String minecraftComponentToJson(Object component) {
        try {
            return (String) Reflections.method$CraftChatMessage$toJSON.invoke(null, component);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Placeholder registerPlatformPlaceholder(String id) {
        if (!placeholderAPI) {
            return new DummyPlaceholder(id);
        }
        Placeholder placeholder;
        if (id.startsWith("%rel_")) {
            placeholder = plugin.getPlaceholderManager().registerRelationalPlaceholder(id,
                    (p1, p2) -> {
                        try {
                            return PlaceholderAPI.setRelationalPlaceholders((Player) p2.player(), (Player) p1.player(), id);
                        } catch (Exception e) {
                            return id;
                        }
                    });
        } else if (id.startsWith("%shared_")) {
            String sub = "%" + id.substring("%shared_".length());
            placeholder =plugin.getPlaceholderManager().registerSharedPlaceholder(id,
                    () -> {
                        try {
                            return PlaceholderAPI.setPlaceholders(null, sub);
                        } catch (Exception e) {
                            return sub;
                        }
                    });
        } else {
            placeholder = plugin.getPlaceholderManager().registerPlayerPlaceholder(id,
                    (p) -> {
                        try {
                            return p == null ? PlaceholderAPI.setPlaceholders(null, id) : PlaceholderAPI.setPlaceholders((OfflinePlayer) p.player(), id);
                        } catch (Exception e) {
                            return id;
                        }
                    });
        }
        return placeholder;
    }

    @Override
    public Object setActionBarTextPacket(Object component) {
        try {
            return Reflections.constructor$ClientboundSetActionBarTextPacket.newInstance(component);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object createBossBarPacket(UUID uuid, Object component, float progress, BossBar.Overlay overlay, BossBar.Color color) {
        try {
            Object barColor = Reflections.method$BossEvent$BossBarColor$valueOf.invoke(null, color.name());
            Object barOverlay = Reflections.method$BossEvent$BossBarOverlay$valueOf.invoke(null, overlay.name());
            Object operationInstance = Reflections.allocateAddOperationInstance();
            Reflections.field$ClientboundBossEventPacket$AddOperation$name.set(operationInstance, component);
            Reflections.field$ClientboundBossEventPacket$AddOperation$progress.set(operationInstance, progress);
            Reflections.field$ClientboundBossEventPacket$AddOperation$color.set(operationInstance, barColor);
            Reflections.field$ClientboundBossEventPacket$AddOperation$overlay.set(operationInstance, barOverlay);
            Reflections.field$ClientboundBossEventPacket$AddOperation$darkenScreen.set(operationInstance, false);
            Reflections.field$ClientboundBossEventPacket$AddOperation$playMusic.set(operationInstance, false);
            Reflections.field$ClientboundBossEventPacket$AddOperation$createWorldFog.set(operationInstance, false);
            return Reflections.constructor$ClientboundBossEventPacket.newInstance(uuid, operationInstance);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object removeBossBarPacket(UUID uuid) {
        try {
            return Reflections.constructor$ClientboundBossEventPacket.newInstance(uuid, Reflections.instance$ClientboundBossEventPacket$REMOVE_OPERATION);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object updateBossBarNamePacket(UUID uuid, Object component) {
        try {
            Object operation = Reflections.constructor$ClientboundBossEventPacket$UpdateNameOperation.newInstance(component);
            return Reflections.constructor$ClientboundBossEventPacket.newInstance(uuid, operation);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Object> createTextDisplayPacket(
            int entityID, UUID uuid,
            Vector3 position, float pitch, float yaw, double headYaw,
            int interpolationDelay, int transformationInterpolationDuration, int positionRotationInterpolationDuration,
            Object component, int backgroundColor, byte opacity,
            boolean hasShadow, boolean isSeeThrough, boolean useDefaultBackgroundColor, Alignment alignment,
            float viewRange, float shadowRadius, float shadowStrength,
            Vector3 scale, Vector3 translation, int lineWidth, boolean isCrouching
    ) {
        try {
            Object addEntityPacket = Reflections.constructor$ClientboundAddEntityPacket.newInstance(
                    entityID, uuid, position.x(), position.y(), position.z(), pitch, yaw,
                    Reflections.instance$EntityType$TEXT_DISPLAY, 0, Reflections.instance$Vec3$Zero, headYaw
            );

            // It's shit code
            ArrayList<Object> values = new ArrayList<>();
            EntityData.InterpolationDelay.addEntityDataIfNotDefaultValue(interpolationDelay,            values);
            if (VersionHelper.isVersionNewerThan1_20_2()) {
                EntityData.PositionRotationInterpolationDuration.addEntityDataIfNotDefaultValue(positionRotationInterpolationDuration, values);
                EntityData.TransformationInterpolationDuration.addEntityDataIfNotDefaultValue(transformationInterpolationDuration, values);
            } else {
                EntityData.InterpolationDuration.addEntityDataIfNotDefaultValue(transformationInterpolationDuration, values);
            }
            EntityData.BillboardConstraints.addEntityDataIfNotDefaultValue((byte) 3,                     values);
            EntityData.BackgroundColor.addEntityDataIfNotDefaultValue(     backgroundColor,              values);
            EntityData.Text.addEntityDataIfNotDefaultValue(                component,                    values);
            EntityData.TextOpacity.addEntityDataIfNotDefaultValue(         isCrouching ? 64 : opacity,   values);
            EntityData.ViewRange.addEntityDataIfNotDefaultValue(           viewRange,                    values);
            EntityData.ShadowRadius.addEntityDataIfNotDefaultValue(        shadowRadius,                 values);
            EntityData.ShadowStrength.addEntityDataIfNotDefaultValue(      shadowStrength,               values);
            EntityData.LineWidth.addEntityDataIfNotDefaultValue(           lineWidth,                    values);
            EntityData.Scale.addEntityDataIfNotDefaultValue(               scale.toVec3(),               values);
            EntityData.Translation.addEntityDataIfNotDefaultValue(         translation.toVec3(),         values);
            EntityData.TextDisplayMasks.addEntityDataIfNotDefaultValue(EntityData.encodeMask(hasShadow, !isCrouching, useDefaultBackgroundColor, alignment.getId()), values);

            Object setDataPacket = Reflections.constructor$ClientboundSetEntityDataPacket.newInstance(entityID, values);

            return List.of(addEntityPacket, setDataPacket);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Consumer<List<Object>> createInterpolationDelayModifier(int delay) {
        return (values) -> EntityData.InterpolationDelay.addEntityData(delay, values);
    }

    @Override
    public Consumer<List<Object>> createTransformationInterpolationDurationModifier(int duration) {
        if (VersionHelper.isVersionNewerThan1_20_2()) {
            return (values) -> EntityData.TransformationInterpolationDuration.addEntityData(duration, values);
        } else {
            return (values) -> EntityData.InterpolationDuration.addEntityData(duration, values);
        }
    }

    @Override
    public Consumer<List<Object>> createTextComponentModifier(Object component) {
        return (values) -> EntityData.Text.addEntityData(component, values);
    }

    @Override
    public Consumer<List<Object>> createScaleModifier(Vector3 scale) {
        return (values) -> EntityData.Scale.addEntityData(scale.toVec3(), values);
    }

    @Override
    public Consumer<List<Object>> createTranslationModifier(Vector3 translation) {
        return (values) -> EntityData.Translation.addEntityData(translation.toVec3(), values);
    }

    @Override
    public Consumer<List<Object>> createSneakModifier(boolean sneak, NameTagConfig config) {
        return (values) -> {
            EntityData.TextOpacity.addEntityData(sneak ? 64 : config.opacity(), values);
            EntityData.TextDisplayMasks.addEntityData(EntityData.encodeMask(config.hasShadow(), !sneak, config.useDefaultBackgroundColor(), config.alignment().getId()), values);
        };
    }

    @Override
    public Object updateTextDisplayPacket(int entityID, List<Consumer<List<Object>>> modifiers) {
        try {
            ArrayList<Object> values = new ArrayList<>();
            for (Consumer<List<Object>> modifier : modifiers) {
                modifier.accept(values);
            }
            return Reflections.constructor$ClientboundSetEntityDataPacket.newInstance(entityID, values);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object setPassengersPacket(int vehicle, int[] passengers) {
        try {
            Object packet = Reflections.allocateClientboundSetPassengersPacketInstance();
            Reflections.field$ClientboundSetPassengersPacket$passengers.set(packet, passengers);
            Reflections.field$ClientboundSetPassengersPacket$vehicle.set(packet, vehicle);
            return packet;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object removeEntityPacket(int... entityID) {
        try {
            return Reflections.constructor$ClientboundRemoveEntitiesPacket.newInstance((Object) entityID);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBiome(String world, int x, int y, int z) {
        Location location = new Location(Bukkit.getWorld(world), x, y, z);
        return BiomeUtils.getBiome(location);
    }

    @Override
    public Object vec3(double x, double y, double z) {
        try {
            return Reflections.constructor$Vector3f.newInstance((float) x, (float) y, (float) z);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onPacketSend(CNPlayer player, PacketEvent event) {
        try {
            Object packet = event.getPacket();
            onPacketSend(player, event, packet);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void onPacketSend(CNPlayer player, PacketEvent event, Object packet) throws ReflectiveOperationException {
        if (Reflections.clazz$ClientboundBundlePacket.isInstance(packet)) {
            Iterable<Object> packets = (Iterable<Object>) Reflections.field$BundlePacket$packets.get(packet);
            for (Object p : packets) {
                onPacketSend(player, event, p);
            }
        } else {
            handlePacket(player, event, packet);
        }
    }

    private void handlePacket(CNPlayer player, PacketEvent event, Object packet) {
        Optional.ofNullable(packetFunctions.get(packet.getClass().getSimpleName()))
                .ifPresent(function -> function.accept(player, event, packet));
    }
}
