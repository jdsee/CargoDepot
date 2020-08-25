package htw.prog3.log;

import htw.prog3.log.InteractionLogger;
import htw.prog3.log.ProcessLogger;
import htw.prog3.log.SmLogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class SmLogManagerTest {
    @Mock
    InteractionLogger mockInteractionLogger;
    @Mock
    ProcessLogger mockProcessLogger;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void from_shouldCreateNewInstance() {
        SmLogManager logManager = SmLogManager.from(mockInteractionLogger, mockProcessLogger);
        SmLogManager other = SmLogManager.from(mockInteractionLogger, mockProcessLogger);

        assertThat(logManager).isNotNull().isNotSameAs(other);
    }

    @Test
    void close() throws IOException {
        SmLogManager logManager = SmLogManager.from(mockInteractionLogger, mockProcessLogger);

        logManager.close();

        verify(mockInteractionLogger).close();
        verify(mockProcessLogger).close();
    }
}