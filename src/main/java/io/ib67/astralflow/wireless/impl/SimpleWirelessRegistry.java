package io.ib67.astralflow.wireless.impl;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.AstralHelper;
import io.ib67.astralflow.util.WeakHashSet;
import io.ib67.astralflow.wireless.IWirelessPeer;
import io.ib67.astralflow.wireless.registry.IWirelessRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

public final class SimpleWirelessRegistry implements IWirelessRegistry {

    private final Map<World, Set<? extends IWirelessPeer<?>>> peersMap = new WeakHashMap<>(8); // 8 worlds is fully enough for most servers.

    public SimpleWirelessRegistry() {

    }

    private static <T> Collection<? extends IWirelessPeer<T>> find(Location location, double range, Class<T> type, Collection<? extends IWirelessPeer<T>> originalList) {
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
        requireNonNull(type, "type");
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
