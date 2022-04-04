package io.ib67.astralflow.machines;

import io.ib67.astralflow.manager.IMachineManager;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@Builder
@Setter
@Getter
public final class MachineProperty {
    private final IMachineManager manager;
    private final UUID uuid;
    private Location location;
    private IState state;
}
