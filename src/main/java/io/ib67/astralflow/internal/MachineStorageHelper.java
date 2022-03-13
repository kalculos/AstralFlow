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

package io.ib67.astralflow.internal;

import com.google.gson.Gson;
import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.IState;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.storage.MachineSerializer;
import io.ib67.util.Util;

import java.nio.charset.StandardCharsets;

public class MachineStorageHelper implements MachineSerializer {
    public static final MachineStorageHelper HELPER = new MachineStorageHelper(AstralFlow.getInstance().getFactories());
    private final Gson MACHINE_SERIALIZER;

    public MachineStorageHelper(IFactoryManager factories) {
        MACHINE_SERIALIZER = Util.BukkitAPI.gsonBuilderForBukkit()
                .registerTypeHierarchyAdapter(IMachine.class, new JsonMachineSerializer(factories))
                .registerTypeHierarchyAdapter(IState.class, new StateSerializer(Util.BukkitAPI.gsonForBukkit()))
                .create();
    }

    public IMachine fromJson(String json) {
        return fromJson(json, IMachine.class);
    }

    public <T> T fromJson(String json, Class<T> tClass) {
        return MACHINE_SERIALIZER.fromJson(json, tClass);
    }

    public String toJson(Object machine) {
        return MACHINE_SERIALIZER.toJson(machine);
    }

    @Override
    public IMachine fromData(byte[] o) {
        return fromJson(new String(o));
    }

    @Override
    public byte[] toData(IMachine machine) {
        return toJson(machine).getBytes(StandardCharsets.UTF_8);
    }
}
