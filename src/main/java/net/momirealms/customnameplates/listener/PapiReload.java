package net.momirealms.customnameplates.listener;

import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PapiReload implements Listener {

    @EventHandler
    public void onReload(me.clip.placeholderapi.events.ExpansionUnregisterEvent event){
        if (CustomNameplates.placeholders != null){
            if (event.getExpansion().equals(CustomNameplates.placeholders)){
                CustomNameplates.placeholders.register();
            }
        }
    }
}