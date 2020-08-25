package htw.prog3.simulation;

import htw.prog3.simulation.RearrangeCargoRunner;
import htw.prog3.simulation.SmSimulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RearrangeCargoRunnerTest {
    @Mock
    SmSimulator mockSimulator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void constructor_shouldReturnNonNullInstance() {
        RearrangeCargoRunner runner = new RearrangeCargoRunner(mockSimulator);

        assertThat(runner).isNotNull();
    }
}