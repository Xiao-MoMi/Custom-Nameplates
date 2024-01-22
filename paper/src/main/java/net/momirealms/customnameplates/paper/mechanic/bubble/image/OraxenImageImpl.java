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

package net.momirealms.customnameplates.paper.mechanic.bubble.image;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.font.FontManager;
import io.th0rgal.oraxen.font.Glyph;
import org.bukkit.entity.Player;

import java.util.Map;

public class OraxenImageImpl implements ImageParser{

    private final FontManager fontManager;

    public OraxenImageImpl() {
        this.fontManager = OraxenPlugin.get().getFontManager();
    }

    @Override
    public String parse(Player player, String text) {
        for (Map.Entry<String, Glyph> entry : this.fontManager.getGlyphByPlaceholderMap().entrySet()) {
            if (entry.getValue().hasPermission(player)) {
                text = text.replace(entry.getKey(), "<white>" + entry.getValue().getCharacter() + "</white>");
            }
        }
        return text;
    }
}
