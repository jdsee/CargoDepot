package htw.prog3.routing.error;

import htw.prog3.routing.error.IllegalInputEvent;
import htw.prog3.routing.error.IllegalInputEventHandler;
import htw.prog3.routing.error.IllegalInputEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

class IllegalInputEventHandlerTest {
    @Mock
    IllegalInputEventListener mockListener;
    @Mock
    IllegalInputEvent mockEvent;
    @Mock
    IllegalInputEventListener mockListener2;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void addListener() {
        IllegalInputEventHandler handler = new IllegalInputEventHandler();

        handler.addListener(mockListener);

        handler.handle(mockEvent);
        verify(mockListener).onIllegalInputEvent(mockEvent);
    }

    @Test
    void removeListener() {
        IllegalInputEventHandler handler = new IllegalInputEventHandler();
        handler.addListener(mockListener);

        handler.removeListener(mockListener);

        handler.handle(mockEvent);
        verifyNoInteractions(mockListener);
    }

    @Test
    void handle_shouldNotifyAllListeners() {
        IllegalInputEventHandler handler = new IllegalInputEventHandler();
        handler.addListener(mockListener);
        handler.addListener(mockListener2);

        handler.handle(mockEvent);

        verify(mockListener).onIllegalInputEvent(mockEvent);
        verify(mockListener2).onIllegalInputEvent(mockEvent);
    }

    @Test
    void handle_shouldDoNothingWhenNoListenerSet() {
        IllegalInputEventHandler handler = new IllegalInputEventHandler();

        Throwable throwable = catchThrowable(() -> handler.handle(mockEvent));

        assertThat(throwable).isNull();
    }
}