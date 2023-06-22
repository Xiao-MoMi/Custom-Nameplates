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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.command.AbstractSubCommand;
import net.momirealms.customnameplates.manager.MessageManager;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.object.DisplayMode;
import net.momirealms.customnameplates.object.nameplate.NameplatesTeam;
import net.momirealms.customnameplates.utils.AdventureUtils;
import net.momirealms.customnameplates.utils.ArmorStandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PreviewCommand extends AbstractSubCommand {

    public static final AbstractSubCommand INSTANCE = new PreviewCommand();
    public PreviewCommand() {
        super("preview");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (noConsoleExecute(sender)) return true;
        Player player = (Player) sender;
        NameplateManager nameplateManager = CustomNameplates.getInstance().getNameplateManager();
        if (nameplateManager.isInCoolDown(player)) {
            AdventureUtils.playerMessage(player, MessageManager.prefix + MessageManager.coolDown);
            return true;
        }
        if (nameplateManager.getMode() == DisplayMode.TEAM) {
            NameplatesTeam team = CustomNameplates.getInstance().getTeamManager().getNameplateTeam(player.getUniqueId());
            if (team != null) {
                Component full = team.getNameplatePrefixComponent()
                        .append(Component.text(player.getName()).color(TextColor.color(AdventureUtils.colorToDecimal(team.getColor()))).font(Key.key("minecraft:default"))
                                .append(team.getNameplateSuffixComponent()));
                ArmorStandUtils.preview(full, player, (int) nameplateManager.getPreview_time());
            }
        } else if (nameplateManager.getMode() == DisplayMode.ARMOR_STAND || nameplateManager.getMode() == DisplayMode.TEXT_DISPLAY) {
            nameplateManager.showPlayerArmorStandTags(player);
        } else {
            AdventureUtils.playerMessage(player, MessageManager.prefix + "<white>Nameplate is disabled.");
            return true;
        }
        AdventureUtils.playerMessage(player, MessageManager.prefix + MessageManager.preview);
        return true;
    }
}
