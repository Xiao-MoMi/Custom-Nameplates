package net.momirealms.customnameplates.common.event;

public interface Cancellable {

    /**
     * Gets the cancelled state.
     */
    boolean cancelled();

    /**
     * Sets the cancelled state.
     */
    void cancelled(final boolean cancelled);
}