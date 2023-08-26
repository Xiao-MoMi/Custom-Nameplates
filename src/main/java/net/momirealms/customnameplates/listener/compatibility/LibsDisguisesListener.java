package net.momirealms.customnameplates.listener.compatibility;

import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;
import net.momirealms.customnameplates.object.carrier.NamedEntityCarrier;
import net.momirealms.customnameplates.object.carrier.NamedEntityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * g2213swo
 * This class is a listener for LibsDisguises events.
 * It is used to hide the nameplates of disguised players.
 */
public class LibsDisguisesListener implements Listener {
    private final NamedEntityCarrier namedEntityCarrier;

    public LibsDisguisesListener(NamedEntityCarrier namedEntityCarrier) {
        this.namedEntityCarrier = namedEntityCarrier;
    }

    @EventHandler
    public void onPlayerDisguise(DisguiseEvent event) {
        if (event.getEntity() instanceof Player player) {
            NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
            asm.hide();
        }
    }

    @EventHandler
    public void onPlayerUnDisguise(UndisguiseEvent event) {
        if (event.getEntity() instanceof Player player && !event.isBeingReplaced()) {
            NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
            asm.show();
        }
    }
}
