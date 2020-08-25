package htw.prog3.simulation;

import htw.prog3.simulation.SimulationSelector;
import htw.prog3.simulation.SmSimulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class SimulationSelectorTest {
    @Mock
    SmSimulator mockSimulator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void get_shouldAlwaysReturnSameSimulator() {
        SimulationSelector selector = new SimulationSelector();
        selector.addSimulator(mockSimulator);

        SmSimulator simulator = selector.get(0);
        SmSimulator other = selector.get(0);

        assertThat(simulator).isNotNull().isSameAs(other);
    }

    @Test
    void getRandomSimulator_shouldReturnAnyNonNullInstance() {
        SimulationSelector selector = new SimulationSelector();
        selector.addSimulator(mockSimulator);

        SmSimulator simulator = selector.getRandomSimulator();

        assertThat(simulator).isNotNull();
    }

    @Test
    void getRandomSimulator_shouldReturnNullWhenNoSimulatorSet() {
        SimulationSelector selector = new SimulationSelector();

        SmSimulator simulator = selector.getRandomSimulator();

        assertThat(simulator).isNull();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Disabled // Not testable: equals() can not be verified
    @Test
    void getRandomStorage_shouldCheckIfPassedSimulatorIsEqualToSelection() {
        SimulationSelector selector = new SimulationSelector();

        selector.getRandomSimulator(mockSimulator);

        verify(mockSimulator).equals(any(SmSimulator.class));
    }
}