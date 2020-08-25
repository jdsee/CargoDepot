package htw.prog3.simulation;

import htw.prog3.simulation.AddCargoRunner;
import htw.prog3.simulation.SimulationSelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

class AddCargoRunnerTest {
    @Mock
    SimulationSelector mockSelector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void constructor_shouldReturnNonNullInstance() {
        AddCargoRunner runner = new AddCargoRunner(mockSelector);

        assertThat(runner).isNotNull();
    }
}