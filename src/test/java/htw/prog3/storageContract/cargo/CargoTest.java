package htw.prog3.storageContract.cargo;

import htw.prog3.sm.core.CargoType;
import htw.prog3.storageContract.cargo.Cargo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

class CargoTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    Cargo mockCargo;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void getCargoType_shouldReturnBaseTypeIfNotOverwritten() {
        CargoType actualType = mockCargo.getCargoType();

        assertThat(actualType).isEqualTo(CargoType.CARGO_BASE_TYPE);
    }
}