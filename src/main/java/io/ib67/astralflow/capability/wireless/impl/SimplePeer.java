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
import io.ib67.astralflow.capability.wireless.IWirelessPeer;
import io.ib67.astralflow.capability.wireless.registry.IWirelessRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

@ApiStatus.Internal
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SimplePeer<T> implements IWirelessPeer<T> {
    @Getter
    private final IWirelessRegistry registry;
    private final Consumer<IWirelessPeer<T>> negotiator;
    private final Consumer<T> messageReceiver;
    private final Location location;
    private final Class<T> type;

    public static <T> SimplePeerBuilder<T> createBuilder() {
        return new SimplePeerBuilder<>();
    }

    @Override
    public void negotiate(IWirelessPeer<T> peer) {
        negotiator.accept(peer);
    }

    @Override
    public void receiveMessage(T message) {
        messageReceiver.accept(message);
    }

    @Override
    public @Nullable Location getLocation() {
        return location;
    }

    @Override
    public Class<T> getMessageClass() {
        return type;
    }

    public static final class SimplePeerBuilder<T> {
        private IWirelessRegistry registry = AstralFlow.getInstance().getCapabilityService().getWirelessRegistry();
        private Consumer<IWirelessPeer<T>> negotiator = t -> {
        };
        private Consumer<T> messageReceiver = t -> {
        };
        private Location location;
        private Class<T> type;

        public SimplePeerBuilder<T> registry(IWirelessRegistry registry) {
            this.registry = registry;
            return this;
        }

        public SimplePeerBuilder<T> negotiator(Consumer<IWirelessPeer<T>> negotiator) {
            this.negotiator = negotiator;
            return this;
        }

        public SimplePeerBuilder<T> messageReceiver(Consumer<T> messageReceiver) {
            this.messageReceiver = messageReceiver;
            return this;
        }

        public SimplePeerBuilder<T> location(Location location) {
            this.location = location;
            return this;
        }

        public SimplePeerBuilder<T> type(Class<T> type) {
            this.type = type;
            return this;
        }

        public SimplePeer<T> build() {
            requireNonNull(registry, "registry");
            requireNonNull(negotiator, "negotiator");
            requireNonNull(messageReceiver, "messageReceiver");
            var peer = new SimplePeer<>(registry, negotiator, messageReceiver, location, type);
            registry.registerPeer(peer);
            return peer;
        }
    }
}
