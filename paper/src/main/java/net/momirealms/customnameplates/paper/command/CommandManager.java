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

package net.momirealms.customnameplates.paper.command;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.data.DataStorageInterface;
import net.momirealms.customnameplates.api.data.LegacyDataStorageInterface;
import net.momirealms.customnameplates.api.data.OnlineUser;
import net.momirealms.customnameplates.api.data.PlayerData;
import net.momirealms.customnameplates.api.mechanic.bubble.Bubble;
import net.momirealms.customnameplates.api.mechanic.nameplate.Nameplate;
import net.momirealms.customnameplates.api.mechanic.tag.NameplatePlayer;
import net.momirealms.customnameplates.api.util.CompletableFutures;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.CustomNameplatesPluginImpl;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import net.momirealms.customnameplates.paper.setting.CNLocale;
import net.momirealms.customnameplates.paper.storage.method.database.sql.MariaDBImpl;
import net.momirealms.customnameplates.paper.storage.method.database.sql.MySQLImpl;
import net.momirealms.customnameplates.paper.storage.method.file.YAMLImpl;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings("DuplicatedCode")
public class CommandManager {

    private final CustomNameplatesPluginImpl plugin;

    public CommandManager(CustomNameplatesPluginImpl plugin) {
        this.plugin = plugin;
        if (!CommandAPI.isLoaded())
            CommandAPI.onLoad(new CommandAPIBukkitConfig(plugin).silentLogs(true));
    }

    public void load() {
        var command1 = new CommandAPICommand("customnameplates")
                .withAliases("nameplates", "cnameplates")
                .withSubcommands(
                        NameplatesCommands.getReloadCommand(),
                        NameplatesCommands.getAboutCommand(),
                        NameplatesCommands.getDataCommand()
                );
        if (CNConfig.nameplateModule) {
            command1.withSubcommands(
                    NameplatesCommands.getEquipCommand(),
                    NameplatesCommands.getUnEquipCommand(),
                    NameplatesCommands.getPreviewCommand(),
                    NameplatesCommands.getListCommand(),
                    NameplatesCommands.getForceEquipCommand(),
                    NameplatesCommands.getForceUnEquipCommand(),
                    NameplatesCommands.getForcePreviewCommand()
            );
        }
        command1.register();
        if (CNConfig.bubbleModule)
            new CommandAPICommand("bubbles")
                    .withSubcommands(
                            BubblesCommands.getListCommand(),
                            BubblesCommands.getEquipCommand(),
                            BubblesCommands.getUnEquipCommand(),
                            BubblesCommands.getForceEquipCommand(),
                            BubblesCommands.getForceUnEquipCommand()
                    )
                    .register();
    }

    public static class BubblesCommands {

        public static CommandAPICommand getListCommand() {
            return new CommandAPICommand("list")
                    .withPermission("bubbles.command.list")
                    .executesPlayer((player, args) -> {
                        if (!CNConfig.bubbleModule) return;
                        List<String> bubbles = CustomNameplatesPlugin.get().getBubbleManager().getAvailableBubblesDisplayNames(player);
                        if (bubbles.size() != 0) {
                            StringJoiner stringJoiner = new StringJoiner(", ");
                            for (String availableBubble : bubbles) {
                                stringJoiner.add(availableBubble);
                            }
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_AVAILABLE_BUBBLE.replace("{Bubble}", stringJoiner.toString()));
                        } else {
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_HAVE_NO_BUBBLE);
                        }
                    });
        }

        public static CommandAPICommand getEquipCommand() {
            return new CommandAPICommand("equip")
                    .withPermission("bubbles.command.equip")
                    .withArguments(new StringArgument("bubble").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> CustomNameplatesPlugin.get().getBubbleManager().getAvailableBubbles((Player) commandSenderSuggestionInfo.sender()).toArray(new String[0]))))
                    .executesPlayer((player, args) -> {
                        if (!CNConfig.bubbleModule) return;
                        String bubble = (String) args.get("bubble");
                        if (!CustomNameplatesPlugin.get().getBubbleManager().containsBubble(bubble)) {
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_BUBBLE_NOT_EXIST);
                            return;
                        }

                        if (!CustomNameplatesPlugin.get().getBubbleManager().hasBubble(player, bubble)) {
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_BUBBLE_NOT_AVAILABLE);
                            return;
                        }

                        CustomNameplatesPlugin.get().getBubbleManager().equipBubble(player, bubble);
                        Bubble bubbleInstance = CustomNameplatesPlugin.get().getBubbleManager().getBubble(bubble);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_EQUIP_BUBBLE.replace("{Bubble}", bubbleInstance.getDisplayName()));
                    });
        }

        public static CommandAPICommand getUnEquipCommand() {
            return new CommandAPICommand("unequip")
                    .withPermission("bubbles.command.unequip")
                    .executesPlayer((player, args) -> {
                        if (!CNConfig.bubbleModule) return;
                        CustomNameplatesPlugin.get().getBubbleManager().unEquipBubble(player);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_UNEQUIP_BUBBLE);
                    });
        }

        public static CommandAPICommand getForceEquipCommand() {
            return new CommandAPICommand("force-equip")
                    .withPermission("customnameplates.admin")
                    .withArguments(new PlayerArgument("player"))
                    .withArguments(new StringArgument("bubble").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> CustomNameplatesPlugin.get().getBubbleManager().getBubbleKeys().toArray(new String[0]))))
                    .executes((sender, args) -> {
                        if (!CNConfig.bubbleModule) return;
                        Player player = (Player) args.get("player");
                        String bubble = (String) args.get("bubble");
                        if (player == null) return;
                        if (!CustomNameplatesPlugin.get().getBubbleManager().equipBubble(player, bubble)) {
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_BUBBLE_NOT_EXIST);
                            return;
                        }
                        Bubble bubbleInstance = CustomNameplatesPlugin.get().getBubbleManager().getBubble(bubble);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_FORCE_EQUIP_BUBBLE.replace("{Bubble}", bubbleInstance.getDisplayName()).replace("{Player}", player.getName()));
                    });
        }

        public static CommandAPICommand getForceUnEquipCommand() {
            return new CommandAPICommand("force-unequip")
                    .withPermission("customnameplates.admin")
                    .withArguments(new PlayerArgument("player"))
                    .executes((sender, args) -> {
                        if (!CNConfig.bubbleModule) return;
                        Player player = (Player) args.get("player");
                        if (player == null) return;
                        CustomNameplatesPlugin.get().getBubbleManager().unEquipBubble(player);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_FORCE_UNEQUIP_BUBBLE.replace("{Player}", player.getName()));
                    });
        }

    }

    public static class NameplatesCommands {
        public static CommandAPICommand getForceEquipCommand() {
            return new CommandAPICommand("force-equip")
                    .withPermission("customnameplates.admin")
                    .withArguments(new PlayerArgument("player"))
                    .withArguments(new StringArgument("nameplate").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> CustomNameplatesPlugin.get().getNameplateManager().getNameplateKeys().toArray(new String[0]))))
                    .executes((sender, args) -> {
                        if (!CNConfig.nameplateModule) return;
                        Player player = (Player) args.get("player");
                        String nameplate = (String) args.get("nameplate");
                        if (player == null) return;
                        if (!CustomNameplatesPlugin.get().getNameplateManager().equipNameplate(player, nameplate, false)) {
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_NAMEPLATE_NOT_EXISTS);
                            return;
                        }
                        Nameplate nameplateInstance = CustomNameplatesPlugin.get().getNameplateManager().getNameplate(nameplate);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_FORCE_EQUIP_NAMEPLATE.replace("{Nameplate}", nameplateInstance.getDisplayName()).replace("{Player}", player.getName()));
                    });
        }

        public static CommandAPICommand getForceUnEquipCommand() {
            return new CommandAPICommand("force-unequip")
                    .withPermission("customnameplates.admin")
                    .withArguments(new PlayerArgument("player"))
                    .executes((sender, args) -> {
                        if (!CNConfig.nameplateModule) return;
                        Player player = (Player) args.get("player");
                        if (player == null) return;
                        CustomNameplatesPlugin.get().getNameplateManager().unEquipNameplate(player, false);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_FORCE_UNEQUIP_NAMEPLATE.replace("{Player}", player.getName()));
                    });
        }

        public static CommandAPICommand getPreviewCommand() {
            return new CommandAPICommand("preview")
                    .withPermission("nameplates.command.preview")
                    .executesPlayer((player, args) -> {
                        if (!CNConfig.nameplateModule) return;
                        NameplatePlayer nameplatePlayer = CustomNameplatesPlugin.get().getNameplateManager().getNameplatePlayer(player.getUniqueId());
                        if (nameplatePlayer == null) {
                            LogUtils.warn(player.getName() + " failed to preview because no tag is created");
                            return;
                        }
                        if (nameplatePlayer.isPreviewing()) {
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_PREVIEW_COOLDOWN);
                            return;
                        }
                        nameplatePlayer.setPreview(true);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_PREVIEW_START);
                        CustomNameplatesPlugin.get().getScheduler().runTaskAsyncLater(() -> {
                            nameplatePlayer.setPreview(false);
                        }, CustomNameplatesPlugin.get().getNameplateManager().getPreviewDuration(), TimeUnit.SECONDS);
                    });
        }

        public static CommandAPICommand getForcePreviewCommand() {
            return new CommandAPICommand("force-preview")
                    .withPermission("customnameplates.admin")
                    .withArguments(new PlayerArgument("player"))
                    .withOptionalArguments(new StringArgument("nameplate").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> CustomNameplatesPlugin.get().getNameplateManager().getNameplateKeys().toArray(new String[0]))))
                    .executes((sender, args) -> {
                        if (!CNConfig.nameplateModule) return;
                        Player player = (Player) args.get("player");
                        String nameplate = (String) args.getOrDefault("nameplate", "");
                        if (player == null) return;
                        NameplatePlayer nameplatePlayer = CustomNameplatesPlugin.get().getNameplateManager().getNameplatePlayer(player.getUniqueId());
                        if (nameplatePlayer == null) {
                            LogUtils.warn(player.getName() + " failed to preview because no tag is created");
                            return;
                        }
                        if (nameplatePlayer.isPreviewing()) {
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_PREVIEW_COOLDOWN);
                            return;
                        }
                        Optional<OnlineUser> user = CustomNameplatesPlugin.get().getStorageManager().getOnlineUser(player.getUniqueId());
                        if (user.isEmpty()) {
                            LogUtils.warn(player.getName() + " failed to preview because data not loaded");
                            return;
                        }
                        if (!nameplate.equals("")) {
                            String previous = user.get().getNameplateKey();
                            if (!CustomNameplatesPlugin.get().getNameplateManager().equipNameplate(player, nameplate, true)) {
                                AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_NAMEPLATE_NOT_EXISTS);
                                return;
                            }
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_FORCE_PREVIEW.replace("{Player}", player.getName()));
                            nameplatePlayer.setPreview(true);
                            CustomNameplatesPlugin.get().getScheduler().runTaskAsyncLater(() -> {
                                nameplatePlayer.setPreview(false);
                                if (previous.equals("none")) {
                                    CustomNameplatesPlugin.get().getNameplateManager().unEquipNameplate(player, true);
                                } else {
                                    CustomNameplatesPlugin.get().getNameplateManager().equipNameplate(player, previous, true);
                                }
                            }, CustomNameplatesPlugin.get().getNameplateManager().getPreviewDuration(), TimeUnit.SECONDS);
                        } else {
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_FORCE_PREVIEW.replace("{Player}", player.getName()));
                            nameplatePlayer.setPreview(true);
                            CustomNameplatesPlugin.get().getScheduler().runTaskAsyncLater(() -> {
                                nameplatePlayer.setPreview(false);
                            }, CustomNameplatesPlugin.get().getNameplateManager().getPreviewDuration(), TimeUnit.SECONDS);
                        }
                    });
        }

        public static CommandAPICommand getEquipCommand() {
            return new CommandAPICommand("equip")
                    .withPermission("nameplates.command.equip")
                    .withArguments(new StringArgument("nameplate").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> CustomNameplatesPlugin.get().getNameplateManager().getAvailableNameplates((Player) commandSenderSuggestionInfo.sender()).toArray(new String[0]))))
                    .executesPlayer((player, args) -> {
                        if (!CNConfig.nameplateModule) return;
                        String nameplate = (String) args.get("nameplate");

                        if (!CustomNameplatesPlugin.get().getNameplateManager().containsNameplate(nameplate)) {
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_NAMEPLATE_NOT_EXISTS);
                            return;
                        }

                        if (!CustomNameplatesPlugin.get().getNameplateManager().hasNameplate(player, nameplate)) {
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_NAMEPLATE_NOT_AVAILABLE);
                            return;
                        }

                        CustomNameplatesPlugin.get().getNameplateManager().equipNameplate(player, nameplate, false);
                        Nameplate nameplateInstance = CustomNameplatesPlugin.get().getNameplateManager().getNameplate(nameplate);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_EQUIP_NAMEPLATE.replace("{Nameplate}", nameplateInstance.getDisplayName()));
                    });
        }

        public static CommandAPICommand getUnEquipCommand() {
            return new CommandAPICommand("unequip")
                    .withPermission("nameplates.command.unequip")
                    .executesPlayer((player, args) -> {
                        if (!CNConfig.nameplateModule) return;
                        CustomNameplatesPlugin.get().getNameplateManager().unEquipNameplate(player, false);
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_UNEQUIP_NAMEPLATE);
                    });
        }

        public static CommandAPICommand getListCommand() {
            return new CommandAPICommand("list")
                    .withPermission("nameplates.command.list")
                    .executesPlayer((player, args) -> {
                        if (!CNConfig.nameplateModule) return;
                        List<String> nameplates = CustomNameplatesPlugin.get().getNameplateManager().getAvailableNameplateDisplayNames(player);
                        if (nameplates.size() != 0) {
                            StringJoiner stringJoiner = new StringJoiner(", ");
                            for (String availableNameplate : nameplates) {
                                stringJoiner.add(availableNameplate);
                            }
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_AVAILABLE_NAMEPLATE.replace("{Nameplates}", stringJoiner.toString()));
                        } else {
                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, CNLocale.MSG_HAVE_NO_NAMEPLATE);
                        }
                    });
        }

        public static CommandAPICommand getReloadCommand() {
            return new CommandAPICommand("reload")
                    .withPermission("customnameplates.admin")
                    .executes((sender, args) -> {
                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, "<red>Usage: /nameplates reload all/pack/config");
                    })
                    .withSubcommands(
                            new CommandAPICommand("all")
                                    .withOptionalArguments(new BooleanArgument("async-pack-generation"))
                                    .executes((sender, args) -> {
                                        boolean async = (boolean) args.getOrDefault("async-pack-generation", false);
                                        long time = System.currentTimeMillis();
                                        CustomNameplatesPlugin.get().reload();
                                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_RELOAD.replace("{time}", String.valueOf(System.currentTimeMillis()-time)));
                                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_GENERATING);
                                        if (async) {
                                            CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() -> {
                                                CustomNameplatesPlugin.get().getResourcePackManager().generateResourcePack();
                                                AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_PACK_GENERATED);
                                            });
                                        } else {
                                            CustomNameplatesPlugin.get().getResourcePackManager().generateResourcePack();
                                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_PACK_GENERATED);
                                        }
                                    }),
                            new CommandAPICommand("pack")
                                    .withOptionalArguments(new BooleanArgument("async-pack-generation"))
                                    .executes((sender, args) -> {
                                        boolean async = (boolean) args.getOrDefault("async-pack-generation", false);
                                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_GENERATING);
                                        if (async) {
                                            CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() -> {
                                                CustomNameplatesPlugin.get().getResourcePackManager().generateResourcePack();
                                                AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_PACK_GENERATED);
                                            });
                                        } else {
                                            CustomNameplatesPlugin.get().getResourcePackManager().generateResourcePack();
                                            AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_PACK_GENERATED);
                                        }
                                    }),
                            new CommandAPICommand("config")
                                    .executes((sender, args) -> {
                                        long time = System.currentTimeMillis();
                                        CustomNameplatesPlugin.get().reload();
                                        AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, CNLocale.MSG_RELOAD.replace("{time}", String.valueOf(System.currentTimeMillis()-time)));
                                    })
                    );
        }

        public static CommandAPICommand getAboutCommand() {
            return new CommandAPICommand("about")
                    .withPermission("customnameplates.admin")
                    .executes((sender, args) -> {
                        AdventureManagerImpl.getInstance().sendMessage(sender, "<#3CB371>⚓ CustomNameplates <gray>- <#98FB98>" + CustomNameplatesPlugin.getInstance().getVersionManager().getPluginVersion());
                        AdventureManagerImpl.getInstance().sendMessage(sender, "<#7FFFAA>A plugin that provides adjustable images for texts");
                        AdventureManagerImpl.getInstance().sendMessage(sender, "<#DA70D6>\uD83E\uDDEA Author: <#FFC0CB>XiaoMoMi");
                        AdventureManagerImpl.getInstance().sendMessage(sender, "<#FF7F50>\uD83D\uDD25 Contributors: <#FFA07A>TopOrigin<white>");
                        AdventureManagerImpl.getInstance().sendMessage(sender, "<#FFD700>⭐ <click:open_url:https://mo-mi.gitbook.io/xiaomomi-plugins/plugin-wiki/customnameplates>Document</click> <#A9A9A9>| <#FAFAD2>⛏ <click:open_url:https://github.com/Xiao-MoMi/Custom-Nameplates>Github</click> <#A9A9A9>| <#48D1CC>\uD83D\uDD14 <click:open_url:https://polymart.org/resource/customnameplates.2543>Polymart</click>");
                    });
        }

        public static CommandAPICommand getDataCommand() {
            return new CommandAPICommand("data")
                    .withPermission("customnameplates.admin")
                    .withSubcommands(
                          new CommandAPICommand("export")
                                  .executes((sender, args) -> {
                                      CustomNameplatesPlugin plugin = CustomNameplatesPlugin.get();
                                      plugin.getScheduler().runTaskAsync(() -> {

                                          AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, "Starting <aqua>export</aqua>.");
                                          DataStorageInterface dataStorageInterface = plugin.getStorageManager().getDataSource();

                                          Set<UUID> uuids = dataStorageInterface.getUniqueUsers(false);
                                          Set<CompletableFuture<Void>> futures = new HashSet<>();
                                          AtomicInteger userCount = new AtomicInteger(0);
                                          Map<UUID, String> out = Collections.synchronizedMap(new TreeMap<>());

                                          int amount = uuids.size();
                                          for (UUID uuid : uuids) {
                                              futures.add(dataStorageInterface.getPlayerData(uuid).thenAccept(it -> {
                                                  if (it.isPresent()) {
                                                      out.put(uuid, plugin.getStorageManager().toJson(it.get()));
                                                      userCount.incrementAndGet();
                                                  }
                                              }));
                                          }

                                          CompletableFuture<Void> overallFuture = CompletableFutures.allOf(futures);

                                          while (true) {
                                              try {
                                                  overallFuture.get(3, TimeUnit.SECONDS);
                                              } catch (InterruptedException | ExecutionException e) {
                                                  e.printStackTrace();
                                                  break;
                                              } catch (TimeoutException e) {
                                                  LogUtils.info("Progress: " + userCount.get() + "/" + amount);
                                                  continue;
                                              }
                                              break;
                                          }

                                          JsonObject outJson = new JsonObject();
                                          for (Map.Entry<UUID, String> entry : out.entrySet()) {
                                              outJson.addProperty(entry.getKey().toString(), entry.getValue());
                                          }
                                          SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                                          String formattedDate = formatter.format(new Date());
                                          File outFile = new File(plugin.getDataFolder(), "exported-" + formattedDate + ".json.gz");
                                          try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(outFile.toPath())), StandardCharsets.UTF_8))) {
                                              new GsonBuilder().disableHtmlEscaping().create().toJson(outJson, writer);
                                          } catch (IOException e) {
                                              e.printStackTrace();
                                          }

                                          AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, "Completed.");
                                      });
                          }),
                          new CommandAPICommand("export-legacy")
                                  .withArguments(new StringArgument("method")
                                          .replaceSuggestions(ArgumentSuggestions.strings("MySQL", "MariaDB", "YAML")))
                                  .executes((sender, args) -> {
                                      String arg = (String) args.get("method");
                                      if (arg == null) return;
                                      CustomNameplatesPlugin plugin = CustomNameplatesPlugin.get();
                                      plugin.getScheduler().runTaskAsync(() -> {

                                          AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, "Starting <aqua>export</aqua>.");

                                          LegacyDataStorageInterface dataStorageInterface;
                                          switch (arg) {
                                              case "MySQL" -> dataStorageInterface = new MySQLImpl(plugin);
                                              case "MariaDB" -> dataStorageInterface = new MariaDBImpl(plugin);
                                              case "YAML" -> dataStorageInterface = new YAMLImpl(plugin);
                                              default -> {
                                                  AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, "No such legacy storage method.");
                                                  return;
                                              }
                                          }

                                          dataStorageInterface.initialize();
                                          Set<UUID> uuids = dataStorageInterface.getUniqueUsers(true);
                                          Set<CompletableFuture<Void>> futures = new HashSet<>();
                                          AtomicInteger userCount = new AtomicInteger(0);
                                          Map<UUID, String> out = Collections.synchronizedMap(new TreeMap<>());

                                          for (UUID uuid : uuids) {
                                              futures.add(dataStorageInterface.getLegacyPlayerData(uuid).thenAccept(it -> {
                                                  if (it.isPresent()) {
                                                      out.put(uuid, plugin.getStorageManager().toJson(it.get()));
                                                      userCount.incrementAndGet();
                                                  }
                                              }));
                                          }

                                          CompletableFuture<Void> overallFuture = CompletableFutures.allOf(futures);

                                          while (true) {
                                              try {
                                                  overallFuture.get(3, TimeUnit.SECONDS);
                                              } catch (InterruptedException | ExecutionException e) {
                                                  e.printStackTrace();
                                                  break;
                                              } catch (TimeoutException e) {
                                                  LogUtils.info("Progress: " + userCount.get() + "/" + uuids.size());
                                                  continue;
                                              }
                                              break;
                                          }

                                          JsonObject outJson = new JsonObject();
                                          for (Map.Entry<UUID, String> entry : out.entrySet()) {
                                              outJson.addProperty(entry.getKey().toString(), entry.getValue());
                                          }
                                          SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                                          String formattedDate = formatter.format(new Date());
                                          File outFile = new File(plugin.getDataFolder(), "exported-" + formattedDate + ".json.gz");
                                          try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(outFile.toPath())), StandardCharsets.UTF_8))) {
                                              new GsonBuilder().disableHtmlEscaping().create().toJson(outJson, writer);
                                          } catch (IOException e) {
                                              e.printStackTrace();
                                          }

                                          dataStorageInterface.disable();

                                          AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, "Completed.");
                                      });
                                  })
                            ,
                          new CommandAPICommand("import")
                                  .withArguments(new StringArgument("file"))
                                  .executes((sender, args) -> {
                                      String fileName = (String) args.get("file");
                                      if (fileName == null) return;
                                      CustomNameplatesPlugin plugin = CustomNameplatesPlugin.get();
                                      File file = new File(plugin.getDataFolder(), fileName);
                                      if (!file.exists()) {
                                          AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, "File not exists.");
                                          return;
                                      }
                                      if (!file.getName().endsWith(".json.gz")) {
                                          AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, "Invalid file.");
                                          return;
                                      }
                                      plugin.getScheduler().runTaskAsync(() -> {
                                          AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, "Starting <aqua>import</aqua>.");
                                          JsonObject data;
                                          try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(file.toPath())), StandardCharsets.UTF_8))) {
                                              data = new GsonBuilder().disableHtmlEscaping().create().fromJson(reader, JsonObject.class);
                                          } catch (IOException e) {
                                              AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, "Error occurred when reading the backup file.");
                                              e.printStackTrace();
                                              return;
                                          }
                                          DataStorageInterface dataStorageInterface = plugin.getStorageManager().getDataSource();
                                          var entrySet = data.entrySet();
                                          int amount = entrySet.size();
                                          AtomicInteger userCount = new AtomicInteger(0);
                                          Set<CompletableFuture<Void>> futures = new HashSet<>();

                                          for (Map.Entry<String, JsonElement> entry : entrySet) {
                                              UUID uuid = UUID.fromString(entry.getKey());
                                              if (entry.getValue() instanceof JsonPrimitive primitive) {
                                                  PlayerData playerData = plugin.getStorageManager().fromJson(primitive.getAsString());
                                                  futures.add(dataStorageInterface.updateOrInsertPlayerData(uuid, playerData).thenAccept(it -> userCount.incrementAndGet()));
                                              }
                                          }
                                          CompletableFuture<Void> overallFuture = CompletableFutures.allOf(futures);
                                          while (true) {
                                              try {
                                                  overallFuture.get(3, TimeUnit.SECONDS);
                                              } catch (InterruptedException | ExecutionException e) {
                                                  e.printStackTrace();
                                                  break;
                                              } catch (TimeoutException e) {
                                                  LogUtils.info("Progress: " + userCount.get() + "/" + amount);
                                                  continue;
                                              }
                                              break;
                                          }
                                          AdventureManagerImpl.getInstance().sendMessageWithPrefix(sender, "Completed.");
                                      });
                                  })
                          );
        }
    }
}
