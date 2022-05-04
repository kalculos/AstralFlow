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

package io.ib67.astralflow.capability.wireless;

import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A minimal unit to build a wireless network. WirelessPeer is location related.<br />
 * And a common usage is to negotiate and exchange Flows between machines.<br />
 * <p>
 * *note* If you're providing a location then you make sure that your location's world isn't change, or you may need to unregister-register again to update the location.
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
