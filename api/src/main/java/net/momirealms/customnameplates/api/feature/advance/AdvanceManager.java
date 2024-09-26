package net.momirealms.customnameplates.api.feature.advance;

import net.momirealms.customnameplates.common.plugin.feature.Reloadable;

public interface AdvanceManager extends Reloadable {

    void loadTemplates();

    ConfigurableFontAdvanceData getFontData(String id);

    int getTextAdvance(String text);
}
