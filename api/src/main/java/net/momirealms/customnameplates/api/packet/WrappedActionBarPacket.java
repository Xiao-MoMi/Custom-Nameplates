package net.momirealms.customnameplates.api.packet;

public class WrappedActionBarPacket {

    private final Object component;

    public WrappedActionBarPacket(Object component) {
        this.component = component;
    }

    public Object component() {
        return component;
    }
}
