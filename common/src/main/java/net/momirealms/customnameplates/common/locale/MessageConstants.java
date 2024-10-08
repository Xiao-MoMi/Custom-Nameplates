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

package net.momirealms.customnameplates.common.locale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

public interface MessageConstants {

    TranslatableComponent.Builder COMMAND_RELOAD_SUCCESS = Component.translatable().key("command.reload.success");
    TranslatableComponent.Builder COMMAND_RELOAD_GENERATING = Component.translatable().key("command.reload.generating");
    TranslatableComponent.Builder COMMAND_RELOAD_GENERATED = Component.translatable().key("command.reload.generated");
    TranslatableComponent.Builder COMMAND_DEBUG_PERFORMANCE = Component.translatable().key("command.debug.performance");

    TranslatableComponent.Builder COMMAND_NAMEPLATES_EQUIP_FAILURE_NOT_EXISTS = Component.translatable().key("command.nameplates.equip.failure.not_exists");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_EQUIP_FAILURE_PERMISSION = Component.translatable().key("command.nameplates.equip.failure.permission");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_EQUIP_FAILURE_NO_CHANGE = Component.translatable().key("command.nameplates.equip.failure.no_change");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_EQUIP_SUCCESS = Component.translatable().key("command.nameplates.equip.success");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_UNEQUIP_FAILURE_NOT_EQUIP = Component.translatable().key("command.nameplates.unequip.failure.not_equip");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_UNEQUIP_SUCCESS = Component.translatable().key("command.nameplates.unequip.success");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_PREVIEW_FAILURE_COOLDOWN = Component.translatable().key("command.nameplates.preview.failure.cooldown");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_PREVIEW_SUCCESS = Component.translatable().key("command.nameplates.preview.success");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_LIST_FAILURE_NONE = Component.translatable().key("command.nameplates.list.failure.none");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_LIST_SUCCESS = Component.translatable().key("command.nameplates.list.success");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_LIST_DELIMITER = Component.translatable().key("command.nameplates.list.delimiter");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_TOGGLE_ON = Component.translatable().key("command.nameplates.toggle.on");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_TOGGLE_OFF = Component.translatable().key("command.nameplates.toggle.off");

    TranslatableComponent.Builder COMMAND_NAMEPLATES_FORCE_EQUIP_SUCCESS = Component.translatable().key("command.nameplates.force_equip.success");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_FORCE_EQUIP_FAILURE_NOT_EXISTS = Component.translatable().key("command.nameplates.force_equip.failure.not_exists");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_FORCE_UNEQUIP_SUCCESS = Component.translatable().key("command.nameplates.force_unequip.success");
    TranslatableComponent.Builder COMMAND_NAMEPLATES_FORCE_PREVIEW_SUCCESS = Component.translatable().key("command.nameplates.force_preview.success");

    TranslatableComponent.Builder COMMAND_BUBBLES_EQUIP_FAILURE_NOT_EXISTS = Component.translatable().key("command.bubbles.equip.failure.not_exists");
    TranslatableComponent.Builder COMMAND_BUBBLES_EQUIP_FAILURE_PERMISSION = Component.translatable().key("command.bubbles.equip.failure.permission");
    TranslatableComponent.Builder COMMAND_BUBBLES_EQUIP_FAILURE_NO_CHANGE = Component.translatable().key("command.bubbles.equip.failure.no_change");
    TranslatableComponent.Builder COMMAND_BUBBLES_EQUIP_SUCCESS = Component.translatable().key("command.bubbles.equip.success");
    TranslatableComponent.Builder COMMAND_BUBBLES_UNEQUIP_FAILURE_NOT_EQUIP = Component.translatable().key("command.bubbles.unequip.failure.not_equip");
    TranslatableComponent.Builder COMMAND_BUBBLES_UNEQUIP_SUCCESS = Component.translatable().key("command.bubbles.unequip.success");
    TranslatableComponent.Builder COMMAND_BUBBLES_LIST_FAILURE_NONE = Component.translatable().key("command.bubbles.list.failure.none");
    TranslatableComponent.Builder COMMAND_BUBBLES_LIST_SUCCESS = Component.translatable().key("command.bubbles.list.success");
    TranslatableComponent.Builder COMMAND_BUBBLES_LIST_DELIMITER = Component.translatable().key("command.bubbles.list.delimiter");

    TranslatableComponent.Builder COMMAND_BUBBLES_FORCE_EQUIP_SUCCESS = Component.translatable().key("command.bubbles.force_equip.success");
    TranslatableComponent.Builder COMMAND_BUBBLES_FORCE_EQUIP_FAILURE_NOT_EXISTS = Component.translatable().key("command.bubbles.force_equip.failure.not_exists");
    TranslatableComponent.Builder COMMAND_BUBBLES_FORCE_UNEQUIP_SUCCESS = Component.translatable().key("command.bubbles.force_unequip.success");
}
