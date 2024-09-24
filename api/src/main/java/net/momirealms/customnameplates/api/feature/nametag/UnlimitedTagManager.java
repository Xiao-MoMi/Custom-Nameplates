package net.momirealms.customnameplates.api.feature.nametag;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;

public interface UnlimitedTagManager extends Reloadable {

    void onTick();

    TagConfig getConfig(String name);

    TagConfig[] allConfigs();

    Runnable onAddPlayer(CNPlayer player, CNPlayer added);

    void onRemovePlayer(CNPlayer player, CNPlayer removed);
}
