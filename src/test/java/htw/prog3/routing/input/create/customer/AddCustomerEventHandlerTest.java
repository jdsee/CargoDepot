package htw.prog3.routing.input.create.customer;

import htw.prog3.routing.input.create.customer.AddCustomerEvent;
import htw.prog3.routing.input.create.customer.AddCustomerEventHandler;
import htw.prog3.routing.input.create.customer.AddCustomerEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

class AddCustomerEventHandlerTest {
    @Mock
    AddCustomerEventListener mockListener;
    @Mock
    AddCustomerEvent mockEvent;
    @Mock
    AddCustomerEventListener mockListener2;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void addListener() {
        AddCustomerEventHandler handler = new AddCustomerEventHandler();

        handler.addListener(mockListener);

        handler.handle(mockEvent);
        verify(mockListener).onAddCustomerEvent(mockEvent);
    }

    @Test
    void removeListener() {
        AddCustomerEventHandler handler = new AddCustomerEventHandler();
        handler.addListener(mockListener);

        handler.removeListener(mockListener);

        handler.handle(mockEvent);
        verifyNoInteractions(mockListener);
    }

    @Test
    void handle_shouldNotifyAllListeners() {
        AddCustomerEventHandler handler = new AddCustomerEventHandler();
        handler.addListener(mockListener);
        handler.addListener(mockListener2);

        handler.handle(mockEvent);

        verify(mockListener).onAddCustomerEvent(mockEvent);
        verify(mockListener2).onAddCustomerEvent(mockEvent);
    }

    @Test
    void handle_shouldDoNothingWhenNoListenerSet() {
        AddCustomerEventHandler handler = new AddCustomerEventHandler();

        Throwable throwable = catchThrowable(() -> handler.handle(mockEvent));

        assertThat(throwable).isNull();
    }
}