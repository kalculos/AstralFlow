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

package io.ib67.astralflow.machine;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.machines.MachineProperty;
import io.ib67.astralflow.machines.exception.MachineNotPushableException;
import io.ib67.astralflow.manager.IMachineManager;
import io.ib67.astralflow.storage.DummyStatefulMachine;
import io.ib67.astralflow.test.TestUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MachineManagerTest {
    IMachineManager manager;

    @BeforeAll
    public void setup() {
        TestUtil.init();
        manager = AstralFlow.getInstance().getMachineManager();
    }

    @Test
    public void onTest() throws MachineNotPushableException {
        var machineLoc = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        var machine = new DummyStatefulMachine(
                MachineProperty
                        .builder().manager(manager).location(machineLoc).build()
        );
        manager.setupMachine(machine, true);
        Assertions.assertTrue(manager.isMachine(machineLoc.getBlock()));
        Assertions.assertTrue(manager.isRegistered(machine));
        var newLoc = new Location(Bukkit.getWorld("world"), 1, 1, 1);
        manager.updateMachineLocation(machineLoc, newLoc, machine);
        Assertions.assertFalse(manager.isMachine(machineLoc.getBlock()));
        Assertions.assertTrue(manager.isRegistered(machine));
        manager.terminateAndRemoveMachine(machine);
        Assertions.assertFalse(manager.isMachine(newLoc.getBlock()));
        Assertions.assertFalse(manager.isRegistered(machine));
    }
}
