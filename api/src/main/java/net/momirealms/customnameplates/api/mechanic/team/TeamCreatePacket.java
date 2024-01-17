package net.momirealms.customnameplates.api.mechanic.team;

import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.Collections;

public class TeamCreatePacket {

    // String i
    private String teamName;
    // Collection<String> j
    private Collection<String> members;

    /*
    Optional<b> k
     */
    // IChatBaseComponent a
    private Component teamDisplay;
    // IChatBaseComponent b
    private Component teamPrefix;
    // IChatBaseComponent c
    private Component teamSuffix;
    // String d
    private TeamTagVisibility tagVisibility;
    // String e
    private TeamCollisionRule collisionRule;
    // Enum f
    private TeamColor teamColor;

    private TeamCreatePacket() {
        this.teamName = "";
        this.members = Collections.singleton("");
        this.teamDisplay = Component.text("");
        this.teamPrefix = Component.text("");
        this.teamSuffix = Component.text("");
        this.tagVisibility = TeamTagVisibility.ALWAYS;
        this.collisionRule = TeamCollisionRule.ALWAYS;
        this.teamColor = TeamColor.WHITE;
    }

    public TeamCreatePacket(
            String teamName,
            Collection<String> members,
            Component teamDisplay,
            Component teamPrefix,
            Component teamSuffix,
            TeamTagVisibility tagVisibility,
            TeamCollisionRule collisionRule,
            TeamColor teamColor
    ) {
        this.teamName = teamName;
        this.members = members;
        this.teamDisplay = teamDisplay;
        this.teamPrefix = teamPrefix;
        this.teamSuffix = teamSuffix;
        this.tagVisibility = tagVisibility;
        this.collisionRule = collisionRule;
        this.teamColor = teamColor;
    }

    public String getTeamName() {
        return teamName;
    }

    public Collection<String> getMembers() {
        return members;
    }

    public Component getTeamDisplay() {
        return teamDisplay;
    }

    public Component getTeamPrefix() {
        return teamPrefix;
    }

    public Component getTeamSuffix() {
        return teamSuffix;
    }

    public TeamTagVisibility getTagVisibility() {
        return tagVisibility;
    }

    public TeamCollisionRule getCollisionRule() {
        return collisionRule;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        
        private final TeamCreatePacket packet;

        public Builder() {
            this.packet = new TeamCreatePacket();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder teamName(String name) {
            packet.teamName = name;
            return this;
        }

        public Builder members(Collection<String> members) {
            packet.members = members;
            return this;
        }

        public Builder display(Component display) {
            packet.teamDisplay = display;
            return this;
        }

        public Builder prefix(Component prefix) {
            packet.teamPrefix = prefix;
            return this;
        }

        public Builder suffix(Component suffix) {
            packet.teamSuffix = suffix;
            return this;
        }

        public Builder color(TeamColor color) {
            packet.teamColor = color;
            return this;
        }

        public Builder tagVisibility(TeamTagVisibility visibility) {
            packet.tagVisibility = visibility;
            return this;
        }

        public Builder collisionRule(TeamCollisionRule rule) {
            packet.collisionRule = rule;
            return this;
        }
        
        public TeamCreatePacket build() {
            return packet;
        }
    }
}
