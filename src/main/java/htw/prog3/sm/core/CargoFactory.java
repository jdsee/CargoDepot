package htw.prog3.sm.core;

import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

public final class CargoFactory {
    public static Cargo create(CargoType type,
                               Customer owner,
                               BigDecimal value,
                               Duration durationOfStorage,
                               Set<Hazard> hazards,
                               boolean pressurized,
                               boolean fragile) {
        Date now = Calendar.getInstance().getTime();
        Objects.requireNonNull(type, FailureMessages.notNull(CargoType.class));
        switch (type) {
            case UNITISED_CARGO:
                return new UnitisedCargoImpl(owner, value, durationOfStorage, hazards, fragile, now);
            case LIQUID_BULK_CARGO:
                return new LiquidBulkCargoImpl(owner, value, durationOfStorage, hazards, pressurized, now);
            case MIXED_CARGO_LIQUID_BULK_AND_UNITISED:
                return new MixedCargoLiquidBulkAndUnitisedImpl(owner, value, durationOfStorage, hazards, pressurized,
                        fragile, now);
            default:
                throw new IllegalArgumentException(FailureMessages.UNKNOWN_CARGO_TYPE);
        }
    }
}