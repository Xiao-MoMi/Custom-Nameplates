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

import java.util.Objects;
import java.util.Set;

/**
 * A placeholder implementation used as a dummy or placeholder that does not have any child placeholders or refresh behavior.
 */
public class DummyPlaceholder implements Placeholder {

    private final String id;
    private final int counterId = PlaceholderCounter.getAndIncrease();

    /**
     * Constructs a DummyPlaceholder with the specified ID.
     *
     * @param id the ID of the placeholder
     */
    public DummyPlaceholder(String id) {
        this.id = id;
    }

    @Override
    public void addChild(Placeholder placeholder) {
    }

    @Override
    public void addChildren(Set<Placeholder> placeholders) {
    }

    @Override
    public void addParent(Placeholder placeholder) {
    }

    @Override
    public void addParents(Set<Placeholder> placeholders) {
    }

    @Override
    public Set<Placeholder> children() {
        return Set.of();
    }

    @Override
    public Set<Placeholder> parents() {
        return Set.of();
    }

    @Override
    public int refreshInterval() {
        return -1;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public int countId() {
        return counterId;
    }

    /**
     * Checks if this DummyPlaceholder is equal to another object based on the ID.
     *
     * @param o the object to compare
     * @return true if the placeholders are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DummyPlaceholder that = (DummyPlaceholder) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Returns the hash code of this DummyPlaceholder based on its ID.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
