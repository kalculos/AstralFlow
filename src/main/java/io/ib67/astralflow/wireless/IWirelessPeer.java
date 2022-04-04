package io.ib67.astralflow.wireless;

import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A minimal unit to build a wireless network. WirelessPeer is location related.
 * And a common usage is to negotiate and exchange Flows between machines.
 *
 * @param <T> transferring data
 */
@ApiStatus.AvailableSince("0.1.0")
public interface IWirelessPeer<T> {
    /**
     * Establish connection with this peer
     *
     * @param peer
     */
    void negotiate(IWirelessPeer<T> peer);

    /**
     * Receives a message from other peers. This method should be called by other peers.
     *
     * @param message
     */
    void receiveMessage(T message);

    /**
     * The location of this peer.
     *
     * @return null if this peer is undetectable.
     */
    @Nullable
    Location getLocation();

    Class<T> getMessageClass();
}
