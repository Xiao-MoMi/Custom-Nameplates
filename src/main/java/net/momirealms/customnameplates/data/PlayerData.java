package net.momirealms.customnameplates.data;

import net.momirealms.customnameplates.ConfigManager;

public class PlayerData {

    public static PlayerData EMPTY;
    static {
        EMPTY = new PlayerData(ConfigManager.MainConfig.default_nameplate, 0);
    }

    private String equipped;
    private int accepted;

    public PlayerData(String equipped, int accepted) {
        this.equipped = equipped;
        this.accepted = accepted;
    }

    public int getAccepted(){
        return this.accepted;
    }

    public void setAccepted(int accepted){
        this.accepted = accepted;
    }

    public String getEquippedNameplate() {
        return this.equipped;
    }

    public void equipNameplate(String nameplate) {
        this.equipped = nameplate.toLowerCase();
    }
}
