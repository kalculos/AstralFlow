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

package io.ib67.astralflow.storage;

import io.ib67.astralflow.item.ItemState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MockItemStorage implements ItemStateStorage {
    private final Map<UUID, ItemState> states = new HashMap<>();

    @Override
    public boolean has(UUID uuid) {
        return states.containsKey(uuid);
    }

    @Override
    public ItemState get(UUID uuid) {
        return states.get(uuid);
    }

    @Override
    public Collection<? extends UUID> getKeys() {
        return states.keySet();
    }

    @Override
    public void save(UUID uuid, ItemState state) {
        states.put(uuid, state);
    }

    @Override
    public void remove(UUID uuid) {
        states.remove(uuid);
    }

    @Override
    public void flush() {

    }
}
