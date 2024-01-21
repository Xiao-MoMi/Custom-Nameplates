package net.momirealms.customnameplates.paper.mechanic.team.packet;

import net.kyori.adventure.text.Component;
import net.momirealms.customnameplates.common.team.TeamCollisionRule;
import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.common.team.TeamTagVisibility;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;

import java.util.Collection;

public class TeamCreate {

    // String i
    private String teamName;
    // Collection<String> j
    private Collection<String> members;

    /*
    Optional<b> k
     */
    // IChatBaseComponent a
    private Object teamDisplay;
    // IChatBaseComponent b
    private Object teamPrefix;
    // IChatBaseComponent c
    private Object teamSuffix;
    // String d
    private TeamTagVisibility tagVisibility;
    // String e
    private TeamCollisionRule collisionRule;
    // Enum f
    private TeamColor teamColor;

    private TeamCreate() {
    }

    public TeamCreate(
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

    public Object getTeamDisplay() {
        return teamDisplay;
    }

    public Object getTeamPrefix() {
        return teamPrefix;
    }

    public Object getTeamSuffix() {
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
        
        private final TeamCreate packet;

        public Builder() {
            this.packet = new TeamCreate();
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

        public Builder display(String display) {
            packet.teamDisplay = AdventureManagerImpl.getInstance().getIChatComponentFromMiniMessage(display);
            return this;
        }

        public Builder prefix(String prefix) {
            packet.teamPrefix = AdventureManagerImpl.getInstance().getIChatComponentFromMiniMessage(prefix);
            return this;
        }

        public Builder suffix(String suffix) {
            packet.teamSuffix = AdventureManagerImpl.getInstance().getIChatComponentFromMiniMessage(suffix);
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
        
        public TeamCreate build() {
            return packet;
        }
    }
}
