package io.ib67.astralflow.wireless;

import io.ib67.astralflow.wireless.registry.IWirelessRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SimplePeer<T> implements IWirelessPeer<T> {
    @Getter
    private final IWirelessRegistry registry;
    private final Consumer<IWirelessPeer<T>> negotiator;
    private final Consumer<T> messageReceiver;
    private final Location location;
    private final Class<T> type;

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
        private IWirelessRegistry registry;
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
