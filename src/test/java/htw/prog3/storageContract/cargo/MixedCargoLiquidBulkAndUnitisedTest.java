package htw.prog3.storageContract.cargo;

import htw.prog3.sm.core.CargoType;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.MixedCargoLiquidBulkAndUnitisedImpl;
import htw.prog3.storageContract.cargo.Cargo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class MixedCargoLiquidBulkAndUnitisedTest {
    @Test
    void getCargoType_shouldReturnProperTypeForImplementation() {
        Cargo cargo = new MixedCargoLiquidBulkAndUnitisedImpl(new CustomerImpl("owner"), BigDecimal.TEN,
                Duration.ofDays(1), new HashSet<>(), true, false);

        CargoType actualType = cargo.getCargoType();

        assertThat(actualType).isEqualTo(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED);
    }
}