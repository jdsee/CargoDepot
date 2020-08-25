package htw.prog3.ui.cli.control;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.ui.cli.control.CommandLineReader;
import htw.prog3.ui.cli.control.ConfigState;
import htw.prog3.ui.cli.control.DeleteState;
import htw.prog3.ui.cli.view.listener.CriticalCapacityListener;
import htw.prog3.ui.cli.view.listener.HazardChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@SuppressWarnings("ResultOfMethodCallIgnored")
class ConfigStateTest {
    @Mock
    CommandLineReader mockContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getInstance_shouldAlwaysReturnSameInstance() {
        ConfigState state = ConfigState.getInstance();
        ConfigState other = ConfigState.getInstance();

        assertThat(state)
                .isNotNull()
                .isSameAs(other);
    }

    @Test
    void isValidCmd_shouldReturnTrueForTwoWords() {
        ConfigState state = ConfigState.getInstance();

        boolean actual = state.isValidCmd("xyz xyz");

        assertThat(actual).isTrue();
    }

    @Test
    void utilizeCmd_shouldActivateAddCargoListenerOnRequest() {
        ConfigState state = ConfigState.getInstance();

        state.utilizeCmd("add AddCargoEventListener", mockContext);

        verify(mockContext).activateAddCargoEventListener();
    }

    @Test
    void utilizeCmd_shouldDeactivateAddCargoListenerOnRequest() {
        ConfigState state = ConfigState.getInstance();

        state.utilizeCmd("remove AddCargoEventListener", mockContext);

        verify(mockContext).deactivateAddCargoEventListener();
    }

    @Test
    void utilizeCmd_shouldActivateHazardsChangeListenerOnRequest() {
        ConfigState state = ConfigState.getInstance();

        state.utilizeCmd("add HazardChangeListener", mockContext);

        verify(mockContext).fireViewConfigEvent(HazardChangeListener.class, true);
    }

    @Test
    void utilizeCmd_shouldDeactivateHazardsChangeListenerOnRequest() {
        ConfigState state = ConfigState.getInstance();

        state.utilizeCmd("remove HazardChangeListener", mockContext);

        verify(mockContext).fireViewConfigEvent(HazardChangeListener.class, false);
    }


    @Test
    void utilizeCmd_shouldActivateCriticalCapacityListenerOnRequest() {
        ConfigState state = ConfigState.getInstance();

        state.utilizeCmd("add CriticalCapacityListener", mockContext);

        verify(mockContext).fireViewConfigEvent(CriticalCapacityListener.class, true);
    }

    @Test
    void utilizeCmd_shouldDeactivateCriticalCapacityListenerOnRequest() {
        ConfigState state = ConfigState.getInstance();

        state.utilizeCmd("remove CriticalCapacityListener", mockContext);

        verify(mockContext).fireViewConfigEvent(CriticalCapacityListener.class, false);
    }

    @Test
    void utilizeCmd_shouldFireIllegalInputEventForBadArgs() {
        ConfigState state = ConfigState.getInstance();

        state.utilizeCmd("xyz xyz", mockContext);

        verify(mockContext).fireIllegalInputEvent(InputFailureMessages.BAD_ARGUMENTS);
    }

    @Test
    void utilizeCmd_shouldFireIllegalInputEventForUnknownListener() {
        ConfigState state = ConfigState.getInstance();

        state.utilizeCmd("add xyz", mockContext);

        verify(mockContext).fireIllegalInputEvent(InputFailureMessages.UNKNOWN_LISTENER);
    }

    @Test
    void processCmd_shouldInitiateStateChangeToConfigState() {
        DeleteState state = DeleteState.getInstance();
        doReturn(state).when(mockContext).getActualState();

        state.processCmd(":config", mockContext);

        verify(mockContext).setActualState(any(ConfigState.class));
    }

    @Test
    void processCmd_shouldFireIllegalInputEventForUnknownState() {
        DeleteState state = DeleteState.getInstance();
        doReturn(state).when(mockContext).getActualState();

        state.processCmd(":xyz", mockContext);

        verify(mockContext).fireIllegalInputEvent(InputFailureMessages.UNKNOWN_STATE);
    }
}