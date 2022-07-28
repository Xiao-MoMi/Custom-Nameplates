package net.momirealms.customnameplates.bossbar.protocollib;

import org.bukkit.boss.BarColor;

public class BossBarConfigP {

    private String text;
    private Overlay overlay;
    private BarColor color;
    private int rate;

    public BossBarConfigP(String text, Overlay overlay, BarColor color, int rate){
        this.text = text;
        this.overlay = overlay;
        this.color = color;
        this.rate = rate;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setOverlay(Overlay overlay) {
        this.overlay = overlay;
    }

    public void setColor(BarColor color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public int getRate() {
        return rate;
    }

    public BarColor getColor() {
        return color;
    }

    public Overlay getOverlay() {
        return overlay;
    }
}
