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

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.glyphs.Glyph;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.feature.chat.emoji.EmojiProvider;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Map;

public class NexoEmojiProvider implements EmojiProvider {

    private final Object fontManager;
    private final Method glyphByPlaceholderMethod;
    private final Method hasPermissionMethod;
    private final Method characterMethod;

    public NexoEmojiProvider() {
        try {
            this.fontManager = NexoPlugin.instance().fontManager();
            this.glyphByPlaceholderMethod = findMethod(fontManager.getClass(),
                    "glyphByPlaceholder", "getGlyphByPlaceholderMap", "placeholderGlyphMap");
            Class<?> glyphClass = Glyph.class;
            this.hasPermissionMethod = findMethod(glyphClass, "hasPermission");
            this.characterMethod = findMethod(glyphClass, "character", "getCharacter", "getChars");
        } catch (Throwable e) {
            throw new RuntimeException("Failed to initialize NexoEmojiProvider. This may be caused by an incompatible Nexo version. " +
                    "Please ensure you are using a Nexo version compatible with this build of CustomNameplates.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String replace(CNPlayer player, String text) {
        try {
            Map<String, Glyph> glyphMap = (Map<String, Glyph>) glyphByPlaceholderMethod.invoke(fontManager);
            if (glyphMap == null) return text;

            for (Map.Entry<String, Glyph> entry : glyphMap.entrySet()) {
                Glyph glyph = entry.getValue();
                boolean hasPermission = true;
                if (hasPermissionMethod != null) {
                    try {
                        hasPermission = (boolean) hasPermissionMethod.invoke(glyph, (Player) player.player());
                    } catch (Throwable ignored) {
                    }
                }
                if (hasPermission) {
                    if (characterMethod == null) continue;
                    String character = String.valueOf(characterMethod.invoke(glyph));
                    text = text.replace(entry.getKey(), "<white><font:default>" + character + "</font></white>");
                }
            }
        } catch (Throwable ignored) {
        }
        return text;
    }

    private static Method findMethod(Class<?> clazz, String... names) {
        for (String name : names) {
            try {
                // Try no-arg method first
                return clazz.getMethod(name);
            } catch (NoSuchMethodException ignored) {
            }
            try {
                // Try with Player parameter (for hasPermission)
                return clazz.getMethod(name, Player.class);
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }
}
