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

package io.ib67.astralflow.item.recipe.choices;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.item.recipe.IngredientChoice;
import io.ib67.util.Lazy;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public final class OreDictChoice implements IngredientChoice {
    private final short count;
    private final short durability;
    private final Set<String> materials;
    private final Lazy<Set<String>, List<ItemStack>> compiledRItems = Lazy.by(t ->
            t.stream().map(e -> AstralFlow.getInstance().getItemRegistry().getOreDict().getItems(e)).flatMap(Collection::stream).collect(Collectors.toList())
    );

    public OreDictChoice(String... itemIds) {
        this((short) 1, (short) 0, itemIds);
    }

    public OreDictChoice(short durability, short count, String... oredictIds) {
        this.durability = durability;
        this.count = count;
        materials = Set.of(oredictIds);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        var ir = AstralFlow.getInstance().getItemRegistry();
        return materials.stream().anyMatch(e -> ir.getOreDict().matchItem(e, itemStack));
    }

    @Override
    public List<? extends ItemStack> getRepresentativeItems() {
        return compiledRItems.get();
    }
}
