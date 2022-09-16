package net.momirealms.customnameplates.nameplates;

import net.momirealms.customnameplates.objects.SimpleChar;

public record BubbleConfig(String format, String name,
                           SimpleChar left,
                           SimpleChar middle,
                           SimpleChar right,
                           SimpleChar tail) {

    @Override
    public String format() {
        return format;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public SimpleChar tail() {
        return tail;
    }

    @Override
    public SimpleChar left() {
        return left;
    }

    @Override
    public SimpleChar middle() {
        return middle;
    }

    @Override
    public SimpleChar right() {
        return right;
    }
}
