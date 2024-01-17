package net.momirealms.customnameplates.api.mechanic.team;

public class TeamRemovePacket {

    private String teamName;

    private TeamRemovePacket() {
    }

    public TeamRemovePacket(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final TeamRemovePacket packet;

        public Builder() {
            this.packet = new TeamRemovePacket();
        }

        public Builder teamName(String teamName) {
            packet.teamName = teamName;
            return this;
        }

        public TeamRemovePacket build() {
            return packet;
        }
    }
}
