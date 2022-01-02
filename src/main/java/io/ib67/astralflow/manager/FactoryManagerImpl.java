package io.ib67.astralflow.manager;

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.IMachineData;
import io.ib67.astralflow.machines.IMachineFactory;
import net.jodah.typetools.TypeResolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FactoryManagerImpl implements IFactoryManager {
    private final Map<Class<? extends IMachine>, IMachineFactory<?, ?>> factories = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IMachine, S extends IMachineData> IMachineFactory<T, S> getMachineFactory(Class<T> type) {
        return (IMachineFactory<T, S>) factories.get(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends IMachineFactory<?, ?>> getMachineFactories() {
        return factories.values();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IMachine, S extends IMachineData> boolean register(IMachineFactory<T, S> factory) {
        var claz = TypeResolver.resolveRawArguments(IMachineFactory.class, factory.getClass())[0];
        if (factories.containsKey(claz)) {
            return false;
        }
        factories.put((Class<? extends IMachine>) claz, factory);
        return true;
    }

    @Override
    public <T extends IMachine, S extends IMachineData> boolean unregister(Class<T> type) {
        return factories.containsKey(type) && factories.remove(type) != null;
    }
}
