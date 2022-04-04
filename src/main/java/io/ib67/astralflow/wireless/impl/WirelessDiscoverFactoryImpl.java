package io.ib67.astralflow.wireless.impl;

import io.ib67.astralflow.wireless.IWirelessDiscover;
import io.ib67.astralflow.wireless.IWirelessPeer;
import io.ib67.astralflow.wireless.registry.IWirelessDiscoverFactory;
import io.ib67.astralflow.wireless.registry.IWirelessRegistry;
import org.bukkit.Location;

public record WirelessDiscoverFactoryImpl(IWirelessRegistry registry) implements IWirelessDiscoverFactory {

    @Override
    public <T extends IWirelessPeer<?>> IWirelessDiscover<T> create(Location location) {
        return null;
    }
}
