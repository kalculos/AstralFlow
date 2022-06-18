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

import lombok.Builder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * MachineContext explains why the machine is created and what are involved in.
 *
 * @param reason        Why is that created?
 * @param owningPlayer  who created this
 * @param owningMachine who created this
 * @param parameter     additional parameter which given by the owning player/holder
 * @param <T>
 */
public record MachineContext<T>(
        @NotNull
        Reason reason,
        @Nullable
        Player owningPlayer,
        @Nullable
        IMachine owningMachine,
        @Nullable
        T parameter
) {
    public static final MachineContext<?> UNKNOWN_CONTEXT = new MachineContext<>(Reason.UNKNOWN, null, null, null);

    @Builder
    @SuppressWarnings("all")
    public MachineContext {
        if (reason == null) reason = Reason.UNKNOWN;
    }

    public boolean hasPlayer() {
        return owningPlayer == null;
    }

    public boolean hasMachine() {
        return owningMachine == null;
    }

    public boolean hasParameter() {
        return parameter == null;
    }

    public enum Reason {
        /**
         * Created by other machines. Probably a multi block structure
         */
        MACHINE,
        /**
         * Created by players' placement.
         */
        PLAYER,
        /**
         * Unknown Reason. Probably plugin operations without defined reason.
         */
        UNKNOWN
    }
}
