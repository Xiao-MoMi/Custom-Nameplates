package net.momirealms.customnameplates.bossbar;

import org.bukkit.boss.BarColor;

public class BossBarConfig {

    private String text;
    private Overlay overlay;
    private BarColor color;
    private int rate;

    public BossBarConfig(String text, Overlay overlay, BarColor color, int rate) {
        this.text = text;
        this.overlay = overlay;
        this.color = color;
        this.rate = rate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Overlay getOverlay() {
        return overlay;
    }

    public void setOverlay(Overlay overlay) {
        this.overlay = overlay;
    }

    public BarColor getColor() {
        return color;
    }

    public void setColor(BarColor color) {
        this.color = color;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
