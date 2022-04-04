package io.ib67.astralflow.machines;

import io.ib67.astralflow.manager.IMachineManager;
import lombok.Builder;
import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public record MachineProperty(
        Location location,
        IMachineManager manager,
        UUID uuid,
        IState initialState
) {
    @Builder
    public MachineProperty {
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(manager, "manager");
        Objects.requireNonNull(uuid, "uuid");
    }
}
