package net.momirealms.customnameplates.listener.compatibility;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.events.CMIPlayerUnVanishEvent;
import com.Zrips.CMI.events.CMIPlayerVanishEvent;
import net.momirealms.customnameplates.object.carrier.NamedEntityCarrier;
import net.momirealms.customnameplates.object.carrier.NamedEntityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * g2213swo
 * This class is a listener for CMI events.
 * It is used to hide the nameplates of vanished players.
 */
public class CMIVanishListener implements Listener {
    private final NamedEntityCarrier namedEntityCarrier;

    private final CMI cmi;

    public CMIVanishListener(NamedEntityCarrier namedEntityCarrier) {
        this.namedEntityCarrier = namedEntityCarrier;
        cmi = CMI.getInstance();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerVanish(CMIPlayerVanishEvent event) {
        final Player player = event.getPlayer();
        NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
        asm.hide();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerUnVanish(CMIPlayerUnVanishEvent event) {
        final Player player = event.getPlayer();
        NamedEntityManager asm = namedEntityCarrier.getNamedEntityManager(player);
        asm.show();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerVanishJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
        if (cmiUser.isVanished()) {
            namedEntityCarrier.getNamedEntityManager(player).hide();
        }
    }
}
