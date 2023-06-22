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

package net.momirealms.customnameplates.command.subcmd;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.command.AbstractSubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.object.DisplayMode;
import net.momirealms.customnameplates.object.nameplate.NameplateConfig;
import net.momirealms.customnameplates.utils.AdventureUtils;
import net.momirealms.customnameplates.utils.ArmorStandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ForcePreviewCommand extends AbstractSubCommand {

    public static final AbstractSubCommand INSTANCE = new ForcePreviewCommand();

    public ForcePreviewCommand() {
        super("forcepreview");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (lackArgs(sender, 1, args.size()) || playerNotOnline(sender, args.get(0))) return true;
        Player player = Bukkit.getPlayer(args.get(0));
        assert player != null;
        NameplateManager nameplateManager = CustomNameplates.getInstance().getNameplateManager();
        if (nameplateManager.isInCoolDown(player)) {
            AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.coolDown);
            return true;
        }
        String nameplate = args.size() >= 2 ? args.get(1) : nameplateManager.getEquippedNameplate(player);
        NameplateConfig nameplateConfig = nameplateManager.getNameplateConfig(nameplate);
        if (nameplateConfig == null) {
            AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.np_not_exist);
            return true;
        }
        if (nameplateManager.getMode() == DisplayMode.TEAM) {
            String playerPrefix = AdventureUtils.replaceLegacy(PlaceholderAPI.setPlaceholders(player, nameplateManager.getPrefix()));
            String playerSuffix = AdventureUtils.replaceLegacy(PlaceholderAPI.setPlaceholders(player, nameplateManager.getSuffix()));
            String text = AdventureUtils.stripAllTags(playerPrefix) + player.getName() + AdventureUtils.stripAllTags(playerSuffix);
            String prefixImage = nameplateManager.getNameplatePrefixWithFont(text, nameplateConfig);
            String suffixImage = CustomNameplates.getInstance().getFontManager().getSuffixStringWithFont(text);
            Component holoComponent = MiniMessage.miniMessage().deserialize(prefixImage)
                    .append(Component.text(player.getName()).color(TextColor.color(AdventureUtils.colorToDecimal(nameplateConfig.color()))).font(Key.key("minecraft:default"))
                            .append(MiniMessage.miniMessage().deserialize(suffixImage)));
            ArmorStandUtils.preview(holoComponent, player, (int) nameplateManager.getPreview_time());
        } else if (nameplateManager.getMode() == DisplayMode.ARMOR_STAND || nameplateManager.getMode() == DisplayMode.TEXT_DISPLAY) {
            nameplateManager.showPlayerArmorStandTags(player, nameplate);
        } else {
            AdventureUtils.sendMessage(sender, MessageManager.prefix + "<white>Nameplate is disabled.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            return filterStartingWith(online_players(), args.get(0));
        }
        if (args.size() == 2) {
            return filterStartingWith(allNameplates(), args.get(1));
        }
        return null;
    }
}
