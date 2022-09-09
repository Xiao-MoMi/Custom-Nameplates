package net.momirealms.customnameplates.nameplates;

import java.util.Objects;

public class TeamInfo {

    private final String prefix;
    private final String suffix;

    public TeamInfo(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamInfo teamInfo = (TeamInfo) o;
        return Objects.equals(prefix, teamInfo.prefix) && Objects.equals(suffix, teamInfo.suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, suffix);
    }
}
