package htw.prog3.ui.cli.control;

import htw.prog3.routing.input.delete.cargo.RemoveCargoEvent;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEvent;
import htw.prog3.ui.cli.control.CommandLineReader;
import htw.prog3.ui.cli.control.DeleteState;
import htw.prog3.ui.cli.control.PersistenceState;
import htw.prog3.ui.cli.control.UpdateState;
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
class DeleteStateTest {
    @Mock
    CommandLineReader mockContext;
    @Captor
    ArgumentCaptor<String> stringCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void factoryMethod_shouldAlwaysReturnSameInstance() {
        DeleteState state = DeleteState.getInstance();
        DeleteState other = DeleteState.getInstance();

        assertThat(state)
                .isNotNull()
                .isSameAs(other);
    }

    @Test
    void utilizeCmd_shouldFireDeleteCargoEventOnDigitInput() {
        DeleteState state = DeleteState.getInstance();

        state.utilizeCmd("123", mockContext);

        verify(mockContext).fireDeleteCargoEvent(any(RemoveCargoEvent.class));
    }

    @Test
    void utilizeCmd_shouldFireIllegalInputEventOnTooLongNumber() {
        DeleteState state = DeleteState.getInstance();

        state.utilizeCmd("7648927014892037483927407238940723894783290", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
    }

    @Test
    void utilizeCmd_shouldFireDeleteCustomerEventOnNonDigitInput() {
        DeleteState state = DeleteState.getInstance();

        state.utilizeCmd("x", mockContext);

        verify(mockContext).fireDeleteCustomerEvent(any(RemoveCustomerEvent.class));
    }

    @Test
    void isValidCmd_shouldReturnTrueForSingleWord() {
        DeleteState state = DeleteState.getInstance();

        boolean actual = state.isValidCmd("singleWord");

        assertThat(actual).isTrue();
    }

    @Test
    void processCmd_shouldInitiateStateChangeToUpdateState() {
        DeleteState state = DeleteState.getInstance();
        doReturn(state).when(mockContext).getActualState();

        state.processCmd(":u", mockContext);

        verify(mockContext).setActualState(any(UpdateState.class));
    }

    @Test
    void processCmd_shouldInitiateStateChangeToPersistenceState() {
        DeleteState state = DeleteState.getInstance();
        doReturn(state).when(mockContext).getActualState();

        state.processCmd(":p", mockContext);

        verify(mockContext).setActualState(any(PersistenceState.class));
    }

    @Test
    void getPromptName_shouldReturnProperName() {
        DeleteState state = DeleteState.getInstance();

        String actual = state.getPromptName();

        assertThat(actual).isEqualTo("delete");
    }
}