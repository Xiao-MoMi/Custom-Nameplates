package net.momirealms.customnameplates.api.feature.actionbar;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;

public interface ActionBarManager extends Reloadable {

    void refreshConditions();

    void checkHeartBeats();

    ActionBarConfig getConfig(String name);

    ActionBarConfig[] allConfigs();

    String getExternalActionBar(CNPlayer<?> player);
}
