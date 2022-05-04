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

package io.ib67.astralflow.security.mem.impl;

import io.ib67.astralflow.internal.AstralConstants;
import io.ib67.astralflow.security.mem.ILeakTracker;
import io.ib67.astralflow.util.LogCategory;
import io.ib67.util.bukkit.Log;
import org.jetbrains.annotations.ApiStatus;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@ApiStatus.Internal
public final class SimpleLeakTracker implements ILeakTracker {
    private final ReferenceQueue<?> queue = new ReferenceQueue<>();
    private final List<TrackedObject> trackedObjects = new LinkedList<>();

    public SimpleLeakTracker() {

    }

    public void onTick() {
        var iter = trackedObjects.iterator();
        while (iter.hasNext()) {
            var trackedObject = iter.next();
            if (trackedObject.ref.get() != null) {
                trackedObject.counter++;
                if (trackedObject.counter % 20 == 0) {
                    if (AstralConstants.MOCKING) {
                        throw new IllegalStateException("Object " + trackedObject.ref.get().getClass().getName() + "#" + System.identityHashCode(trackedObject.ref.get()) + " is proposed to be garbage collected but it alives for " + trackedObject.counter + " rounds. Is there any memory leak?");
                    } else {
                        Log.warn(LogCategory.LEAK_DETECTOR, "Object " + trackedObject.ref.get().getClass().getName() + "#" + System.identityHashCode(trackedObject.ref.get()) + " is proposed to be garbage collected but it alives for " + trackedObject.counter + " rounds. Is there any memory leak?");
                    }
                }
            } else {
                iter.remove();
            }
        }
        for (Object o; (o = queue.poll()) != null; ) {
            Object finalO = o;
            trackedObjects.removeIf(e -> e.ref.get() == finalO);
        }
    }

    @Override
    public void track(Object obj) {
        Objects.requireNonNull(obj, "obj cannot be null");
        if (trackedObjects.stream().anyMatch(e -> e.ref.get() == obj)) {
            throw new IllegalArgumentException("Object already tracked");
        }
        trackedObjects.add(new TrackedObject(new WeakReference<>(obj), 0));
    }

    @Override
    public void untrack(Object obj) {
        Objects.requireNonNull(obj, "obj cannot be null");
        trackedObjects.removeIf(e -> e.ref.get() == obj);
    }
}
