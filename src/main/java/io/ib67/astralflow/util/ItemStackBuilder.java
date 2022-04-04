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

import io.ib67.util.bukkit.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ItemStackBuilder {
    private Material material = Material.AIR;
    private List<String> lore = new ArrayList<>();
    private int customModelId;
    private String displayName;
    private List<ItemFlag> itemFlags = new ArrayList<>();

    private boolean unbreakable;

    public ItemStackBuilder unbreakable() {
        this.unbreakable = true;
        return this;
    }

    public ItemStackBuilder material(Material material) {
        this.material = material;
        return this;
    }

    public ItemStackBuilder lore(String... strings) {
        for (String string : strings) {
            lore.add(Text.colored(string));
        }
        return this;
    }

    public ItemStackBuilder customModelId(int customModelId) {
        this.customModelId = customModelId;
        return this;
    }

    public ItemStackBuilder displayName(String displayName) {
        this.displayName = Text.colored(displayName);
        return this;
    }

    public ItemStackBuilder itemFlags(ItemFlag... flags) {
        for (ItemFlag flag : flags) {
            itemFlags.add(flag);
        }
        return this;
    }


    public ItemStack build() {
        if (material == Material.AIR) {
            throw new IllegalArgumentException("Material cannot be AIR");
        }
        var item = new ItemStack(material);
        if (customModelId != 0 || displayName != null || !lore.isEmpty() || unbreakable || !itemFlags.isEmpty()) {
            var meta = item.getItemMeta();
            meta.setCustomModelData(customModelId);
            meta.setLore(lore);
            meta.setDisplayName(displayName);
            meta.setUnbreakable(unbreakable);
            meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
            item.setItemMeta(meta);
        }
        return item;
    }
}
