package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.common.Key;
import net.momirealms.customnameplates.api.mechanic.font.FontData;
import org.jetbrains.annotations.Nullable;

public interface WidthManager {

    boolean registerFontData(Key key, FontData fontData);

    boolean unregisterFontData(Key key);

    @Nullable FontData getFontData(Key key);

    int getTextWidth(String textWithTags);
}
