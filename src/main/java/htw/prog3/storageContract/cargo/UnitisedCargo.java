package htw.prog3.storageContract.cargo;

import htw.prog3.sm.core.CargoType;
import htw.prog3.sm.core.UnitisedCargoImpl;
import htw.prog3.storageContract.administration.Customer;
import javafx.beans.property.ReadOnlyBooleanProperty;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Set;

public interface UnitisedCargo extends Cargo {
    CargoType CARGO_TYPE = CargoType.UNITISED_CARGO;

    static UnitisedCargo create(Customer owner, BigDecimal value, Duration durationOfStorage, Set<Hazard> hazards, boolean fragile) {
        return new UnitisedCargoImpl(owner, value, durationOfStorage, hazards, fragile);
    }

    @Override
    default CargoType getCargoType() {
        return CARGO_TYPE;
    }

    boolean isFragile();

    ReadOnlyBooleanProperty fragileProperty();
}
