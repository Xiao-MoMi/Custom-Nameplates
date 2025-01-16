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

package net.momirealms.customnameplates.bukkit.command.feature;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import net.momirealms.customnameplates.bukkit.command.BukkitCommandFeature;
import net.momirealms.customnameplates.common.command.CustomNameplatesCommandManager;
import net.momirealms.customnameplates.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class NameplatesUnEquipCommand extends BukkitCommandFeature<CommandSender> {

    public NameplatesUnEquipCommand(CustomNameplatesCommandManager<CommandSender> commandManager, BukkitCustomNameplates plugin) {
        super(commandManager, plugin);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(context -> {
                    if (!ConfigManager.nameplateModule()) return;
                    CNPlayer player = plugin.getPlayer(context.sender().getUniqueId());
                    if (player == null) {
                        throw new RuntimeException("Player should not be null");
                    }
                    if (!player.isLoaded()) {
                        plugin.getPluginLogger().warn("Player " + player.name() + " tried to unequip a nameplate when data not loaded");
                        return;
                    }
                    if (player.nameplateData().equals("none")) {
                        handleFeedback(context, MessageConstants.COMMAND_NAMEPLATES_UNEQUIP_FAILURE_NOT_EQUIP);
                        return;
                    }
                    player.setNameplateData("none");
                    player.save();
                    handleFeedback(context, MessageConstants.COMMAND_NAMEPLATES_UNEQUIP_SUCCESS);
                });
    }

    @Override
    public String getFeatureID() {
        return "nameplates_unequip";
    }
}
