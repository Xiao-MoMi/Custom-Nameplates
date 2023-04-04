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

package net.momirealms.customnameplates.manager;

import net.momirealms.customnameplates.object.Function;
import net.momirealms.customnameplates.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager extends Function {

    public static String noPerm;
    public static String prefix;
    public static String lackArgs;
    public static String reload;
    public static String not_online;
    public static String no_console;
    public static String coolDown;
    public static String preview;
    public static String generate;
    public static String noNameplate;
    public static String generateDone;
    public static String np_equip;
    public static String np_unEquip;
    public static String np_force_equip;
    public static String np_force_unEquip;
    public static String np_not_exist;
    public static String np_notAvailable;
    public static String np_available;
    public static String np_haveNone;
    public static String bb_equip;
    public static String bb_unEquip;
    public static String bb_force_equip;
    public static String bb_force_unEquip;
    public static String bb_not_exist;
    public static String bb_notAvailable;
    public static String bb_available;
    public static String bb_haveNone;
    public static String nonArgs;
    public static String unavailableArgs;

    @Override
    public void load(){
        YamlConfiguration config = ConfigUtils.getConfig("messages" + File.separator + ConfigManager.lang +".yml");
        noPerm = config.getString("messages.no-perm");
        prefix = config.getString("messages.prefix");
        lackArgs = config.getString("messages.lack-args");
        reload = config.getString("messages.reload");
        coolDown = config.getString("messages.cooldown");
        preview = config.getString("messages.preview");
        generate = config.getString("messages.generate");
        generateDone = config.getString("messages.generate-done");
        noNameplate = config.getString("messages.no-nameplate");
        not_online = config.getString("messages.not-online");
        no_console = config.getString("messages.no-console");
        nonArgs = config.getString("messages.none-args");
        unavailableArgs = config.getString("messages.invalid-args");
        np_equip = config.getString("messages.equip-nameplates");
        np_unEquip = config.getString("messages.unequip-nameplates");
        np_force_equip = config.getString("messages.force-equip-nameplates");
        np_force_unEquip = config.getString("messages.force-unequip-nameplates");
        np_not_exist = config.getString("messages.not-exist-nameplates");
        np_notAvailable = config.getString("messages.not-available-nameplates");
        np_available = config.getString("messages.available-nameplates");
        np_haveNone = config.getString("messages.have-no-nameplates");
        bb_equip = config.getString("messages.equip-bubbles");
        bb_unEquip = config.getString("messages.unequip-bubbles");
        bb_force_equip = config.getString("messages.force-equip-bubbles");
        bb_force_unEquip = config.getString("messages.force-unequip-bubbles");
        bb_not_exist = config.getString("messages.not-exist-bubbles");
        bb_notAvailable = config.getString("messages.not-available-bubbles");
        bb_available = config.getString("messages.available-bubbles");
        bb_haveNone = config.getString("messages.have-no-bubbles");
    }
}
