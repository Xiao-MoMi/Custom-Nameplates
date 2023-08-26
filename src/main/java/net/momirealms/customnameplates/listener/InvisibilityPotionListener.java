package net.momirealms.customnameplates.listener;

import net.momirealms.customnameplates.object.carrier.NamedEntityCarrier;
import net.momirealms.customnameplates.object.carrier.NamedEntityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

/**
 * g2213swo
 * This class is a listener for potion effect events.
 * It is used to hide the nameplates of invisible players.
 * I think this feature should be left to the server administrators to decide whether to enable
 * If you don't want this feature, you can delete this class.
 */
public class InvisibilityPotionListener implements Listener {

    private final NamedEntityCarrier namedEntityCarrier;

    public InvisibilityPotionListener(NamedEntityCarrier namedEntityCarrier) {
        this.namedEntityCarrier = namedEntityCarrier;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerEffect(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player player) {
            final NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
            final EntityPotionEffectEvent.Action action = event.getAction();
            if (event.getModifiedType().equals(PotionEffectType.INVISIBILITY)) {
                switch (action) {
                    case ADDED, CHANGED -> {
                        asm.hide();
                    }
                    case REMOVED, CLEARED -> {
                        asm.show();
                    }
                }
            }
        }
    }
}
