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

package net.momirealms.customnameplates.bukkit.compatibility.emoji;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.font.FontManager;
import io.th0rgal.oraxen.font.Glyph;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.feature.chat.emoji.EmojiProvider;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

public class OraxenEmojiProvider implements EmojiProvider {
    private final FontManager fontManager;
    private final Function<Glyph, String> characterFunction;

    public OraxenEmojiProvider(int version) {
        if (version == 1) {
            this.fontManager = OraxenPlugin.get().getFontManager();
            this.characterFunction = Glyph::getCharacter;
        } else {
            try {
                Method fm = OraxenPlugin.class.getMethod("fontManager");
                this.fontManager = (FontManager) fm.invoke(OraxenPlugin.get());
                Method cm = Glyph.class.getMethod("character");
                this.characterFunction = (glyph -> {
                    try {
                        return (String) cm.invoke(glyph);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String replace(CNPlayer player, String text) {
        for (Map.Entry<String, Glyph> entry : this.fontManager.getGlyphByPlaceholderMap().entrySet()) {
            if (entry.getValue().hasPermission((Player) player.player())) {
                text = text.replace(entry.getKey(), "<white><font:default>" + characterFunction.apply(entry.getValue()) + "</font></white>");
            }
        }
        return text;
    }
}
