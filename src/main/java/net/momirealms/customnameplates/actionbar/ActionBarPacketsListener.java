package net.momirealms.customnameplates.actionbar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.*;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.font.FontOffset;
import net.momirealms.customnameplates.font.FontUtil;

import java.util.Map;

public class ActionBarPacketsListener extends PacketAdapter {

    private final ActionBarManager manager;

    public ActionBarPacketsListener(ActionBarManager manager) {
        super(CustomNameplates.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        this.manager = manager;
    }

    public void onPacketSending(PacketEvent event) {
//        PacketContainer packet = event.getPacket();
//        WrappedChatComponent chatComponent = packet.getChatComponents().read(0);
//        String json = chatComponent.getJson();
//        JsonElement jsonElement = new JsonParser().parse(json);
//        StringBuilder sb = new StringBuilder();
//        getJsonText(jsonElement, sb);
//        int width = FontUtil.getTotalWidth(sb + MiniMessage.miniMessage().stripTags(PlaceholderAPI.setPlaceholders(event.getPlayer(),ConfigManager.ActionbarConfig.text)));
//        String neg = FontOffset.getShortestNegChars(width/2);
//        Component raw = GsonComponentSerializer.gson().deserialize(json).append(Component.text(neg).font(ConfigManager.Main.key)).append(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(event.getPlayer(),ConfigManager.ActionbarConfig.text))).append(Component.text(neg).font(ConfigManager.Main.key));
//        WrappedChatComponent chatComponent1 = WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(raw));
//        packet.getChatComponents().write(0, chatComponent1);
    }
}
