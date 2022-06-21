package net.momirealms.customnameplates.nameplates;

import net.momirealms.customnameplates.font.FontCache;
import net.momirealms.customnameplates.font.FontNegative;
import net.momirealms.customnameplates.font.FontWidth;
import org.bukkit.ChatColor;

public class NameplateUtil {

    private final FontCache fontcache;

    public NameplateUtil(FontCache font) {
        this.fontcache = font;
    }

    /*
    根据玩家名构造长度适合的铭牌字符
    当然这个玩家名是带上前缀与后缀的
     */
    public String makeCustomNameplate(String prefix, String name, String suffix) {
        int totalWidth = FontWidth.getTotalWidth(ChatColor.stripColor(prefix + name + suffix));
        boolean isEven = totalWidth % 2 == 0; //奇偶判断
        char left = this.fontcache.getChar().getLeft();
        char middle = this.fontcache.getChar().getMiddle();
        char right = this.fontcache.getChar().getRight();
        char neg_1 = FontNegative.NEG_1.getCharacter();
        int left_offset = totalWidth + 16 + 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FontNegative.getShortestNegChars(isEven ? left_offset : left_offset + 1)); //先向左平移一个正方形的距离
        stringBuilder.append(left).append(neg_1); //将铭牌的左部分拼接
        int mid_amount = (totalWidth + 1) / 16; //显示名称的总长，如果超过一个正方形则多复制几个正方形
        for (int i = 0; i < (mid_amount == 0 ? 1 : mid_amount); i++) {
            stringBuilder.append(middle).append(neg_1); //减一是字符之间的间距（3）
        }
        stringBuilder.append(FontNegative.getShortestNegChars(16 - ((totalWidth + 1) % 16 + (isEven ? 0 : 1))));
        stringBuilder.append(middle).append(neg_1);
        stringBuilder.append(right).append(neg_1); //将铭牌的右部分拼接
        stringBuilder.append(FontNegative.getShortestNegChars(isEven ? left_offset : left_offset + 1)); //首尾对称处理，保证铭牌位于正中央
        return stringBuilder.toString();
    }

    /*
    用于为增加了后缀的玩家名计算负空格
    保证铭牌总是位于玩家头顶中央的位置
     */
    public String getSuffixLength(String name) {
        final int totalWidth = FontWidth.getTotalWidth(ChatColor.stripColor(name));
        return FontNegative.getShortestNegChars(totalWidth + totalWidth % 2 + 1);
    }

    /*
    获取铭牌上玩家名的颜色
     */
    public ChatColor getColor() {
        return this.fontcache.getConfig().getColor();
    }
}
