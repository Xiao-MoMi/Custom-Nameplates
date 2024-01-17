package net.momirealms.customnameplates.paper.mechanic.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.PlaceholderManager;
import net.momirealms.customnameplates.api.mechanic.character.ConfiguredChar;
import net.momirealms.customnameplates.api.mechanic.font.OffsetFont;
import net.momirealms.customnameplates.api.mechanic.placeholder.*;
import net.momirealms.customnameplates.api.util.FontUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluginPlaceholders extends PlaceholderExpansion {

    private final CustomNameplatesPlugin plugin;
    private final PlaceholderManager placeholderManager;

    public PluginPlaceholders(CustomNameplatesPlugin plugin, PlaceholderManager placeholderManager) {
        this.plugin = plugin;
        this.placeholderManager = placeholderManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "nameplates";
    }

    @Override
    public @NotNull String getAuthor() {
        return "XiaoMoMi";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.3";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        String[] mainSplit = params.split("_", 2);
        String mainPara = mainSplit[0];
        String mainArg = mainSplit.length == 1 ? "" : mainSplit[1];
        switch (mainPara) {
            case "image" -> {
                ConfiguredChar configuredChar = plugin.getImageManager().getImage(mainArg);
                if (configuredChar == null) return "Image not exists";
                return FontUtils.surroundNameplateFont(String.valueOf(configuredChar.getCharacter()));
            }
            case "image-char" -> {
                ConfiguredChar configuredChar = plugin.getImageManager().getImage(mainArg);
                if (configuredChar == null) return "Image not exists";
                return String.valueOf(configuredChar.getCharacter());
            }
            case "offset" -> {
                return FontUtils.surroundNameplateFont(OffsetFont.getOffsetChars(Integer.parseInt(mainArg)));
            }
            case "offset-char" -> {
                return OffsetFont.getOffsetChars(Integer.parseInt(mainArg));
            }
            case "checkupdate" -> {
                return String.valueOf(!plugin.getVersionManager().isLatest());
            }
            case "is-latest" -> {
                return String.valueOf(plugin.getVersionManager().isLatest());
            }
            case "static" -> {
                StaticText text = placeholderManager.getStaticText(mainArg);
                if (text == null) return "Static text not exists";
                return text.getValue(offlinePlayer);
            }
            case "unicode", "descent" -> {
                DescentText descentText = placeholderManager.getDescentText(mainArg);
                if (descentText == null) return "Descent text not exists";
                return descentText.getValue(offlinePlayer);
            }
            case "conditional" -> {
                ConditionalText conditionalText = placeholderManager.getConditionalText(mainArg);
                if (conditionalText == null) return "Conditional text not exists";
                return conditionalText.getValue(offlinePlayer);
            }
            case "nameplate" -> {
                NameplateText nameplateText = placeholderManager.getNameplateText(mainArg);
                if (nameplateText == null) return "Nameplate text not exists";
                return nameplateText.getValue(offlinePlayer);
            }
            case "background" -> {
                BackGroundText backGroundText = placeholderManager.getBackGroundText(mainArg);
                if (backGroundText == null) return "Background text not exists";
                return backGroundText.getValue(offlinePlayer);
            }
        }

        Player onlinePlayer = offlinePlayer.getPlayer();
        if (onlinePlayer == null) return null;
        switch (mainPara) {
            case "time" -> {
                long time = onlinePlayer.getWorld().getTime();
                String ap = time >= 6000 && time < 18000 ? " PM" : " AM";
                int hours = (int) (time / 1000) ;
                int minutes = (int) ((time - hours * 1000 ) * 0.06);
                hours += 6;
                while (hours >= 12) hours -= 12;
                if (minutes < 10) return hours + ":0" + minutes + ap;
                else return hours + ":" + minutes + ap;
            }
            case "actionbar" -> {
                return plugin.getActionBarManager().getOtherPluginActionBar(onlinePlayer);
            }
            case "prefix" -> {
                return plugin.getNameplateManager().getNameplatePrefix(onlinePlayer);
            }
            case "suffix" -> {
                return plugin.getNameplateManager().getNameplateSuffix(onlinePlayer);
            }
            case "nametag" -> {
                return plugin.getNameplateManager().getFullNameTag(onlinePlayer);
            }
            case "equipped" -> {
                var optPlayer = plugin.getStorageManager().getOnlineUser(onlinePlayer.getUniqueId());
                if (optPlayer.isEmpty()) return "Data not loaded";
                switch (mainArg) {
                    case "nameplate" -> {
                        return optPlayer.get().getNameplateKey();
                    }
                    case "bubble" -> {
                        return optPlayer.get().getBubbleKey();
                    }
                }
            }
        }
        return null;
    }

}
