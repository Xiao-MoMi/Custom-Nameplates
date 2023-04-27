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

package net.momirealms.customnameplates.manager;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.listener.JoinQuitListener;
import net.momirealms.customnameplates.listener.packet.ActionBarListener;
import net.momirealms.customnameplates.listener.packet.SystemChatListener;
import net.momirealms.customnameplates.object.Function;
import net.momirealms.customnameplates.object.actionbar.ActionBarConfig;
import net.momirealms.customnameplates.object.actionbar.ActionBarTask;
import net.momirealms.customnameplates.utils.AdventureUtils;
import net.momirealms.customnameplates.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ActionBarManager extends Function {

    private final LinkedHashMap<String, ActionBarConfig> actionBarConfigMap;
    private final ConcurrentHashMap<UUID, ActionBarTask> actionBarTaskMap;
    private final ActionBarListener actionBarListener;
    private final SystemChatListener systemChatListener;
    private final JoinQuitListener joinQuitListener;
    private final CustomNameplates plugin;

    public ActionBarManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.actionBarConfigMap = new LinkedHashMap<>();
        this.actionBarTaskMap = new ConcurrentHashMap<>();
        this.joinQuitListener = new JoinQuitListener(this);
        this.actionBarListener = new ActionBarListener(this);
        this.systemChatListener = new SystemChatListener(this);
    }

    @Override
    public void load() {
        if (!ConfigManager.enableActionBar) return;
        this.loadConfig();
        Bukkit.getPluginManager().registerEvents(joinQuitListener, plugin);
        CustomNameplates.getProtocolManager().addPacketListener(actionBarListener);
        CustomNameplates.getProtocolManager().addPacketListener(systemChatListener);
        for (Player player : Bukkit.getOnlinePlayers()) {
            onJoin(player);
        }
    }

    @Override
    public void unload() {
        for (ActionBarTask actionBarTask : actionBarTaskMap.values()) {
            actionBarTask.stop();
        }
        actionBarConfigMap.clear();
        CustomNameplates.getProtocolManager().removePacketListener(actionBarListener);
        CustomNameplates.getProtocolManager().removePacketListener(systemChatListener);
        HandlerList.unregisterAll(joinQuitListener);
    }

    @Override
    public void onJoin(Player player) {
        ActionBarTask actionBarTask = new ActionBarTask(player, actionBarConfigMap.values().toArray(new ActionBarConfig[0]));
        actionBarTaskMap.put(player.getUniqueId(), actionBarTask);
        actionBarTask.start();
    }

    @Override
    public void onQuit(Player player) {
        ActionBarTask actionBarTask = actionBarTaskMap.remove(player.getUniqueId());
        if (actionBarTask != null) actionBarTask.stop();
    }

    @Nullable
    public ActionBarTask getActionBarTask(UUID uuid) {
        return actionBarTaskMap.get(uuid);
    }

    private void loadConfig() {
        YamlConfiguration config = ConfigUtils.getConfig("configs" + File.separator + "actionbar.yml");
        for (String key : config.getKeys(false)) {
            ConfigurationSection actionBarSection = config.getConfigurationSection(key);
            if (actionBarSection == null) continue;
            actionBarConfigMap.put(key, new ActionBarConfig(
                    actionBarSection.getInt("switch-interval", 15) * 20,
                    actionBarSection.getString("text") == null ? actionBarSection.getStringList("dynamic-text").toArray(new String[0]) : new String[]{actionBarSection.getString("text")},
                    ConfigUtils.getRequirements(actionBarSection.getConfigurationSection("conditions"))
            ));
        }
        AdventureUtils.consoleMessage("[CustomNameplates] Loaded <green>" + actionBarConfigMap.size() + " <gray>actionbars");
    }

    public void onReceiveActionBarPacket(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        WrappedChatComponent wrappedChatComponent = packet.getChatComponents().read(0);
        if (wrappedChatComponent != null) {
            ActionBarTask actionBarTask = getActionBarTask(event.getPlayer().getUniqueId());
            if (actionBarTask != null) {
                Component component = GsonComponentSerializer.gson().deserialize(wrappedChatComponent.getJson());
                if (component instanceof ScoreComponent scoreComponent) {
                    if (scoreComponent.name().equals("nameplates") && scoreComponent.objective().equals("actionbar")) {
                        return;
                    }
                }
                event.setCancelled(true);
                actionBarTask.setOtherText(AdventureUtils.getMiniMessageFormat(component), System.currentTimeMillis());
            }
        }
    }

    public void onReceiveSystemChatPacket(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        // 1.19+
        Boolean overlay = packet.getBooleans().readSafely(0);
        // lower version
        Integer position = packet.getIntegers().readSafely(0);
        if ((overlay != null && overlay || position != null && position == 2)) {
            ActionBarTask actionBarTask = getActionBarTask(event.getPlayer().getUniqueId());
            if (actionBarTask != null) {
                String json = packet.getStrings().read(0);
                if (json != null && !json.equals("")) {
                    Component component = GsonComponentSerializer.gson().deserialize(json);
                    if (component instanceof ScoreComponent scoreComponent) {
                        if (scoreComponent.name().equals("nameplates") && scoreComponent.objective().equals("actionbar")) {
                            return;
                        }
                    }
                    event.setCancelled(true);
                    actionBarTask.setOtherText(AdventureUtils.getMiniMessageFormat(component), System.currentTimeMillis());
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    public String getOtherPluginActionBarText(Player player) {
        ActionBarTask actionBarTask = getActionBarTask(player.getUniqueId());
        if (actionBarTask != null) {
            return actionBarTask.getOtherText();
        }
        return "";
    }
}
