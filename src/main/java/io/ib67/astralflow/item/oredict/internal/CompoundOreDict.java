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

import io.ib67.astralflow.item.oredict.IOreDict;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
public final class CompoundOreDict implements IOreDict {
    private final List<IOreDict> oreDicts;

    @Override
    public IOreDict registerItem(String dictKey, ItemStack prototype, Predicate<ItemStack> tester) {
        for (IOreDict oreDict : oreDicts) {
            if (!(oreDict instanceof VanillaOreDict)) {
                oreDict.registerItem(dictKey, prototype, tester);
            }
        }
        return this;
    }

    @Override
    public boolean matchItem(String oredictId, ItemStack itemStack) {
        for (IOreDict oreDict : oreDicts) {
            var result = oreDict.matchItem(oredictId, itemStack);
            if (result) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<? extends ItemStack> getItems(String oredictId) {
        var list = new LinkedList<ItemStack>();
        for (IOreDict oreDict : oreDicts) {
            list.addAll(oreDict.getItems(oredictId));
        }
        return Collections.unmodifiableCollection(list);
    }
}
