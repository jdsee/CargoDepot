package htw.prog3.storageContract.cargo;

import htw.prog3.sm.core.CargoType;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.UnitisedCargoImpl;
import htw.prog3.storageContract.cargo.Cargo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class UnitisedCargoTest {
    @Test
    void getCargoType_shouldReturnProperTypeForImplementation() {
        Cargo cargo = new UnitisedCargoImpl(new CustomerImpl("owner"), BigDecimal.TEN,
                Duration.ofDays(1), new HashSet<>(), true);

        CargoType actualType = cargo.getCargoType();

        assertThat(actualType).isEqualTo(CargoType.UNITISED_CARGO);
    }
}