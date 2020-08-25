package htw.prog3.sm.core;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.sm.core.CargoType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static htw.prog3.sm.core.CargoType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CargoTypeTest {
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void fromInt_shouldReturnBaseTypeForZero() {
        CargoType type = CargoType.from(0);

        assertThat(type).isEqualTo(CARGO_BASE_TYPE);
    }


    @Test
    void fromInt_shouldMatchAssociatedIntValue() {
        CargoType type = CargoType.from(0);
        int intVal = type.intValue();

        CargoType actual = from(intVal);

        assertThat(actual).isEqualTo(type);
    }

    @Test
    void parseCargoType_shouldParseUnitisedCargo() {
        CargoType actual = CargoType.parseCargoType("UnitisedCargo");

        assertThat(actual).isEqualTo(UNITISED_CARGO);
    }

    @Test
    void parseCargoType_shouldParseLiquidBulkCargo() {
        CargoType actual = CargoType.parseCargoType("LiquidBulkCargo");

        assertThat(actual).isEqualTo(LIQUID_BULK_CARGO);
    }

    @Test
    void parseCargoType_shouldParseMixedCargoLiquidBulkAndUnitised() {
        CargoType actual = CargoType.parseCargoType("MixedCargoLiquidBulkAndUnitised");

        assertThat(actual).isEqualTo(MIXED_CARGO_LIQUID_BULK_AND_UNITISED);
    }

    @Test
    void parseCargoType_shouldThrowIllegalArgumentExceptionForNonValidInput() {
        Throwable t = catchThrowable(() -> parseCargoType("NON VALID"));

        assertThat(t).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(InputFailureMessages.UNKNOWN_CARGO_TYPE);
    }

    @Test
    void validValues_shouldReturnAllValuesExceptBaseType() {
        List<CargoType> actual = validValues();

        assertThat(actual).containsExactly(UNITISED_CARGO, LIQUID_BULK_CARGO, MIXED_CARGO_LIQUID_BULK_AND_UNITISED);
    }

    @Test
    void intValue() {
        CargoType type = CargoType.from(2);

        int actual = type.intValue();

        assertThat(CargoType.from(actual)).isEqualTo(type);
    }

    @Test
    void testToString() {
        String actual = LIQUID_BULK_CARGO.toString();

        assertThat(actual).isEqualTo("Liquid Bulk Cargo");
    }
}