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
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.DummyPlayer;
import net.momirealms.customnameplates.api.util.Vector3;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import net.momirealms.customnameplates.bukkit.command.BukkitCommandFeature;
import net.momirealms.customnameplates.common.command.CustomNameplatesCommandManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class DebugTestCommand extends BukkitCommandFeature<CommandSender> {

    public DebugTestCommand(CustomNameplatesCommandManager<CommandSender> commandManager, BukkitCustomNameplates plugin) {
        super(commandManager, plugin);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(context -> {
                    Player owner = context.sender();
                    Location loc = context.sender().getLocation().add(5,0,5);
                    CustomNameplates plugin = CustomNameplates.getInstance();
                    CNPlayer cnPlayer = plugin.getPlayer(owner.getUniqueId());
                    Entity entity = owner.getWorld().spawn(loc, ArmorStand.class, e -> {
                        int fakeEntityId = e.getEntityId();
                        CNPlayer fakePlayer = new DummyPlayer(plugin, cnPlayer, fakeEntityId, new Vector3(loc.getX(), loc.getY(), loc.getZ()));
                        plugin.addPlayerUnsafe(fakeEntityId, fakePlayer);
                        plugin.getUnlimitedTagManager().onPlayerJoin(fakePlayer);
                    });
                    plugin.getScheduler().sync().runLater(() -> {
                        entity.remove();
                        CNPlayer fake = plugin.removePlayerUnsafe(entity.getEntityId());
                        if (fake != null) {
                            plugin.getUnlimitedTagManager().onPlayerQuit(fake);
                        }
                    }, 100, null);
                });
    }

    @Override
    public String getFeatureID() {
        return "debug_test";
    }
}
