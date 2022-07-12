package net.momirealms.customnameplates.data;

public class PlayerData {

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
