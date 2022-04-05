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

package io.ib67.astralflow.item.oredict.internal;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.internal.AstralConstants;
import io.ib67.astralflow.item.oredict.IOreDict;
import io.ib67.util.Pair;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SimpleOreDict implements IOreDict {
    private final Multimap<String, Pair<ItemStack, Predicate<ItemStack>>> items = ArrayListMultimap.create();
    private volatile boolean locked = false;

    {
        HookType.ASTRALFLOW_STARTUP_COMPLETED.register(this::compile);
    }

    private void compile() {
        if (!AstralConstants.MOCKING) locked = true;
    }

    @Override
    public IOreDict registerItem(String dictKey, ItemStack prototype, Predicate<ItemStack> itemStackPredicate) {
        if (locked) throw new IllegalStateException("OreDict is locked due to server startup completed.");
        items.put(dictKey, Pair.of(prototype, itemStackPredicate));
        return this;
    }

    @Override
    public boolean matchItem(String oredictId, ItemStack itemStack) {
        return items.get(oredictId).stream().anyMatch(e -> e.value.test(itemStack));
    }

    @Override
    public Collection<? extends ItemStack> getItems(String dictKey) {
        return items.get(dictKey).stream().map(e -> e.key).collect(Collectors.toList()); // should we defensive-copy here?
    }
}
