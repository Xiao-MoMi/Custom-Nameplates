package net.momirealms.customnameplates.api.data;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.mechanic.nameplate.Nameplate;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OnlineUser {

    private final Player player;
    private String nameplate;
    private String bubble;

    public OnlineUser(Player player, PlayerData playerData) {
        this.player = player;
        this.nameplate = playerData.getNameplate();
        this.bubble = playerData.getBubble();
    }

    public PlayerData toPlayerData() {
        return PlayerData.builder()
                .setBubble(bubble)
                .setNameplate(nameplate)
                .build();
    }

    public UUID getUUID() {
        return player.getUniqueId();
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Get the original nameplate key from data
     */
    public String getNameplateKey() {
        return nameplate;
    }

    /**
     * Get the original bubble key from data
     */
    public String getBubbleKey() {
        return bubble;
    }

    /**
     * This value might be inconsistent with the key get by "getNameplateKey()"
     * Because if a player doesn't have a nameplate, his nameplate would be the default one
     */
    public Nameplate getNameplate() {
        String temp = nameplate;
        if (temp.equals("none")) {
            temp = CustomNameplatesPlugin.get().getNameplateManager().getDefaultNameplate();
        }
        return CustomNameplatesPlugin.get().getNameplateManager().getNameplate(temp);
    }

    /**
     * Set nameplate for a player
     *
     * @param nameplate nameplate
     */
    public void setNameplate(String nameplate) {
        this.nameplate = nameplate;
    }

    /**
     * Set bubble for a player
     *
     * @param bubble bubble
     */
    public void setBubble(String bubble) {
        this.bubble = bubble;
    }
}
