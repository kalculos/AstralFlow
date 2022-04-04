package io.ib67.astralflow.wireless;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * A discoverer for wireless peers.
 *
 * @param <T> WirelessPeers' Type
 */
public interface IWirelessDiscover<T extends IWirelessPeer<?>> {
    <E> Collection<? extends IWirelessPeer<E>> findPeersWithType(Class<? extends E> type);

    Collection<? extends IWirelessPeer<T>> findPeers();

    <E> void collectPeers(Class<? extends E> typeOfPeer, Consumer<E> acceptor);
}
