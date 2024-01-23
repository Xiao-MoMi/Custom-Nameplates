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

package net.momirealms.customnameplates.paper.mechanic.actionbar;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.ActionBarManager;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.actionbar.listener.ActionBarListener;
import net.momirealms.customnameplates.paper.mechanic.actionbar.listener.ChatMessageListener;
import net.momirealms.customnameplates.paper.mechanic.actionbar.listener.SystemChatListener;
import net.momirealms.customnameplates.paper.mechanic.misc.DisplayController;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import net.momirealms.customnameplates.paper.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ActionBarManagerImpl implements ActionBarManager, Listener {

    private final ConcurrentHashMap<UUID, ActionBarReceiver> receiverMap;
    private final CustomNameplatesPlugin plugin;
    private final ActionBarListener actionBarListener;
    private ActionBarConfig config;
    private ChatMessageListener chatMessageListener;
    private SystemChatListener systemChatListener;

    public ActionBarManagerImpl(CustomNameplatesPlugin plugin) {
        this.receiverMap = new ConcurrentHashMap<>();
        this.plugin = plugin;
        this.actionBarListener = new ActionBarListener(this);
        if (plugin.getVersionManager().isVersionNewerThan1_19()) {
            this.systemChatListener = new SystemChatListener(this);
        } else {
            this.chatMessageListener = new ChatMessageListener(this);
        }
    }

    public void load() {
        if (!CNConfig.actionBarModule) return;
        this.loadConfigs();
        for (Player player : Bukkit.getOnlinePlayers()) {
            createActionBarFor(player);
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (actionBarListener != null) ProtocolLibrary.getProtocolManager().addPacketListener(actionBarListener);
        if (systemChatListener != null) ProtocolLibrary.getProtocolManager().addPacketListener(systemChatListener);
        if (chatMessageListener != null) ProtocolLibrary.getProtocolManager().addPacketListener(chatMessageListener);
    }

    public void unload() {
        for (ActionBarReceiver receiver : receiverMap.values()) {
            receiver.cancelTask();
            receiver.destroy();
        }
        receiverMap.clear();

        HandlerList.unregisterAll(this);
        if (actionBarListener != null) ProtocolLibrary.getProtocolManager().removePacketListener(actionBarListener);
        if (systemChatListener != null) ProtocolLibrary.getProtocolManager().removePacketListener(systemChatListener);
        if (chatMessageListener != null) ProtocolLibrary.getProtocolManager().removePacketListener(chatMessageListener);
    }

    public void reload() {
        unload();
        load();
    }

    private void loadConfigs() {
        YamlConfiguration config = plugin.getConfig("configs" + File.separator + "actionbar.yml");
        boolean temp = false;
        for (Map.Entry<String, Object> barEntry : config.getValues(false).entrySet()) {
            if (!(barEntry.getValue() instanceof ConfigurationSection section))
                return;

            if (temp) {
                LogUtils.warn("You can create at most 1 actionbar in actionbar.yml. Actionbar " + barEntry.getKey() + " would not work.");
                continue;
            }

            this.config = ActionBarConfig.builder()
                    .checkFrequency(section.getInt("check-frequency", 10))
                    .requirement(plugin.getRequirementManager().getRequirements(section.getConfigurationSection("conditions")))
                    .displayOrder(ConfigUtils.getTimeLimitTexts(section.getConfigurationSection("text-display-order")))
                    .build();
            temp = true;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (CNConfig.sendDelay == 0) {
            createActionBarFor(player);
            return;
        }
        this.plugin.getScheduler().runTaskAsyncLater(() -> {
            createActionBarFor(player);
        }, CNConfig.sendDelay * 50L, TimeUnit.MILLISECONDS);
    }

    private void createActionBarFor(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }
        ActionBarReceiver receiver = new ActionBarReceiver(plugin, player, new DisplayController(
                player, config.getCheckFrequency(), config.getRequirements(), config.getTextDisplayOrder()
        ));
        receiver.arrangeTask();
        this.putReceiverToMap(player.getUniqueId(), receiver);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ActionBarReceiver receiver = receiverMap.remove(event.getPlayer().getUniqueId());
        if (receiver != null) {
            receiver.cancelTask();
        }
    }

    private void putReceiverToMap(UUID uuid, ActionBarReceiver actionBarReceiver) {
        ActionBarReceiver previous = this.receiverMap.put(uuid, actionBarReceiver);
        if (previous != null) {
            LogUtils.warn("Unexpected error: Duplicated actionbar created");
            previous.cancelTask();
        }
    }

    private ActionBarReceiver getReceiver(UUID uuid) {
        return this.receiverMap.get(uuid);
    }

    // 1.19+
    public void onReceiveSystemChatPacket(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Boolean overlay = packet.getBooleans().readSafely(0);
        if (overlay != null && overlay) {
            ActionBarReceiver receiver = getReceiver(event.getPlayer().getUniqueId());
            if (receiver != null) {
                event.setCancelled(true);
                String json = packet.getStrings().readSafely(0);
                if (json != null && !json.equals("")) {
                    Component component = GsonComponentSerializer.gson().deserialize(json);
                    if (component instanceof TranslatableComponent) {
                        // We can't get TranslatableComponent's width :(
                        return;
                    }
                    receiver.setOtherPluginText(AdventureManagerImpl.getInstance().getMiniMessageFormat(component), System.currentTimeMillis());
                }
            }
        }
    }

    // lower version
    public void onReceiveChatMessagePacket(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        EnumWrappers.ChatType type = packet.getChatTypes().readSafely(0);
        if (type == EnumWrappers.ChatType.GAME_INFO) {
            ActionBarReceiver receiver = getReceiver(event.getPlayer().getUniqueId());
            if (receiver != null) {
                event.setCancelled(true);
                WrappedChatComponent wrappedChatComponent = packet.getChatComponents().read(0);
                if (wrappedChatComponent != null) {
                    String json = wrappedChatComponent.getJson();
                    Component component = GsonComponentSerializer.gson().deserialize(json);
                    if (component instanceof TranslatableComponent) {
                        // We can't get TranslatableComponent's width :(
                        return;
                    }
                    receiver.setOtherPluginText(AdventureManagerImpl.getInstance().getMiniMessageFormat(component), System.currentTimeMillis());
                }
            }
        }
    }

    public void onReceiveActionBarPacket(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        WrappedChatComponent wrappedChatComponent = packet.getChatComponents().read(0);
        if (wrappedChatComponent != null) {
            ActionBarReceiver receiver = getReceiver(event.getPlayer().getUniqueId());
            if (receiver != null) {
                String strJson = wrappedChatComponent.getJson();
                // for better performance
                if (strJson.contains("\"name\":\"np\",\"objective\":\"ab\"")) {
                    return;
                }
                event.setCancelled(true);
                receiver.setOtherPluginText(
                        AdventureManagerImpl.getInstance().getMiniMessageFormat(
                                GsonComponentSerializer.gson().deserialize(strJson)
                        ), System.currentTimeMillis()
                );
            }
        }
    }

    @NotNull
    @Override
    public String getOtherPluginActionBar(Player player) {
        ActionBarReceiver receiver = getReceiver(player.getUniqueId());
        if (receiver != null) {
            return receiver.getOtherPluginText();
        }
        return "";
    }
}
