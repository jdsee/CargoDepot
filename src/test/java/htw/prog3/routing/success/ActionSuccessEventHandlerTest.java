package htw.prog3.routing.success;

import htw.prog3.routing.success.ActionSuccessEvent;
import htw.prog3.routing.success.ActionSuccessEventHandler;
import htw.prog3.routing.success.ActionSuccessEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

class ActionSuccessEventHandlerTest {
    @Mock
    ActionSuccessEventListener mockListener;
    @Mock
    ActionSuccessEvent mockEvent;
    @Mock
    ActionSuccessEventListener mockListener2;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void addListener() {
        ActionSuccessEventHandler handler = new ActionSuccessEventHandler();

        handler.addListener(mockListener);

        handler.handle(mockEvent);
        verify(mockListener).onActionSuccessEvent(mockEvent);
    }

    @Test
    void removeListener() {
        ActionSuccessEventHandler handler = new ActionSuccessEventHandler();
        handler.addListener(mockListener);

        handler.removeListener(mockListener);

        handler.handle(mockEvent);
        verifyNoInteractions(mockListener);
    }

    @Test
    void handle_shouldNotifyAllListeners() {
        ActionSuccessEventHandler handler = new ActionSuccessEventHandler();
        handler.addListener(mockListener);
        handler.addListener(mockListener2);

        handler.handle(mockEvent);

        verify(mockListener).onActionSuccessEvent(mockEvent);
        verify(mockListener2).onActionSuccessEvent(mockEvent);
    }

    @Test
    void handle_shouldDoNothingWhenNoListenerSet() {
        ActionSuccessEventHandler handler = new ActionSuccessEventHandler();

        Throwable throwable = catchThrowable(() -> handler.handle(mockEvent));

        assertThat(throwable).isNull();
    }
}