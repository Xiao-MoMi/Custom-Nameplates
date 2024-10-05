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

public class DummyPlaceholder implements Placeholder {

    private final String id;
    private final int counterId = PlaceholderCounter.getAndIncrease();

    public DummyPlaceholder(String id) {
        this.id = id;
    }

    @Override
    public void addChild(Placeholder placeholder) {
    }

    @Override
    public void addChildren(Set<Placeholder> placeholder) {
    }

    @Override
    public Set<Placeholder> children() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DummyPlaceholder that = (DummyPlaceholder) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
