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

package io.ib67.astralflow.api.item.dummy.fetch;

import io.ib67.astralflow.api.item.dummy.FetchMethod;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.hook.event.machine.MachineBreakEvent;
import io.ib67.astralflow.machines.IMachine;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * You should really consider {@link io.ib67.astralflow.api.item.machine.MachineItem} instead.
 * A way to fetch item from breaking a machine.
 */
@ApiStatus.AvailableSince("0.1.0")
@Deprecated
public final class BreakMachine implements FetchMethod {
    private final Class<? extends IMachine> machineClass;
    private final boolean polymorph;

    private Supplier<ItemStack> producer;

    public BreakMachine(Class<? extends IMachine> machineClass, boolean polymorph) {
        requireNonNull(machineClass, "machineClass");
        this.machineClass = machineClass;
        this.polymorph = polymorph;
    }

    @Override
    public void init(Supplier<ItemStack> producer) {
        this.producer = producer;
        HookType.MACHINE_BREAK.register(this::onMachineBreak);
    }

    private void onMachineBreak(MachineBreakEvent event) {
        if (polymorph) {
            if (machineClass.isAssignableFrom(event.getMachine().getClass())) {
                event.getBrokenBlock().getLocation().getWorld().dropItemNaturally(event.getBrokenBlock().getLocation(), producer.get());
            }
        } else {
            if (machineClass.equals(event.getMachine().getClass())) {
                event.getBrokenBlock().getLocation().getWorld().dropItemNaturally(event.getBrokenBlock().getLocation(), producer.get());
            }
        }
    }
}
