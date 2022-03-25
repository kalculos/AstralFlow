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

package io.ib67.astralflow.texture;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Texture {
    private final String namespace;
    private final String textureId;
    private final Path pathToResource;
    private final TextureType type;

    ITextureRegistry textureRegistry;

    public static Texture of(String namespace, String id, Path pathToResource, TextureType type) {
        return new Texture(namespace, id, pathToResource, type);
    }

    public static Texture of(String namespaceAndId, Path pathToResource, TextureType type) {
        var a = namespaceAndId.split(":");
        if (a.length != 2) {
            throw new IllegalArgumentException("Invalid namespace and id. " + namespaceAndId);
        }
        return of(a[0], a[1], pathToResource, type);
    }

    public static Texture ofItem(String namespaceAndId, Path pathToResource) {
        return of(namespaceAndId, pathToResource, TextureType.ITEM);
    }

    public static Texture ofBlock(String namespaceAndId, Path pathToResource) {
        return of(namespaceAndId, pathToResource, TextureType.BLOCK);
    }

    public static Texture ofEntity(String namespaceAndId, Path pathToResource) {
        return of(namespaceAndId, pathToResource, TextureType.ENTITY);
    }

    public int getModelId() {
        if (textureRegistry == null) {
            throw new IllegalStateException("This texture has not been registered with a texture registry.");
        }
        return textureRegistry.fetchModelId(this);
    }
}
