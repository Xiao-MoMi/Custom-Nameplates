package net.momirealms.customnameplates.api.mechanic.tag.unlimited;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Vector;

public interface EntityTagEntity {

    void addTag(StaticTextEntity tag);

    StaticTextEntity addTag(StaticTextTagSetting setting);

    void removeTag(StaticTextEntity tag);

    Vector<StaticTextEntity> getStaticTags();

    void forceAddNearbyPlayer(Player player);

    void forceRemoveNearbyPlayer(Player player);

    Entity getEntity();

    void destroy();
}
