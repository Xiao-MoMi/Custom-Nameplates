package net.momirealms.customnameplates.api.feature.pack.width;

import net.momirealms.customnameplates.common.plugin.feature.Reloadable;

public interface WidthManager extends Reloadable {

    void loadTemplates();

    ConfigurableFontWidthData getFontData(String id);

    int getTextWidth(String text);
}
