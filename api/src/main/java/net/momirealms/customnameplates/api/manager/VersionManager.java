package net.momirealms.customnameplates.api.manager;

import java.util.concurrent.CompletionStage;

public interface VersionManager {

    boolean isFolia();

    String getServerVersion();

    CompletionStage<Boolean> checkUpdate();

    boolean isVersionNewerThan1_19();

    boolean isVersionNewerThan1_19_R2();

    boolean isVersionNewerThan1_19_R3();

    boolean isVersionNewerThan1_20();

    boolean isVersionNewerThan1_20_R2();

    String getPluginVersion();

    boolean isLatest();

    int getPackFormat();

}
