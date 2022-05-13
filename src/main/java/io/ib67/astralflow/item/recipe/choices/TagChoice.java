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

package io.ib67.astralflow.item.recipe.choices;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.ib67.astralflow.item.recipe.IngredientChoice;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Objects;

/**
 * A {@link IngredientChoice} that is based on {@link org.bukkit.Tag}.
 */
@ApiStatus.AvailableSince("0.2.0-M1")
public class TagChoice implements IngredientChoice {
    private static final Multimap<Tag<Material>, ItemStack> cache = ArrayListMultimap.create();
    @Getter
    private final short count;
    @Getter
    private final short durability;
    private final Tag<Material> tag;

    public TagChoice(Tag<Material> tag) {
        this((short) 1, (short) 0, tag);
    }

    public TagChoice(short count, short durability, Tag<Material> tag) {
        this.count = count;
        this.durability = durability;
        Objects.requireNonNull(tag, "Tag cannot be null");
        this.tag = tag;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return tag.isTagged(itemStack.getType());
    }

    @Override
    public List<? extends ItemStack> getRepresentativeItems() {
        if (!cache.containsKey(tag)) {
            cache.putAll(tag, tag.getValues().stream().map(ItemStack::new).toList());
        }
        return (List<? extends ItemStack>) cache.get(tag);
    }
}
