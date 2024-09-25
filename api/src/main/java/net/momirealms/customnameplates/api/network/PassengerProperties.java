package net.momirealms.customnameplates.api.network;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PassengerProperties {

    private final int entityID;

    private boolean isCrouching;
    private double scale;

    private final Set<Integer> passengerIDs = Collections.synchronizedSet(new HashSet<>());

    public PassengerProperties(int entityID) {
        this.entityID = entityID;
        this.isCrouching = false;
        this.scale = 1;
    }

    public PassengerProperties(int entityID, boolean isCrouching, double scale) {
        this.entityID = entityID;
        this.isCrouching = isCrouching;
        this.scale = scale;
    }

    public boolean isCrouching() {
        return isCrouching;
    }

    public void setCrouching(boolean crouching) {
        isCrouching = crouching;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public int entityID() {
        return entityID;
    }

    public void addPassengerID(int passengerID) {
        passengerIDs.add(passengerID);
    }

    public void removePassengerID(int passengerID) {
        passengerIDs.remove(passengerID);
    }

    public Set<Integer> getPassengerIDs() {
        return passengerIDs;
    }

    public boolean isEmpty() {
        return passengerIDs.isEmpty();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PassengerProperties that = (PassengerProperties) object;
        return entityID == that.entityID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entityID);
    }
}
