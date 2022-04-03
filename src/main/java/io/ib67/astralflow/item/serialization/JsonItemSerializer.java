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

package io.ib67.astralflow.item.serialization;

import com.google.gson.Gson;
import io.ib67.astralflow.internal.ItemKeySerializer;
import io.ib67.astralflow.internal.StateSerializer;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.machines.IState;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.util.Util;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JsonItemSerializer implements ItemSerializer {
    private final Gson serializer;

    public JsonItemSerializer(IFactoryManager factoryManager) {
        var serializer = Util.BukkitAPI.gsonBuilderForBukkit()
                .registerTypeHierarchyAdapter(ItemKey.class, new ItemKeySerializer())
                .create();
        this.serializer = Util.BukkitAPI.gsonBuilderForBukkit()
                .registerTypeHierarchyAdapter(IState.class, new StateSerializer(serializer))
                .create();
    }

    @Override
    public ItemState deserialize(byte[] bytes) {
        return serializer.fromJson(new String(bytes), ItemState.class);
    }

    @Override
    public byte[] serialize(ItemState state) {
        return serializer.toJson(state).getBytes(UTF_8);
    }
}
