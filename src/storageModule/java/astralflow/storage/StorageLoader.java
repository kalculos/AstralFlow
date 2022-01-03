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

package astralflow.storage;

import astralflow.storage.listeners.BlockListener;
import astralflow.storage.machines.HelloMachine;
import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.factories.StatelessMachineFactory;
import io.ib67.util.bukkit.Log;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class StorageLoader {
    {
        Log.info("Loading &aStorage Module");
        AstralFlow.getInstance().getFactories().register(HelloMachine.class, new StatelessMachineFactory<>((location, uuid) -> {
            if (uuid == null) { // create a new machine.
                return new HelloMachine(UUID.randomUUID(), location);
            } else {
                return new HelloMachine(uuid, location);
            }
        }));
        Bukkit.getPluginManager().registerEvents(new BlockListener(), (Plugin) AstralFlow.getInstance());
    }
}
