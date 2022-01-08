/*
 *
 *   AstralFlow - Storage utilities for spigot servers.
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.ib67.astralflow.hook.HookType;
import org.bukkit.inventory.RecipeChoice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OreDictImpl implements IOreDict {
    private final Map<String, RecipeChoice.ExactChoice> choiceMap = new HashMap<>();
    private Multimap<String, Item> items = ArrayListMultimap.create();
    private volatile boolean locked = false;

    {
        HookType.SERVER_STARTUP_COMPLETED.register(this::compile);
    }

    private void compile() {
        locked = true;
        for (String s : items.keySet()) {
            choiceMap.put(s, new RecipeChoice.ExactChoice(items.get(s).stream().map(Item::getPrototype).collect(Collectors.toList())));
        }
        items = null; // remove unnecessary references.
    }

    @Override
    public IOreDict registerItem(Item prototype, String dictKey) {
        if (locked) throw new IllegalStateException("OreDict is locked due to server startup completed.");
        if (items.containsKey(dictKey)) throw new IllegalArgumentException(dictKey + " is already registered.");
        items.put(dictKey, prototype);
        return this;
    }

    @Override
    public RecipeChoice.ExactChoice getChoices(String dictKey) {
        return choiceMap.get(dictKey);
    }
}
