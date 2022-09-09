package net.momirealms.customnameplates.actionbar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.CustomNameplates;

public class ActionBarPacketsListener extends PacketAdapter {

    private ActionBarManager manager;

    public ActionBarPacketsListener(ActionBarManager manager) {
        super(CustomNameplates.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        this.manager = manager;
    }

    public void onPacketSending(PacketEvent event) {
//        PacketContainer packet = event.getPacket();
//        WrappedChatComponent chatComponent = packet.getChatComponents().read(0);
//        Component component = GsonComponentSerializer.gson().deserialize(chatComponent.getJson());
    }
}
