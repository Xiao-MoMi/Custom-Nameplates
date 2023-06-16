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

package net.momirealms.customnameplates.object.nameplate;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.object.DynamicText;
import net.momirealms.customnameplates.utils.AdventureUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NameplatesTeam {

    private final NameplateManager nameplateManager;
    private final Player player;
    private ChatColor color;
    private final DynamicText prefix;
    private final DynamicText suffix;
    private String nameplate_prefix;
    private String nameplate_suffix;
    private String nameplate;
    private final String team_name;

    public NameplatesTeam(NameplateManager nameplateManager, Player player, String team_name) {
        this.nameplateManager = nameplateManager;
        this.team_name = team_name;
        this.player = player;
        this.prefix = new DynamicText(player, nameplateManager.getPrefix());
        this.suffix = new DynamicText(player, nameplateManager.getSuffix());
        update(true);
    }

    public boolean update(boolean force) {
        String newNameplate = nameplateManager.getEquippedNameplate(player);
        NameplateConfig nameplateConfig = nameplateManager.getNameplateConfig(newNameplate);
        boolean updated = prefix.update() || suffix.update() || force || !newNameplate.equals(nameplate);
        if (newNameplate.equals("none") || nameplateConfig == null) {
            this.color = ChatColor.WHITE;
            this.nameplate = "none";
            if (updated) {
                nameplate = newNameplate;
                nameplate_prefix = prefix.getLatestValue();
                nameplate_suffix = suffix.getLatestValue();
            }
        } else {
            this.color = nameplateConfig.color();
            String name = PlaceholderAPI.setPlaceholders(player, nameplateManager.getPlayerNamePapi());
            if (updated) {
                nameplate = newNameplate;
                String text = AdventureUtils.stripAllTags(prefix.getLatestValue())
                        + name +
                        AdventureUtils.stripAllTags(suffix.getLatestValue());
                this.nameplate_prefix = nameplateManager.getNameplatePrefixWithFont(text, nameplateConfig) + prefix.getLatestValue();
                this.nameplate_suffix = CustomNameplates.getInstance().getFontManager().getSuffixStringWithFont(text) + suffix.getLatestValue();
            }
        }
        return updated;
    }

    public String getTeam_name() {
        return team_name;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public String getNameplatePrefixText() {
        return nameplate_prefix;
    }

    public String getNameplateSuffixText() {
        return nameplate_suffix;
    }

    public Component getNameplatePrefixComponent() {
        return AdventureUtils.getComponentFromMiniMessage(nameplate_prefix);
    }

    public Component getNameplateSuffixComponent() {
        return AdventureUtils.getComponentFromMiniMessage(nameplate_suffix);
    }
}