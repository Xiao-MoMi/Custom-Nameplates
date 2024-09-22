package net.momirealms.customnameplates.api.placeholder;

public interface SharedPlaceholder extends Placeholder {

    String request();

    void update();

    String getLatestValue();
}
