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

package io.ib67.astralflow.config;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.storage.IMachineStorage;
import io.ib67.astralflow.storage.ItemStateStorage;
import io.ib67.astralflow.storage.impl.FileItemStorage;
import io.ib67.astralflow.storage.impl.FileMachineStorage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AstralFlowConfiguration {
    public static final int CONFIG_CURRENT_VERSION = 1;

    private final int version = CONFIG_CURRENT_VERSION;
    private final Language locale;
    private final IMachineStorage storage;
    private final ItemStateStorage itemStorage;

    public static AstralFlowConfiguration defaultConfiguration(Path itemStorageDir, Path machineStorageDir) {
        return new AstralFlowConfiguration(
                new Language(),
                new FileMachineStorage(machineStorageDir, AstralFlow.getInstance().getFactories()),
                new FileItemStorage(itemStorageDir, AstralFlow.getInstance().getFactories())
        );
    }
}
