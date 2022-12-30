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

package io.ib67.astralflow.internal.storage;

import io.ib67.astralflow.internal.IChunkTracker;
import io.ib67.kiwi.WeakHashSet;
import org.bukkit.Chunk;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public final class SimpleChunkTracker implements IChunkTracker {
    private final Set<Chunk> markedChunks;

    public SimpleChunkTracker(int capacity, boolean allowResizing) {
        markedChunks = new WeakHashSet(capacity);
        if (!allowResizing) ((WeakHashSet<Chunk>) markedChunks).disableResizing();
    }

    @Override
    public boolean isChunkMarked(Chunk chunk) {
        return markedChunks.contains(chunk);
    }

    @Override
    public void markChunk(Chunk chunk) {
        markedChunks.add(chunk);
    }

    @Override
    public void unmarkChunk(Chunk chunk) {
        markedChunks.remove(chunk);
    }

    @Override
    public Collection<? extends Chunk> getMarkedChunks() {
        return Collections.unmodifiableCollection(markedChunks);
    }
}
