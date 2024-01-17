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

package net.momirealms.customnameplates.paper.adventure;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.AdventureManager;
import net.momirealms.customnameplates.paper.mechanic.misc.PacketManager;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import net.momirealms.customnameplates.paper.setting.CNLocale;
import net.momirealms.customnameplates.paper.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class AdventureManagerImpl implements AdventureManager {

    private final BukkitAudiences adventure;
    private static AdventureManager instance;
    private final CacheSystem cacheSystem;

    public AdventureManagerImpl(CustomNameplatesPlugin plugin) {
        this.adventure = BukkitAudiences.create(plugin);
        this.cacheSystem = new CacheSystem();
        instance = this;
    }

    public static AdventureManager getInstance() {
        return instance;
    }

    public void close() {
        if (adventure != null)
            adventure.close();
    }

    @Override
    public Object getIChatComponentFromMiniMessage(String text) {
        return cacheSystem.getIChatFromCache(text);
    }

    @Override
    public String stripTags(String text) {
        return MiniMessage.miniMessage().stripTags(text);
    }

    @Override
    public Component getComponentFromMiniMessage(String text) {
        if (text == null) {
            return Component.empty();
        }
        return cacheSystem.getComponentFromCache(text);
    }

    @Override
    public void sendMessage(CommandSender sender, String s) {
        if (s == null) return;
        if (sender instanceof Player player) sendPlayerMessage(player, s);
        else if (sender instanceof ConsoleCommandSender) sendConsoleMessage(s);
    }

    @Override
    public void sendMessageWithPrefix(CommandSender sender, String s) {
        if (s == null) return;
        if (sender instanceof Player player) sendPlayerMessage(player, CNLocale.MSG_PREFIX + s);
        else if (sender instanceof ConsoleCommandSender) sendConsoleMessage(CNLocale.MSG_PREFIX + s);
    }

    @Override
    public void sendConsoleMessage(String s) {
        if (s == null) return;
        Audience au = adventure.sender(Bukkit.getConsoleSender());
        au.sendMessage(getComponentFromMiniMessage(s));
    }

    @Override
    public void sendPlayerMessage(Player player, String s) {
        if (s == null) return;
        Audience au = adventure.player(player);
        au.sendMessage(getComponentFromMiniMessage(s));
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int in, int duration, int out) {
        Audience au = adventure.player(player);
        Title.Times times = Title.Times.times(Duration.ofMillis(in), Duration.ofMillis(duration), Duration.ofMillis(out));
        au.showTitle(Title.title(getComponentFromMiniMessage(title), getComponentFromMiniMessage(subtitle), times));
    }

    @Override
    public void sendTitle(Player player, Component title, Component subtitle, int in, int duration, int out) {
        Audience au = adventure.player(player);
        Title.Times times = Title.Times.times(Duration.ofMillis(in), Duration.ofMillis(duration), Duration.ofMillis(out));
        au.showTitle(Title.title(title, subtitle, times));
    }

    @Override
    public void sendActionbar(Player player, String text) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        packet.getModifier().write(0, getIChatComponent(componentToJson(getComponentFromMiniMessage(text).append(Component.score().name("np").objective("ab").build()))));
        PacketManager.getInstance().send(player, packet);
    }

    @Override
    public void sendActionbar(Player player, Component component) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        packet.getModifier().write(0, getIChatComponent(componentToJson(component)));
        PacketManager.getInstance().send(player, packet);
    }

    @Override
    public void sendSound(Player player, Sound.Source source, Key key, float volume, float pitch) {
        Sound sound = Sound.sound(key, source, volume, pitch);
        Audience au = adventure.player(player);
        au.playSound(sound);
    }

    @Override
    public void sendSound(Player player, Sound sound) {
        Audience au = adventure.player(player);
        au.playSound(sound);
    }

    @Override
    public String legacyToMiniMessage(String legacy) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = legacy.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!isColorCode(chars[i])) {
                stringBuilder.append(chars[i]);
                continue;
            }
            if (i + 1 >= chars.length) {
                stringBuilder.append(chars[i]);
                continue;
            }
            switch (chars[i+1]) {
                case '0' -> stringBuilder.append("<black>");
                case '1' -> stringBuilder.append("<dark_blue>");
                case '2' -> stringBuilder.append("<dark_green>");
                case '3' -> stringBuilder.append("<dark_aqua>");
                case '4' -> stringBuilder.append("<dark_red>");
                case '5' -> stringBuilder.append("<dark_purple>");
                case '6' -> stringBuilder.append("<gold>");
                case '7' -> stringBuilder.append("<gray>");
                case '8' -> stringBuilder.append("<dark_gray>");
                case '9' -> stringBuilder.append("<blue>");
                case 'a' -> stringBuilder.append("<green>");
                case 'b' -> stringBuilder.append("<aqua>");
                case 'c' -> stringBuilder.append("<red>");
                case 'd' -> stringBuilder.append("<light_purple>");
                case 'e' -> stringBuilder.append("<yellow>");
                case 'f' -> stringBuilder.append("<white>");
                case 'r' -> stringBuilder.append("<r><!i>");
                case 'l' -> stringBuilder.append("<b>");
                case 'm' -> stringBuilder.append("<s>");
                case 'o' -> stringBuilder.append("<i>");
                case 'n' -> stringBuilder.append("<u>");
                case 'k' -> stringBuilder.append("<o>");
                case 'x' -> {
                    if (i + 13 >= chars.length
                            || !isColorCode(chars[i+2])
                            || !isColorCode(chars[i+4])
                            || !isColorCode(chars[i+6])
                            || !isColorCode(chars[i+8])
                            || !isColorCode(chars[i+10])
                            || !isColorCode(chars[i+12])) {
                        stringBuilder.append(chars[i]);
                        continue;
                    }
                    stringBuilder
                            .append("<#")
                            .append(chars[i+3])
                            .append(chars[i+5])
                            .append(chars[i+7])
                            .append(chars[i+9])
                            .append(chars[i+11])
                            .append(chars[i+13])
                            .append(">");
                    i += 12;
                }
                default -> {
                    stringBuilder.append(chars[i]);
                    continue;
                }
            }
            i++;
        }
        return stringBuilder.toString();
    }

    @Override
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isColorCode(char c) {
        return c == '§' || c == '&';
    }

    @Override
    public String componentToLegacy(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    @Override
    public String componentToJson(Component component) {
        return GsonComponentSerializer.gson().serialize(component);
    }

    @Override
    public Object shadedToOriginal(Component component) {
        Object cp;
        try {
            cp = ReflectionUtils.gsonDeserializeMethod.invoke(ReflectionUtils.gsonInstance, GsonComponentSerializer.gson().serialize(component));
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        return cp;
    }

    @Override
    public String getMiniMessageFormat(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    @Override
    public Object getIChatComponent(String json) {
        try {
            return ReflectionUtils.iChatComponentMethod.invoke(null, json);
        } catch (InvocationTargetException | IllegalAccessException exception) {
            exception.printStackTrace();
            return ReflectionUtils.emptyComponent;
        }
    }

    public class CacheSystem {

        private final LoadingCache<String, Object> miniMessageToIChatComponentCache;
        private final LoadingCache<String, Component> miniMessageToComponentCache;

        public CacheSystem() {
            miniMessageToIChatComponentCache = CacheBuilder.newBuilder()
                    .maximumSize(100)
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .build(
                            new CacheLoader<>() {
                                @NotNull
                                @Override
                                public Object load(@NotNull String text) {
                                    return fetchIChatData(text);
                                }
                            });
            miniMessageToComponentCache = CacheBuilder.newBuilder()
                    .maximumSize(100)
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .build(
                            new CacheLoader<>() {
                                @NotNull
                                @Override
                                public Component load(@NotNull String text) {
                                    return fetchComponent(text);
                                }
                            });
        }

        @NotNull
        private Object fetchIChatData(String text) {
            Component component = getComponentFromMiniMessage(text);
            return getIChatComponent(GsonComponentSerializer.gson().serialize(component));
        }

        @NotNull
        private Component fetchComponent(String text) {
            if (CNConfig.legacyColorSupport) {
                return MiniMessage.miniMessage().deserialize(legacyToMiniMessage(text));
            } else {
                return MiniMessage.miniMessage().deserialize(text);
            }
        }

        public Object getIChatFromCache(String text) {
            try {
                return miniMessageToIChatComponentCache.get(text);
            } catch (ExecutionException e) {
                e.printStackTrace();
                return ReflectionUtils.emptyComponent;
            }
        }

        public Component getComponentFromCache(String text) {
            try {
                return miniMessageToComponentCache.get(text);
            } catch (ExecutionException e) {
                e.printStackTrace();
                return Component.empty();
            }
        }
    }
}