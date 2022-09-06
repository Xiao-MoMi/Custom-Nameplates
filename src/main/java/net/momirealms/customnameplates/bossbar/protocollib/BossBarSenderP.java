package net.momirealms.customnameplates.bossbar.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.UUID;

public class BossBarSenderP extends BukkitRunnable {

    private final Player player;
    private PacketContainer packet;
    private int timer;
    private final BossBarConfigP bossbarConfig;
    private String text;
    private Overlay overlay;
    private BarColor barColor;

    public BossBarSenderP(Player player, BossBarConfigP bossbarConfig){
        this.player = player;
        this.bossbarConfig = bossbarConfig;
        this.timer = 0;
    }

    public void showBossbar(){
        this.packet = new PacketContainer(PacketType.Play.Server.BOSS);
        this.overlay = bossbarConfig.getOverlay();
        this.barColor = bossbarConfig.getColor();
        packet.getModifier().write(0, UUID.randomUUID());
        InternalStructure internalStructure = packet.getStructures().read(1);
        if (ConfigManager.MainConfig.placeholderAPI){
            this.text = PlaceholderAPI.setPlaceholders(player, bossbarConfig.getText());
        }else {
            this.text = bossbarConfig.getText();
        }
        internalStructure.getChatComponents().write(0, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(text))));
        internalStructure.getFloat().write(0,1F);
        internalStructure.getEnumModifier(BarColor.class, 2).write(0, barColor);
        internalStructure.getEnumModifier(Overlay.class, 3).write(0, overlay);
        internalStructure.getModifier().write(5, false);
        internalStructure.getModifier().write(6, false);
        internalStructure.getModifier().write(4, false);
        try{
            CustomNameplates.protocolManager.sendServerPacket(player, packet);
        }catch (InvocationTargetException e){
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Failed to display bossbar for "+player.getName());
        }
    }

    @Override
    public void run() {
        if (timer < bossbarConfig.getRate()){
            timer++;
        }else {
            timer = 0;
            String newText;
            Label_out: {
                if (ConfigManager.MainConfig.placeholderAPI){
                    newText = PlaceholderAPI.setPlaceholders(player, bossbarConfig.getText());
                }else {
                    newText = bossbarConfig.getText();
                }
                if (!Objects.equals(newText, text) || !Objects.equals(bossbarConfig.getColor(), barColor) || !Objects.equals(bossbarConfig.getOverlay(), overlay)){
                    text = newText;
                    barColor = bossbarConfig.getColor();
                    overlay = bossbarConfig.getOverlay();
                    break Label_out;
                }
                return;
            }
            InternalStructure internalStructure = packet.getStructures().read(1);
            internalStructure.getChatComponents().write(0, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(text))));
            internalStructure.getEnumModifier(BarColor.class, 2).write(0, barColor);
            internalStructure.getEnumModifier(Overlay.class, 3).write(0, overlay);
            internalStructure.getModifier().write(5, false);
            internalStructure.getModifier().write(6, false);
            internalStructure.getModifier().write(4, false);
            try{
                CustomNameplates.protocolManager.sendServerPacket(player, packet);
            }catch (InvocationTargetException e){
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Failed to display bossbar for " + player.getName());
            }
        }
    }
}
