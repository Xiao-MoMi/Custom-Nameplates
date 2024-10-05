/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.api.network;

import net.momirealms.customnameplates.api.CNPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Tracker {

    private boolean isCrouching;
    private double scale;
    private final CNPlayer tracker;

    private final Set<Integer> passengerIDs = Collections.synchronizedSet(new HashSet<>());

    /**
     * Constructs a new Tracker for the specified player.
     *
     * @param tracker the player being tracked
     */
    public Tracker(CNPlayer tracker) {
        this.isCrouching = false;
        this.scale = 1;
        this.tracker = tracker;
    }

    public CNPlayer tracker() {
        return tracker;
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
}