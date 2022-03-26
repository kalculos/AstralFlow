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

package io.ib67.astralflow.machines;

import io.ib67.astralflow.AstralFlow;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static io.ib67.astralflow.machines.IState.EMPTY_STATE;

public abstract class AbstractMachine implements IMachine {
    private final UUID id;

    // this reference keeps same with the machineStorage one.
    private Location location;
    @Setter(AccessLevel.PROTECTED)
    @Getter
    private IState state = EMPTY_STATE;

    /**
     * DO NOT COPY LOCATION
     *
     * @param id
     * @param location
     */
    protected AbstractMachine(UUID id, Location location) {
        this.id = id;
        this.location = location;
    }

    @Override
    public boolean canTick() {
        return location.isWorldLoaded() && Objects.requireNonNull(location.getWorld()).isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    protected void setLocation(Location location) {
        var previousLoc = this.location.clone();
        this.location = location;
        AstralFlow.getInstance().getMachineManager().updateMachineLocation(previousLoc, this); // todo: decoupling this
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public static class SimpleMachineState<V> implements IState {
        private final Map<String, V> values = new HashMap<>();

        public V get(String s) {
            return values.get(s);
        }

        public V getOrDefault(String s, V or) {
            return values.getOrDefault(s, or);
        }

        public V computeIfAbsent(String s, Function<? super String, ? extends V> l) {
            return values.computeIfAbsent(s, l);
        }

        public V computeIfPresent(String s, BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
            return values.computeIfPresent(s, remappingFunction);
        }

        public V put(String s, V v) {
            return values.put(s, v);
        }

        public V remove(String s) {
            return values.remove(s);
        }
    }
}
