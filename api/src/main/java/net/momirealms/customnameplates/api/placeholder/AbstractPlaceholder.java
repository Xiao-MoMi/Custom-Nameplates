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

package net.momirealms.customnameplates.api.placeholder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract base class for placeholders, providing common functionality for managing children, ID, and refresh intervals.
 */
public abstract class AbstractPlaceholder implements Placeholder {

    protected String id;
    protected int countId = PlaceholderCounter.getAndIncrease();
    protected int refreshInterval;
    protected PlaceholderManager manager;
    protected Set<Placeholder> children = new HashSet<>();

    /**
     * Constructs an AbstractPlaceholder with the specified manager, ID, and refresh interval.
     *
     * @param manager         the PlaceholderManager that manages this placeholder
     * @param id              the ID of the placeholder, must start and end with '%'
     * @param refreshInterval the refresh interval for the placeholder, 0 will default to -1 (no refresh)
     */
    protected AbstractPlaceholder(PlaceholderManager manager, String id, int refreshInterval) {
        if (refreshInterval == 0) {
            refreshInterval = -1;
        }
        if (!(id.startsWith("%") && id.endsWith("%"))) {
            throw new IllegalArgumentException("Placeholder ID must start and end with '%'");
        }
        this.manager = manager;
        this.id = id;
        this.refreshInterval = refreshInterval;
    }

    /**
     * Returns the unique count ID of this placeholder.
     *
     * @return the count ID
     */
    @Override
    public int countId() {
        return countId;
    }

    /**
     * Returns the set of child placeholders associated with this placeholder.
     *
     * @return the set of child placeholders
     */
    @Override
    public Set<Placeholder> children() {
        return children;
    }

    /**
     * Adds a child placeholder to this placeholder.
     *
     * @param placeholder the child placeholder to add
     */
    @Override
    public void addChild(Placeholder placeholder) {
        children.add(placeholder);
    }

    /**
     * Adds multiple child placeholders to this placeholder.
     *
     * @param placeholders the set of child placeholders to add
     */
    @Override
    public void addChildren(Set<Placeholder> placeholders) {
        children.addAll(placeholders);
    }

    /**
     * Returns the refresh interval of this placeholder.
     *
     * @return the refresh interval
     */
    @Override
    public int refreshInterval() {
        return refreshInterval;
    }

    /**
     * Returns the ID of this placeholder.
     *
     * @return the placeholder ID
     */
    @Override
    public String id() {
        return id;
    }

    /**
     * Compares this placeholder with another object for equality based on the ID.
     *
     * @param o the object to compare
     * @return true if the placeholders are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractPlaceholder that = (AbstractPlaceholder) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Returns the hash code of this placeholder based on its ID.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
