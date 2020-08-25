package htw.prog3.sm.core;

import htw.prog3.sm.core.CargoFactory;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.FailureMessages;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.LiquidBulkCargo;
import htw.prog3.storageContract.cargo.MixedCargoLiquidBulkAndUnitised;
import htw.prog3.storageContract.cargo.UnitisedCargo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;

import static htw.prog3.sm.core.CargoType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CargoFactoryTest {

    @Test
    void create_shouldHandleUnitisedCargo() {
        Cargo cargo = CargoFactory.create(UNITISED_CARGO, new CustomerImpl("dummy"),
                BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true, false);

        assertThat(cargo).isInstanceOf(UnitisedCargo.class);
    }

    @Test
    void create_shouldHandleLiquidBulkCargo() {
        Cargo cargo = CargoFactory.create(LIQUID_BULK_CARGO, new CustomerImpl("dummy"),
                BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true, false);

        assertThat(cargo).isInstanceOf(LiquidBulkCargo.class);
    }

    @Test
    void create_shouldHandleMixedCargoLiquidBulkAndUnitised() {
        Cargo cargo = CargoFactory.create(MIXED_CARGO_LIQUID_BULK_AND_UNITISED, new CustomerImpl("dummy"),
                BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true, false);

        assertThat(cargo).isInstanceOf(MixedCargoLiquidBulkAndUnitised.class);
    }

    @Test
    void create_shouldThrowIllegalArgumentExceptionForNonInstantiableType() {
        Throwable t = catchThrowable(() -> CargoFactory.create(CARGO_BASE_TYPE, new CustomerImpl("dummy"),
                BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true, false));

        assertThat(t).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(FailureMessages.UNKNOWN_CARGO_TYPE);
    }
}