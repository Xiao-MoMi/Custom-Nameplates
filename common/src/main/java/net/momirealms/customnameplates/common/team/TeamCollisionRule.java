package net.momirealms.customnameplates.common.team;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum TeamCollisionRule {

    ALWAYS("always"),
    NEVER("never"),
    PUSH_OTHER_TEAMS("pushOtherTeams"),
    PUSH_OWN_TEAM("pushOwnTeam");

    private final String id;

    TeamCollisionRule(@NotNull String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @NotNull
    public static TeamCollisionRule byId(String id) {
        return Arrays.stream(values())
                .filter(mode -> mode.id.equals(id))
                .findFirst()
                .orElse(ALWAYS);
    }
}
