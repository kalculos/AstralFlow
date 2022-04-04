package io.ib67.astralflow.wireless.registry;

import io.ib67.astralflow.wireless.IWirelessPeer;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Providing an interface for peers to seek other peers.
 */
@ApiStatus.AvailableSince("0.1.0")
public interface IWirelessRegistry extends IWirelessDiscoverFactory {

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
    <T> Collection<? extends IWirelessPeer<T>> findPeers(Location location, Predicate<? extends Class<T>> typePredictor);

    /**
     * Find nearby peers with their type in async threads, this may help for large-area discovery.
     *
     * @param location
     * @param typePredictor
     * @param consumer
     * @param <T>
     * @return
     */
    <T> IWirelessRegistry findPeersAsync(Location location, Predicate<? extends Class<T>> typePredictor, Consumer<? extends IWirelessPeer<T>> consumer, Runnable whenDone);

    /**
     * Find nearby peers without checking their type.
     *
     * @param location The location to find nearby peers.
     * @param <T>      The type of the peer.
     * @return The nearby peers.
     */
    default <T> Collection<? extends IWirelessPeer<T>> findPeers(Location location) {
        return findPeers(location, t -> true);
    }
}
