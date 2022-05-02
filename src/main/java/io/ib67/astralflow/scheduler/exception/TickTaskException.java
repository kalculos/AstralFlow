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

package io.ib67.astralflow.scheduler.exception;

import io.ib67.astralflow.Tickable;
import lombok.Getter;

public final class TickTaskException extends Exception {
    @Getter
    private final Tickable tickable;

    public TickTaskException(String message, Tickable tickable) {
        super(message);
        this.tickable = tickable;
    }

    public TickTaskException(String message, Throwable cause, Tickable tickable) {
        super(message, cause);
        this.tickable = tickable;
    }

    public TickTaskException(Throwable cause, Tickable tickable) {
        super(cause);
        this.tickable = tickable;
    }

    protected TickTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Tickable tickable) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.tickable = tickable;
    }
}
