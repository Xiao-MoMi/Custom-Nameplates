/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 */

package net.momirealms.customnameplates.bukkit.gui;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.feature.nameplate.Nameplate;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NameplatesGui implements Listener {

    private static final String CONFIG_PATH = "gui" + File.separator + "nameplates.yml";

    private final BukkitCustomNameplates plugin;
    private List<String> layout = List.of();
    private String title;
    private final Map<Character, GuiIcon> icons = new LinkedHashMap<>();

    public NameplatesGui(BukkitCustomNameplates plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.getConfigManager().saveResource(CONFIG_PATH);
        YamlDocument document = plugin.getConfigManager().loadData(new File(plugin.getDataDirectory().toFile(), CONFIG_PATH));
        this.title = document.getString("title", "&f铭牌菜单");
        this.layout = document.getStringList("layout");
        if (layout.isEmpty() || layout.size() > 6 || layout.stream().anyMatch(row -> row.length() != 9)) {
            plugin.getPluginLogger().warn("Invalid GUI layout in " + CONFIG_PATH + "; using the default layout");
            this.layout = List.of(
                    "#########",
                    "#ABCDEFG#",
                    "#HIJKLMO#",
                    "#QRSTUVW#",
                    "#########",
                    "###p#n#x#"
            );
        }

        this.icons.clear();
        Section iconsSection = document.getSection("icons");
        if (iconsSection == null) {
            plugin.getPluginLogger().warn("No icons section found in " + CONFIG_PATH);
            return;
        }
        for (Object key : iconsSection.getKeys()) {
            String keyText = String.valueOf(key);
            if (keyText.length() != 1) {
                plugin.getPluginLogger().warn("Ignoring GUI icon key '" + keyText + "': keys must be one character");
                continue;
            }
            char character = keyText.charAt(0);
            String path = "icons." + keyText;
            String type = document.getString(path + ".type", "static");
            String action = document.getString(path + ".action", "none");
            ItemTemplate display = loadTemplate(document, path + ".display", defaultMaterial(type), defaultName(type));
            ItemTemplate equipped = "nameplate".equalsIgnoreCase(type)
                    ? loadTemplate(document, path + ".equipped-display", Material.LIME_DYE, "&a{nameplate}")
                    : null;
            this.icons.put(character, new GuiIcon(type, action, display, equipped));
        }
    }

    public void open(Player player) {
        open(player, 0);
    }

    private void open(Player player, int requestedPage) {
        CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
        if (cnPlayer == null || !cnPlayer.isLoaded()) {
            player.sendMessage(color("&c玩家数据尚未加载完成"));
            return;
        }

        List<Nameplate> owned = new ArrayList<>(plugin.getNameplateManager().availableNameplates(cnPlayer));
        owned.sort(Comparator.comparing(Nameplate::id, String.CASE_INSENSITIVE_ORDER));
        List<Integer> nameplateSlots = slotsByType("nameplate");
        if (nameplateSlots.isEmpty()) {
            player.sendMessage(color("&cGUI 配置中没有 type: nameplate 的图标槽位"));
            return;
        }

        int pages = Math.max(1, (owned.size() + nameplateSlots.size() - 1) / nameplateSlots.size());
        int page = Math.floorMod(requestedPage, pages);
        NameplatesGuiHolder holder = new NameplatesGuiHolder(page, pages);
        Map<String, String> common = Map.of(
                "page", String.valueOf(page + 1),
                "pages", String.valueOf(pages),
                "owned", String.valueOf(owned.size()),
                "player", player.getName()
        );
        Inventory inventory = createInventory(holder, layout.size() * 9, replace(title, common), player);
        holder.inventory = inventory;

        int itemIndex = page * nameplateSlots.size();
        int pageEnd = Math.min(itemIndex + nameplateSlots.size(), owned.size());
        for (int row = 0; row < layout.size(); row++) {
            for (int column = 0; column < 9; column++) {
                int slot = row * 9 + column;
                GuiIcon icon = icons.get(layout.get(row).charAt(column));
                if (icon == null) {
                    continue;
                }
                if (icon.isNameplate()) {
                    if (itemIndex >= pageEnd) {
                        continue;
                    }
                    Nameplate entry = owned.get(itemIndex++);
                    boolean equipped = entry.id().equals(cnPlayer.nameplateData());
                    Map<String, String> placeholders = new HashMap<>(common);
                    placeholders.put("id", entry.id());
                    placeholders.put("nameplate", entry.displayName());
                    ItemTemplate template = equipped && icon.equippedDisplay != null ? icon.equippedDisplay : icon.display;
                    inventory.setItem(slot, template.create(placeholders, player));
                    holder.nameplatesBySlot.put(slot, entry.id());
                } else {
                    inventory.setItem(slot, icon.display.create(common, player));
                    holder.actionsBySlot.put(slot, icon.action);
                }
            }
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof NameplatesGuiHolder holder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player) || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        String action = holder.actionsBySlot.get(event.getRawSlot());
        if (action != null) {
            switch (action.toLowerCase()) {
                case "previous-page" -> open(player, holder.page - 1);
                case "next-page" -> open(player, holder.page + 1);
                case "close" -> player.closeInventory();
                default -> {
                }
            }
            return;
        }

        String nameplateId = holder.nameplatesBySlot.get(event.getRawSlot());
        if (nameplateId == null) {
            return;
        }
        CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
        if (cnPlayer == null || !cnPlayer.isLoaded() || !plugin.getNameplateManager().hasNameplate(cnPlayer, nameplateId)) {
            player.closeInventory();
            player.sendMessage(color("&c你当前无法佩戴这个铭牌"));
            return;
        }

        if (nameplateId.equals(cnPlayer.nameplateData())) {
            cnPlayer.setNameplateData("none");
            player.sendMessage(color("&f已取消佩戴铭牌"));
        } else {
            cnPlayer.setNameplateData(nameplateId);
            player.sendMessage(color("&f已佩戴铭牌: &a" + nameplateId));
        }
        cnPlayer.save();
        open(player, holder.page);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof NameplatesGuiHolder) {
            event.setCancelled(true);
        }
    }

    private Inventory createInventory(InventoryHolder holder, int size, String rawTitle, Player player) {
        Object component = craftEngineComponent(resolvePlaceholders(player, rawTitle));
        if (component != null) {
            try {
                for (Method method : Bukkit.class.getMethods()) {
                    Class<?>[] parameters = method.getParameterTypes();
                    if (method.getName().equals("createInventory") && parameters.length == 3 && parameters[1] == int.class
                            && parameters[2].getName().equals("net.kyori.adventure.text.Component")) {
                        return (Inventory) method.invoke(null, holder, size, component);
                    }
                }
            } catch (ReflectiveOperationException exception) {
                plugin.debug(() -> "Unable to create component titled GUI: " + exception.getMessage());
            }
        }
        return Bukkit.createInventory(holder, size, color(stripMiniMessageTags(rawTitle)));
    }

    private ItemTemplate loadTemplate(YamlDocument document, String path, Material fallbackMaterial, String fallbackName) {
        return new ItemTemplate(
                document.getString(path + ".mat", fallbackMaterial.name()),
                document.getString(path + ".name", fallbackName),
                document.getStringList(path + ".lore"),
                document.getBoolean(path + ".shiny", false),
                document.getInt(path + ".custom-model-data", 0),
                document.getString(path + ".item-model", ""),
                document.getString(path + ".tooltip-style", ""),
                document.getBoolean(path + ".hide-tooltip", false)
        );
    }

    private Material defaultMaterial(String type) {
        return "nameplate".equalsIgnoreCase(type) ? Material.NAME_TAG : Material.GRAY_STAINED_GLASS_PANE;
    }

    private String defaultName(String type) {
        return "nameplate".equalsIgnoreCase(type) ? "&f{nameplate}" : " ";
    }

    private List<Integer> slotsByType(String type) {
        List<Integer> result = new ArrayList<>();
        for (int row = 0; row < layout.size(); row++) {
            for (int column = 0; column < 9; column++) {
                GuiIcon icon = icons.get(layout.get(row).charAt(column));
                if (icon != null && icon.type.equalsIgnoreCase(type)) {
                    result.add(row * 9 + column);
                }
            }
        }
        return result;
    }

    private ItemStack createBaseItem(String source) {
        if (source.regionMatches(true, 0, "source:craftengine:", 0, "source:craftengine:".length())) {
            String id = source.substring("source:craftengine:".length());
            ItemStack custom = createCraftEngineItem(id);
            if (custom != null) {
                return custom;
            }
            plugin.getPluginLogger().warn("Unable to create CraftEngine item '" + id + "'; using PAPER");
            return new ItemStack(Material.PAPER);
        }
        if (source.regionMatches(true, 0, "head:", 0, 5)) {
            return createHead(source.substring(5));
        }
        Material material = Material.matchMaterial(source);
        return new ItemStack(material == null || material.isAir() ? Material.PAPER : material);
    }

    private ItemStack createCraftEngineItem(String id) {
        Plugin craftEngine = Bukkit.getPluginManager().getPlugin("CraftEngine");
        if (craftEngine == null || !craftEngine.isEnabled()) {
            return null;
        }
        try {
            ClassLoader loader = craftEngine.getClass().getClassLoader();
            Class<?> keyClass = Class.forName("net.momirealms.craftengine.core.util.Key", true, loader);
            Object key = keyClass.getMethod("of", String.class).invoke(null, id);
            Class<?> itemClass = Class.forName("net.momirealms.craftengine.core.item.Item", true, loader);
            Object item = itemClass.getMethod("byId", keyClass).invoke(null, key);
            if (item == null) {
                return null;
            }
            return ((ItemStack) item.getClass().getMethod("getBukkitItem").invoke(item)).clone();
        } catch (ReflectiveOperationException | LinkageError exception) {
            plugin.debug(() -> "Unable to create CraftEngine item: " + exception.getMessage());
            return null;
        }
    }

    private ItemStack createHead(String texture) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        if (!(item.getItemMeta() instanceof SkullMeta meta)) {
            return item;
        }
        String url = textureUrl(texture);
        if (url == null) {
            return item;
        }
        try {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.nameUUIDFromBytes(texture.getBytes(StandardCharsets.UTF_8)));
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(new URL(url));
            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
            item.setItemMeta(meta);
        } catch (MalformedURLException ignored) {
        }
        return item;
    }

    private String textureUrl(String texture) {
        if (texture.startsWith("http://") || texture.startsWith("https://")) {
            return texture;
        }
        try {
            String json = new String(Base64.getDecoder().decode(texture), StandardCharsets.UTF_8);
            int start = json.indexOf("http");
            int end = json.indexOf('"', start);
            return start >= 0 && end > start ? json.substring(start, end).replace("\\/", "/") : null;
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private ItemStack applyModernComponents(ItemStack item, String itemModel, String tooltipStyle, boolean hideTooltip) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        invokeKeySetter(meta, "setItemModel", itemModel);
        invokeKeySetter(meta, "setTooltipStyle", tooltipStyle);
        if (hideTooltip) {
            try {
                meta.getClass().getMethod("setHideTooltip", boolean.class).invoke(meta, true);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    private void invokeKeySetter(ItemMeta meta, String methodName, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        try {
            Class<?> keyClass = Class.forName("org.bukkit.NamespacedKey");
            Object key = keyClass.getMethod("fromString", String.class).invoke(null, value);
            meta.getClass().getMethod(methodName, keyClass).invoke(meta, key);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private void setComponentText(ItemMeta meta, String name, List<String> lore, Player player) {
        Object nameComponent = craftEngineComponent(resolvePlaceholders(player, name));
        if (nameComponent != null) {
            try {
                Class<?> componentClass = Class.forName("net.kyori.adventure.text.Component");
                meta.getClass().getMethod("displayName", componentClass).invoke(meta, nameComponent);
                if (!lore.isEmpty()) {
                    List<Object> components = new ArrayList<>();
                    for (String line : lore) {
                        Object component = craftEngineComponent(resolvePlaceholders(player, line));
                        if (component != null) {
                            components.add(component);
                        }
                    }
                    meta.getClass().getMethod("lore", List.class).invoke(meta, components);
                }
                return;
            } catch (ReflectiveOperationException ignored) {
            }
        }
        meta.setDisplayName(color(stripMiniMessageTags(resolvePlaceholders(player, name))));
        if (!lore.isEmpty()) {
            meta.setLore(lore.stream().map(line -> color(stripMiniMessageTags(resolvePlaceholders(player, line)))).toList());
        }
    }

    private Object craftEngineComponent(String text) {
        Plugin craftEngine = Bukkit.getPluginManager().getPlugin("CraftEngine");
        if (craftEngine == null || !craftEngine.isEnabled()) {
            return null;
        }
        try {
            ClassLoader loader = craftEngine.getClass().getClassLoader();
            Class<?> helper = Class.forName("net.momirealms.craftengine.core.util.AdventureHelper", true, loader);
            text = (String) helper.getMethod("legacyToMiniMessage", String.class).invoke(null, text);
            Object miniMessage = helper.getMethod("customMiniMessage").invoke(null);
            Object component = findSingleStringMethod(miniMessage.getClass(), "deserialize").invoke(miniMessage, text);
            Class<?> utils = Class.forName("net.momirealms.craftengine.bukkit.util.ComponentUtils", true, loader);
            return findSingleArgumentMethod(utils, "adventureToPaperAdventure").invoke(null, component);
        } catch (ReflectiveOperationException | LinkageError exception) {
            plugin.debug(() -> "Unable to parse CraftEngine component: " + exception.getMessage());
            return null;
        }
    }

    private String resolvePlaceholders(Player player, String text) {
        Plugin placeholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (placeholderApi == null || !placeholderApi.isEnabled()) {
            return text;
        }
        try {
            Class<?> api = Class.forName("me.clip.placeholderapi.PlaceholderAPI", true, placeholderApi.getClass().getClassLoader());
            return (String) api.getMethod("setPlaceholders", Player.class, String.class).invoke(null, player, text);
        } catch (ReflectiveOperationException exception) {
            return text;
        }
    }

    private static Method findSingleStringMethod(Class<?> type, String name) throws NoSuchMethodException {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == 1 && method.getParameterTypes()[0] == String.class) {
                return method;
            }
        }
        throw new NoSuchMethodException(type.getName() + "#" + name + "(String)");
    }

    private static Method findSingleArgumentMethod(Class<?> type, String name) throws NoSuchMethodException {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == 1) {
                return method;
            }
        }
        throw new NoSuchMethodException(type.getName() + "#" + name);
    }

    private static String replace(String text, Map<String, String> placeholders) {
        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    private static String stripMiniMessageTags(String text) {
        return text.replaceAll("<[^>]+>", "");
    }

    private static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private record GuiIcon(String type, String action, ItemTemplate display, ItemTemplate equippedDisplay) {
        private boolean isNameplate() {
            return type.equalsIgnoreCase("nameplate");
        }
    }

    private record ItemTemplate(String material, String name, List<String> lore, boolean shiny,
                                int customModelData, String itemModel, String tooltipStyle, boolean hideTooltip) {
        private ItemStack create(Map<String, String> placeholders, Player player) {
            NameplatesGui gui = BukkitCustomNameplates.getInstance().getNameplatesGui();
            ItemStack item = gui.createBaseItem(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                gui.setComponentText(meta, replace(name, placeholders), lore.stream().map(line -> replace(line, placeholders)).toList(), player);
                if (customModelData > 0) {
                    meta.setCustomModelData(customModelData);
                }
                if (shiny) {
                    try {
                        meta.getClass().getMethod("setEnchantmentGlintOverride", Boolean.class).invoke(meta, true);
                    } catch (ReflectiveOperationException ignored) {
                    }
                }
                meta.addItemFlags(ItemFlag.values());
                item.setItemMeta(meta);
            }
            return gui.applyModernComponents(item, itemModel, tooltipStyle, hideTooltip);
        }
    }

    private static class NameplatesGuiHolder implements InventoryHolder {
        private final int page;
        private final int pages;
        private final Map<Integer, String> nameplatesBySlot = new HashMap<>();
        private final Map<Integer, String> actionsBySlot = new HashMap<>();
        private Inventory inventory;

        private NameplatesGuiHolder(int page, int pages) {
            this.page = page;
            this.pages = pages;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
