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

public abstract class AbstractPlaceholder implements Placeholder {

    protected String id;
    protected int countId = PlaceholderCounter.getAndIncrease();
    protected int refreshInterval;
    protected PlaceholderManager manager;
    protected Set<Placeholder> children = new HashSet<>();
    protected Set<Placeholder> parents = new HashSet<>();

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

    @Override
    public int countId() {
        return countId;
    }

    @Override
    public Set<Placeholder> children() {
        return children;
    }

    @Override
    public void addChild(Placeholder placeholder) {
        children.add(placeholder);
    }

    @Override
    public void addChildren(Set<Placeholder> placeholders) {
        children.addAll(placeholders);
    }

    @Override
    public void addParent(Placeholder placeholder) {
        parents.add(placeholder);
    }

    @Override
    public void addParents(Set<Placeholder> placeholders) {
        parents.addAll(placeholders);
    }

    @Override
    public Set<Placeholder> parents() {
        return parents;
    }

    @Override
    public int refreshInterval() {
        return refreshInterval;
    }

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
