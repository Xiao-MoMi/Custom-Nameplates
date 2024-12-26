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

package net.momirealms.customnameplates.bukkit.compatibility.quest;

import com.typewritermc.engine.paper.events.AsyncCinematicEndEvent;
import com.typewritermc.engine.paper.events.AsyncCinematicStartEvent;
import com.typewritermc.engine.paper.events.AsyncDialogueEndEvent;
import com.typewritermc.engine.paper.events.AsyncDialogueStartEvent;
import net.momirealms.customnameplates.api.AbstractCNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TypeWriterListener implements Listener {

    private final CustomNameplates plugin;

    public TypeWriterListener(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDialogueStart(AsyncDialogueStartEvent event) {
        if (!ConfigManager.twDialogue()) return;
        Player player = event.getPlayer();
        AbstractCNPlayer cnPlayer = (AbstractCNPlayer) plugin.getPlayer(player.getUniqueId());
        cnPlayer.acquireActionBar("TWDialogue");
    }

    @EventHandler(ignoreCancelled = true)
    public void onDialogueEnd(AsyncDialogueEndEvent event) {
        if (!ConfigManager.twDialogue()) return;
        Player player = event.getPlayer();
        AbstractCNPlayer cnPlayer = (AbstractCNPlayer) plugin.getPlayer(player.getUniqueId());
        cnPlayer.releaseActionBar("TWDialogue");
    }

    @EventHandler(ignoreCancelled = true)
    public void onCinematicStart(AsyncCinematicStartEvent event) {
        if (!ConfigManager.twCinematic()) return;
        Player player = event.getPlayer();
        AbstractCNPlayer cnPlayer = (AbstractCNPlayer) plugin.getPlayer(player.getUniqueId());
        cnPlayer.acquireActionBar("TWCinematic");
    }

    @EventHandler(ignoreCancelled = true)
    public void onCinematicEnd(AsyncCinematicEndEvent event) {
        if (!ConfigManager.twCinematic()) return;
        Player player = event.getPlayer();
        AbstractCNPlayer cnPlayer = (AbstractCNPlayer) plugin.getPlayer(player.getUniqueId());
        cnPlayer.releaseActionBar("TWCinematic");
    }
}
