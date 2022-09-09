package net.momirealms.customnameplates.actionbar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customnameplates.CustomNameplates;

public class ActionBarPacketsListener extends PacketAdapter {

    private ActionBarManager manager;

    public ActionBarPacketsListener(ActionBarManager manager) {
        super(CustomNameplates.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        this.manager = manager;
    }

    public void onPacketSending(PacketEvent event) {

    }
}
