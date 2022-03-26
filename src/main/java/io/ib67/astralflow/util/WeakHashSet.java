/*
 *
 *   AstralFlow - The plugin enriches bukkit servers
 *   Copyright (C) 2022 The Inlined Lambdas and Contributors
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *   USA
 */

package io.ib67.astralflow.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WeakHashSet<E> implements Set<E> {
    private static final Object EMPTY_OBJ = new Object();
    private final Map<E, Object> map;

    public WeakHashSet(int capacity) {
        this.map = new WeakHashMap<>(capacity);
    }

    public WeakHashSet() {
        this(16);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return map.keySet().toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return map.keySet().toArray(a);
    }

    @Override
    public boolean add(E e) {
        return map.put(e, EMPTY_OBJ) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) != null;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        c.forEach(this::remove);
        return true;
    }

    @Override
    public void clear() {
        map.clear();
    }
}
