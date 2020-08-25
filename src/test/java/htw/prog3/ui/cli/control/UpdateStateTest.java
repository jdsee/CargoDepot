package htw.prog3.ui.cli.control;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.routing.input.update.inspect.InspectCargoEvent;
import htw.prog3.ui.cli.control.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@SuppressWarnings("ResultOfMethodCallIgnored")
class UpdateStateTest {
    @Mock
    CommandLineReader mockContext;
    @Captor
    ArgumentCaptor<InspectCargoEvent> inspectCargoEventCaptor;
    @Captor
    ArgumentCaptor<String> stringCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void factoryMethod_shouldAlwaysReturnSameInstance() {
        UpdateState state = UpdateState.getInstance();
        UpdateState other = UpdateState.getInstance();

        assertThat(state)
                .isNotNull()
                .isSameAs(other);
    }

    @Test
    void utilizeCmd_shouldFireInspectCargoEventOnDigitInput() {
        UpdateState state = UpdateState.getInstance();

        state.utilizeCmd("123", mockContext);

        verify(mockContext).fireInspectCargoEvent(inspectCargoEventCaptor.capture());
        assertThat(inspectCargoEventCaptor.getValue().getStoragePosition()).isEqualTo(123);
    }

    @Test
    void utilizeCmd_shouldFireIllegalInputEventOnTooLongDigit() {
        UpdateState state = UpdateState.getInstance();

        state.utilizeCmd("123784957430434756546458304", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(InputFailureMessages.UNKNOWN_STORAGE_POSITION);
    }

    @Test
    void isValidCmd_shouldReturnTrueForDigits() {
        UpdateState state = UpdateState.getInstance();

        boolean actual = state.isValidCmd("123");

        assertThat(actual).isTrue();
    }

    @Test
    void processCmd_shouldFireIllegalInputEventOnNonValidInput() {
        UpdateState state = UpdateState.getInstance();

        state.processCmd("abc", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(InputFailureMessages.BAD_ARGUMENTS);
    }

    @Test
    void processCmd_shouldFireInspectCargoEventOnValidInput() {
        UpdateState state = UpdateState.getInstance();

        state.processCmd("123", mockContext);

        verify(mockContext).fireInspectCargoEvent(any(InspectCargoEvent.class));
    }

    @Test
    void processCmd_shouldInitiateStateChangeToCreateState() {
        UpdateState state = UpdateState.getInstance();
        doReturn(state).when(mockContext).getActualState();

        state.processCmd(":c", mockContext);

        verify(mockContext).setActualState(any(CreateState.class));
    }

    @Test
    void processCmd_shouldInitiateStateChangeToPresentationState() {
        UpdateState state = UpdateState.getInstance();
        doReturn(state).when(mockContext).getActualState();

        state.processCmd(":r", mockContext);

        verify(mockContext).setActualState(any(PresentationState.class));
    }

    @Test
    void processCmd_shouldInitiateStateChangeToDeleteState() {
        UpdateState state = UpdateState.getInstance();
        doReturn(state).when(mockContext).getActualState();

        state.processCmd(":d", mockContext);

        verify(mockContext).setActualState(any(DeleteState.class));
    }

    @Test
    void processCmd_shouldFireIllegalInputEventOnUnknownStateCommand() {
        UpdateState state = UpdateState.getInstance();
        doReturn(state).when(mockContext).getActualState();

        state.processCmd(":x", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(InputFailureMessages.UNKNOWN_STATE);
    }

    @Test
    void getPromptName_shouldReturnProperName() {
        UpdateState state = UpdateState.getInstance();

        String actual = state.getPromptName();

        assertThat(actual).isEqualTo("update");
    }
}