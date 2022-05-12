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

package io.ib67.astralflow.capability.wireless.impl;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.AstralHelper;
import io.ib67.astralflow.capability.wireless.IWirelessPeer;
import io.ib67.astralflow.capability.wireless.registry.IWirelessRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.inlambda.kiwi.WeakHashSet;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

public final class SimpleWirelessRegistry implements IWirelessRegistry {

    private final Map<World, Set<? extends IWirelessPeer<?>>> peersMap = new WeakHashMap<>(8); // 8 worlds is fully enough for most servers.

    public SimpleWirelessRegistry() {

    }

    private static <T> Collection<? extends IWirelessPeer<T>> find(Location location, double range, Class<T> type, Collection<? extends IWirelessPeer<T>> originalList) {
        requireNonNull(location, "location");
        requireNonNull(originalList, "originalList");
        if(range < 0){
            throw new IllegalArgumentException("Range must be positive");
        }
        var stream = originalList.stream();
        if (type != null) {
            stream = stream.filter(e -> e.getMessageClass() == type);
        }
        var dst = Math.pow(range, 2);
        var loc = location.clone();
        return stream
                .filter(e -> Objects.nonNull(e.getLocation()))
                .filter(e -> e.getLocation().distanceSquared(loc) <= dst)
                .map(e -> (IWirelessPeer<T>) e)
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> IWirelessRegistry registerPeer(IWirelessPeer<T> peer) {
        requireNonNull(peer, "peer");
        AstralHelper.ensureMainThread("registerPeer");
        var location = peer.getLocation();
        var world = location == null ? null : location.getWorld();
        ((Set<IWirelessPeer<T>>) peersMap.computeIfAbsent(world, w -> new WeakHashSet<>())).add(peer);
        return this;
    }

    @Override
    public <T> IWirelessRegistry unregisterPeer(IWirelessPeer<T> peer) {
        requireNonNull(peer, "peer");
        AstralHelper.ensureMainThread("unregisterPeer");
        var location = peer.getLocation();
        var world = location == null ? null : location.getWorld();
        if (!peersMap.containsKey(world)) {
            throw new IllegalArgumentException("Peer is not registered");
        }
        peersMap.get(world).remove(peer);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<? extends IWirelessPeer<T>> findPeers(Location location, double range, Class<T> type) {
        AstralHelper.ensureMainThread("findPeers should be called in main thread.");
        requireNonNull(location, "location");
        if (range <= 0) {
            throw new IllegalArgumentException("Range must be positive");
        }
        var world = location.getWorld();
        var peers = (Collection<? extends IWirelessPeer<T>>) peersMap.getOrDefault(world, Collections.emptySet());
        return find(location, range, type, peers);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CompletableFuture<Collection<? extends IWirelessPeer<T>>> findPeersAsync(Location location, double range) {
        AstralHelper.ensureMainThread("findPeersAsync should be called in main thread.");
        requireNonNull(location, "location");
        if (range <= 0) {
            throw new IllegalArgumentException("Range must be positive");
        }
        // make a snapshot.
        var set = peersMap.get(location.getWorld());
        if (set == null) {
            // do nothing.
            return CompletableFuture.completedFuture(Collections.emptySet());
        }

        var snapshot = (Collection<? extends IWirelessPeer<T>>) List.copyOf(set);
        var future = new CompletableFuture<Collection<? extends IWirelessPeer<T>>>();
        Bukkit.getScheduler().runTaskAsynchronously(AstralFlow.getInstance().asPlugin(), () -> {
            var result = find(location, range, null, snapshot);
            future.complete(result);
        });
        return future;
    }
}
