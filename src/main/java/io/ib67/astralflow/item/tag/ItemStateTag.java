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

package io.ib67.astralflow.item.tag;

import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.item.serialization.ItemSerializer;
import io.ib67.astralflow.item.serialization.ItemStorageType;
import io.ib67.astralflow.manager.IFactoryManager;
import io.netty.buffer.Unpooled;
import lombok.RequiredArgsConstructor;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

@RequiredArgsConstructor
public class ItemStateTag implements PersistentDataType<byte[], ItemState> {
    private final ItemStorageType defaultStorageType;
    private final IFactoryManager factoryManager;
    private final Map<ItemStorageType, ItemSerializer> serializers = new EnumMap<>(ItemStorageType.class);

    @NotNull
    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @NotNull
    @Override
    public Class<ItemState> getComplexType() {
        return ItemState.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull ItemState complex, @NotNull PersistentDataAdapterContext context) {
        var data = serializers.computeIfAbsent(defaultStorageType, type -> defaultStorageType.apply(factoryManager)).serialize(complex);
        var len = data.length;
        var storageIndex = defaultStorageType.getTypeIndex();
        var buf = Unpooled.buffer(4 + 4 + len);
        buf.writeInt(storageIndex);
        buf.writeInt(len);
        buf.writeBytes(data);
        var result = buf.array();
        buf.release();
        return result;
    }

    @NotNull
    @Override
    public ItemState fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        var buf = Unpooled.wrappedBuffer(primitive);
        var storageIndex = buf.readInt();
        var storageType = ItemStorageType.getType(storageIndex);
        var len = buf.readInt();
        var data = new byte[len];
        buf.readBytes(len);
        buf.release();
        return serializers.computeIfAbsent(storageType, type -> type.apply(factoryManager)).deserialize(data);
    }
}
