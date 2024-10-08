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

package net.momirealms.customnameplates.bukkit.command;

import net.kyori.adventure.util.Index;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import net.momirealms.customnameplates.bukkit.command.feature.*;
import net.momirealms.customnameplates.common.command.AbstractCommandManager;
import net.momirealms.customnameplates.common.command.CommandFeature;
import net.momirealms.customnameplates.common.sender.Sender;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.setting.ManagerSetting;

import java.util.List;

public class BukkitCommandManager extends AbstractCommandManager<CommandSender> {

    private final BukkitCustomNameplates plugin;
    private final Index<String, CommandFeature<CommandSender>> INDEX;

    public BukkitCommandManager(BukkitCustomNameplates plugin) {
        super(plugin, new LegacyPaperCommandManager<>(
                plugin.getBootstrap(),
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        ));
        this.plugin = plugin;
        List<CommandFeature<CommandSender>> FEATURES = List.of(
                new ReloadCommand(this, plugin),
                new DebugPerformanceCommand(this, plugin),
                new DebugWidthCommand(this, plugin),
                new DebugLinesCommand(this, plugin),
                new DebugTestCommand(this, plugin),
                new NameplatesEquipCommand(this, plugin),
                new NameplatesUnEquipCommand(this, plugin),
                new NameplatesListCommand(this, plugin),
                new NameplatesPreviewCommand(this, plugin),
                new NameplatesForceUnEquipCommand(this, plugin),
                new NameplatesForcePreviewCommand(this, plugin),
                new NameplatesForceEquipCommand(this, plugin),
                new NameplatesToggleCommand(this, plugin),
                new BubblesForceUnEquipCommand(this, plugin),
                new BubblesListCommand(this, plugin),
                new BubblesUnEquipCommand(this, plugin),
                new BubblesForceEquipCommand(this, plugin),
                new BubblesEquipCommand(this, plugin)
        );
        this.INDEX = Index.create(CommandFeature::getFeatureID, FEATURES);
        final LegacyPaperCommandManager<CommandSender> manager = (LegacyPaperCommandManager<CommandSender>) getCommandManager();
        manager.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            manager.registerBrigadier();
            manager.brigadierManager().setNativeNumberSuggestions(true);
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }
    }

    @Override
    protected Sender wrapSender(CommandSender sender) {
        return plugin.getSenderFactory().wrap(sender);
    }

    @Override
    public Index<String, CommandFeature<CommandSender>> getFeatures() {
        return INDEX;
    }
}
