package htw.prog3.routing.input.create.cargo;

import htw.prog3.sm.core.CargoType;
import htw.prog3.storageContract.cargo.Hazard;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.EventObject;
import java.util.Set;

public class AddCargoEvent extends EventObject {
    private final CargoType type;
    private final String name;
    private final BigDecimal value;
    private final Set<Hazard> hazards;
    private final boolean pressurized;
    private final boolean fragile;
    private final Duration durationOfStorage;

    public AddCargoEvent(CargoType type,
                         String name,
                         BigDecimal value,
                         Duration durationOfStorage,
                         Set<Hazard> hazards,
                         boolean pressurized,
                         boolean fragile,
                         Object source) {
        super(source);
        this.type = type;
        this.name = name;
        this.value = value;
        this.durationOfStorage = durationOfStorage;
        this.hazards = hazards;
        this.pressurized = pressurized;
        this.fragile = fragile;
    }

    public CargoType getCargoType() {
        return type;
    }

    public String getOwnerName() {
        return name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Duration getDurationOfStorage() {
        return durationOfStorage;
    }

    public Set<Hazard> getHazards() {
        return hazards;
    }

    public boolean isPressurized() {
        return pressurized;
    }

    public boolean isFragile() {
        return fragile;
    }
}
