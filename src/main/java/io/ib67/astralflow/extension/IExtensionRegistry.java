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

package io.ib67.astralflow.extension;

import io.ib67.astralflow.api.external.AstralExtension;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * Where you can register your module at startup time.
 */
@ApiStatus.AvailableSince("0.1.0")
public interface IExtensionRegistry {
    /**
     * Get registered extensions.
     *
     * @return Collection of registered extensions.
     */
    @NotNull
    Collection<? extends AstralExtension> getExtensions();

    /**
     * Get an extension by its name.
     *
     * @param name Name of the extension.
     * @return the extension.
     */
    @NotNull
    Optional<AstralExtension> getExtensionByName(String name);

    /**
     * Register an extension.
     * Only available in init phase. Throws IllegalStateException otherwise.
     *
     * @param extension Extension to register.
     */
    void registerExtension(AstralExtension extension);
}
