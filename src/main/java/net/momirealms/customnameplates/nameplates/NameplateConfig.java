package net.momirealms.customnameplates.nameplates;

import org.bukkit.ChatColor;

public record NameplateConfig(ChatColor color, int height, String name, int yoffset) {

    public static NameplateConfig EMPTY;

    static {
        EMPTY = new NameplateConfig(ChatColor.WHITE, 16, "none", 12);
    }

    //获取Team颜色
    public ChatColor getColor() {
        return this.color;
    }

    //获取自定义font大小
    public int getHeight() {
        return this.height;
    }

    //获取铭牌名
    public String getName() {
        return this.name;
    }

    public int getyoffset() {return  this.yoffset; }
}
