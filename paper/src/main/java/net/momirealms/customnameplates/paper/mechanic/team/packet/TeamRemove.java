package net.momirealms.customnameplates.paper.mechanic.team.packet;

public class TeamRemove {

    private String teamName;

    private TeamRemove() {
    }

    public TeamRemove(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final TeamRemove packet;

        public Builder() {
            this.packet = new TeamRemove();
        }

        public Builder teamName(String teamName) {
            packet.teamName = teamName;
            return this;
        }

        public TeamRemove build() {
            return packet;
        }
    }
}
