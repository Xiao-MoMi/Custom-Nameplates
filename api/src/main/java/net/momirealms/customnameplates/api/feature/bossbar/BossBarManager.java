package net.momirealms.customnameplates.api.feature.bossbar;

import net.momirealms.customnameplates.common.plugin.feature.Reloadable;

public interface BossBarManager extends Reloadable {

    void onTick();

    BossBarConfig getConfig(String name);

    BossBarConfig[] allConfigs();
}
