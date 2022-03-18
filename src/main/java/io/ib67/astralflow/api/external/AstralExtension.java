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

package io.ib67.astralflow.api.external;

import io.ib67.astralflow.AstralFlow;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * A class that represents an extension.
 * <p>
 * Registering your own contents' by subclassing this first. Extensions will be registered automatically by the constructor.
 * Also check the doc.
 */
@ApiStatus.AvailableSince("0.1.0")
public abstract class AstralExtension {
    private final ExtensionInfo info;

    public AstralExtension(@NotNull ExtensionInfo info) {
        Objects.requireNonNull(info, "extension info cannot be null");
        this.info = info;
        // register this extension automatically.
        AstralFlow.getInstance().getExtensionRegistry().registerExtension(this);
    }

    public final ExtensionInfo getInfo() {
        return info;
    }

    /**
     * Called when the extension is in INIT phase. You can register your machines and items here.
     */
    public void init() {

    }

    /**
     * Called when your extension and astralflow itself is going to be disabled. (Generally server shutdown.)
     */
    public void terminate() {

    }
}
