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

package io.ib67.astralflow.storage.impl;

import io.ib67.astralflow.storage.KeyedStorage;
import lombok.Builder;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
public class FileBasedKeyedStorage<V> implements KeyedStorage<String, V> {
    private final Path storageDir;
    private final Function<V, byte[]> valueOutMapper;
    private final Function<byte[], V> valueInMapper;

    @Override
    public boolean has(String uuid) {
        return storageDir.resolve(uuid).toFile().exists();
    }

    @Override
    @SneakyThrows
    public V get(String uuid) {
        var pathToFile = storageDir.resolve(uuid);
        if (!pathToFile.toFile().exists()) return null;
        return valueInMapper.apply(Files.readAllBytes(pathToFile));
    }

    @Override
    public Collection<? extends String> getKeys() {
        var a = storageDir.toFile().listFiles();
        if (a != null) return Stream.of(a)
                .map(File::getName)
                .collect(Collectors.toList());
        return Collections.emptyList();
    }

    @Override
    @SneakyThrows
    public void save(String uuid, V state) {
        Files.write(storageDir.resolve(uuid), valueOutMapper.apply(state));
    }

    @Override
    @SneakyThrows
    public void remove(String uuid) {
        Files.deleteIfExists(storageDir.resolve(uuid));
    }

    @Override
    public void flush() {
        // no-op
    }
}
