/*
 *
 *   AstralFlow - The plugin who is turning bukkit into mod-pack
 *   Copyright (C) 2022 iceBear67
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

package io.ib67.astralflow.item;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

@UtilityClass
public final class ItemKeys {
    private final Map<String, ItemKey> cachedItemKeys = new WeakHashMap<>();

    public static final ItemKey from(String namespace, String id) {
        Objects.requireNonNull(namespace, "Namespace cannot be null");
        Objects.requireNonNull(id, "ID cannot be null");
        return cachedItemKeys.computeIfAbsent(namespace + ":" + id, k -> new SimpleItemKey(id, namespace));
    }

    public static ItemKey from(String namespaceAndID) {
        Objects.requireNonNull(namespaceAndID, "Namespace and ID cannot be null");
        var a = namespaceAndID.split(":");
        if (a.length != 2) {
            throw new IllegalArgumentException("Namespace and ID must be separated by a colon");
        }
        return from(a[0], a[1]);
    }

    private record SimpleItemKey(
            String id,
            String namespace
    ) implements ItemKey {
        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getNamespace() {
            return namespace;
        }
    }
}
