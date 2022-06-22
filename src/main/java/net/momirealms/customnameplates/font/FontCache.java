package net.momirealms.customnameplates.font;

import net.momirealms.customnameplates.nameplates.NameplateConfig;

public record FontCache(String name, FontChar fontChar, NameplateConfig config) {

    public static FontCache EMPTY;
    static {
        FontCache.EMPTY = new FontCache("none", new FontChar('小', '默', '米'), NameplateConfig.EMPTY);
    }

    public String getName() {
        return this.name;
    }

    public FontChar getChar() {
        return this.fontChar;
    }
    public NameplateConfig getConfig() {
        return this.config;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FontCache fontCache)) {
            return false;
        }
        if (!fontCache.canEqual(this)) {
            return false;
        }
        final String name = this.getName();
        final String name2 = fontCache.getName();
        Label_a:
        {
            if (name == null) {
                if (name2 == null) {
                    break Label_a;
                }
            } else if (name.equals(name2)) {
                break Label_a;
            }
            return false;
        }
        FontChar info = this.getChar();
        FontChar info2 = fontCache.getChar();
        Label_b:
        {
            if (info == null) {
                if (info2 == null) {
                    break Label_b;
                }
            } else if (info.equals(info2)) {
                break Label_b;
            }
            return false;
        }
        final NameplateConfig config = this.getConfig();
        final NameplateConfig config2 = fontCache.getConfig();
        if (config == null) {
            return config2 == null;
        } else return config.equals(config2);
    }

    @Override
    public int hashCode() {
        final int n = 1;
        final String name = this.getName();
        final int n2 = n * 59 + ((name == null) ? 43 : name.hashCode());
        final FontChar fontChar = this.getChar();
        final int n3 = n2 * 59 + ((fontChar == null) ? 43 : fontChar.hashCode());
        final NameplateConfig config = this.getConfig();
        return n3 * 59 + ((config == null) ? 43 : config.hashCode());
    }

    @Override
    public String toString() {
        return "FontCache(name=" + this.getName() + ", info=" + this.getChar() + ", config=" + this.getConfig() + ")";
    }

    private boolean canEqual(final Object other) {
        return other instanceof FontCache;
    }
}
