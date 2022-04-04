package io.ib67.astralflow.wireless.registry;

import io.ib67.astralflow.wireless.IWirelessPeer;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Providing an interface for peers to seek other peers.
 */
@ApiStatus.AvailableSince("0.1.0")
public interface IWirelessRegistry {

    /**
     * Register a peer. Registered Peers will be kept in weak reference for GC to clean.
     *
     * @param peer The peer to register.
     * @param <T>  The type of the peer.
     * @return This registry, for fluent api usage.
     */
    <T> IWirelessRegistry registerPeer(IWirelessPeer<T> peer);

    /**
     * Unregister a peer. This peer will not be discovered via this registry until it is registered again.
     *
     * @param peer The peer to unregister.
     * @param <T>  The type of the peer.
     * @return This registry, for fluent api usage.
     */
    <T> IWirelessRegistry unregisterPeer(IWirelessPeer<T> peer);

    /**
     * Find near-by peers with their type.
     *
     * @param location The location to find nearby peers.
     * @param <T>      The type of the peer.
     * @return The nearby peers.
     */
    <T> Collection<? extends IWirelessPeer<T>> findPeers(Location location, double range, @Nullable Class<T> type);

    /**
     * Find nearby peers with their type in async threads, this may help for large-area discovery.
     * This should be called on the main thread.
     *
     * @param location
     * @param <T>
     * @return
     */
    <T> CompletableFuture<? extends Collection<? extends IWirelessPeer<T>>> findPeersAsync(Location location, double range);

    /**
     * Find nearby peers without checking their type.
     *
     * @param location The location to find nearby peers.
     * @param <T>      The type of the peer.
     * @return The nearby peers.
     */
    default <T> Collection<? extends IWirelessPeer<T>> findPeers(Location location, double range) {
        return findPeers(location, range, null);
    }
}
