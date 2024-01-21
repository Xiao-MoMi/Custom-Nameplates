package net.momirealms.customnameplates.common.team;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum TeamTagVisibility {

    ALWAYS("always"),
    HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
    HIDE_FOR_OWN_TEAM("hideForOwnTeam"),
    NEVER("never");

    private final String id;

    TeamTagVisibility(@NotNull String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @NotNull
    public static TeamTagVisibility byId(String id) {
        return Arrays.stream(values())
                .filter(mode -> mode.id.equals(id))
                .findFirst()
                .orElse(ALWAYS);
    }
}
