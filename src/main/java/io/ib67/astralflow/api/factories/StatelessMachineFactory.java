package io.ib67.astralflow.api.factories;

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.IMachineData;
import io.ib67.astralflow.machines.IMachineFactory;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.UUID;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class StatelessMachineFactory<T extends IMachine> implements IMachineFactory<T, IMachineData> {
    private final BiFunction<Location, UUID, T> factory;

    @Override
    public T createMachine(Location location) {
        return factory.apply(location, null);
    }

    @Override
    public T createMachine(Location location, UUID uuid) {
        return factory.apply(location, uuid);
    }

    @Override
    public T createMachine(T anotherMachine) {
        return factory.apply(anotherMachine.getLocation(), null);
    }

    @Override
    public T createMachine(Location location, IMachineData initialState) {
        return createMachine(location);
    }

    @Override
    public T createMachine(Location location, UUID uuid, IMachineData initialState) {
        return createMachine(location, uuid);
    }
}
