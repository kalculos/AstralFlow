package io.ib67.astralflow.wireless.registry;

import io.ib67.astralflow.wireless.IWirelessDiscover;
import io.ib67.astralflow.wireless.IWirelessPeer;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;

/**
 * A factory to create {@link IWirelessDiscover} instances.
 */
@ApiStatus.AvailableSince("0.1.0")
public interface IWirelessDiscoverFactory {
    <T extends IWirelessPeer<?>> IWirelessDiscover<T> create(Location location);
}
