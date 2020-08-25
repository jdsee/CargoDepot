package htw.prog3.log;

import htw.prog3.log.InteractionLogDictionary;
import htw.prog3.log.InteractionLogger;
import htw.prog3.routing.input.create.cargo.AddCargoEvent;
import htw.prog3.routing.input.create.cargo.AddCargoEventHandler;
import htw.prog3.routing.input.create.customer.AddCustomerEvent;
import htw.prog3.routing.input.create.customer.AddCustomerEventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

class InteractionLoggerTest {
    @Mock
    InteractionLogDictionary dictionary;
    @Mock
    AddCargoEvent mockAddCargoEvent;
    @Mock
    PrintWriter mockOut;
    @Mock
    AddCustomerEvent mockAddCustomerEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void setAddCargoEventHandler_shouldEnableLoggingOfAddCargoEvent() {
        doReturn("x").when(dictionary).addItemAttemptMsg(mockAddCargoEvent);
        AddCargoEventHandler handler = new AddCargoEventHandler();
        InteractionLogger logger = new InteractionLogger(mockOut, dictionary);

        logger.registerAddCargoEventListener(handler);

        handler.handle(mockAddCargoEvent);
        verify(mockOut).println("x");
    }

    @Test
    void setAddCustomerEventHandler() {
        doReturn("x").when(dictionary).addCustomerAttemptMsg(mockAddCustomerEvent);
        AddCustomerEventHandler handler = new AddCustomerEventHandler();
        InteractionLogger logger = new InteractionLogger(mockOut, dictionary);

        logger.registerAddCustomerEventListener(handler);

        handler.handle(mockAddCustomerEvent);
        verify(mockOut).println("x");
    }

    @Test
    void close() {
        InteractionLogger logger = new InteractionLogger(mockOut, dictionary);

        logger.close();

        verify(mockOut).close();
    }
}