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

package io.ib67.astralflow.item.recipe;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.internal.item.state.InternalItemState;
import io.ib67.astralflow.item.StateScope;
import io.ib67.astralflow.item.factory.ItemPrototypeFactory;
import io.ib67.util.Lazy;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public final class AstralItemChoice implements IngredientChoice {
    private final short count;
    private final short durability;
    private final Set<String> materials;
    private final Lazy<Set<String>, List<ItemStack>> compiledRItems = Lazy.by(t ->
            t.stream().map(e -> AstralFlow.getInstance().getItemRegistry().getRegistry(e))
                    .map(ItemPrototypeFactory::getPrototype).collect(Collectors.toList())
    );

    public AstralItemChoice(String... itemIds) {
        this((short) 1, (short) 0, itemIds);
    }

    public AstralItemChoice(short durability, short count, String... itemIds) {
        this.durability = durability;
        this.count = count;
        materials = Set.of(itemIds);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        var ir = AstralFlow.getInstance().getItemRegistry();
        var isItem = ir.isItem(itemStack);
        if (isItem) {
            var state = (InternalItemState) ir.getState(itemStack, StateScope.INTERNAL_ITEM);
            return materials.contains(state.getPrototypeKey());
        }
        return false;
    }

    @Override
    public List<? extends ItemStack> getRepresentativeItems() {
        return compiledRItems.get();
    }
}
