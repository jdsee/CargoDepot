package htw.prog3.ui.cli.control;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.routing.persistence.all.PersistenceType;
import htw.prog3.ui.cli.control.CommandLineReader;
import htw.prog3.ui.cli.control.PersistenceState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class PersistenceStateTest {
    @Mock
    CommandLineReader mockContext;
    @Captor
    ArgumentCaptor<PersistenceType> persistenceTypeCaptor;
    @Captor
    ArgumentCaptor<Integer> integerCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getInstance_shouldAlwaysReturnSameInstance() {
        PersistenceState state = PersistenceState.getInstance();
        PersistenceState other = PersistenceState.getInstance();

        assertThat(state)
                .isNotNull()
                .isSameAs(other);
    }

    @Test
    void getPromptName_shouldReturnProperName() {
        PersistenceState state = PersistenceState.getInstance();

        String actual = state.getPromptName();

        assertThat(actual).isEqualTo("persistence");
    }

    @Test
    void isValid_shouldReturnTrueForSingleWord() {
        PersistenceState state = PersistenceState.getInstance();

        boolean actual = state.isValidCmd("x");

        assertThat(actual).isTrue();
    }

    @Test
    void isValid_shouldReturnTrueForSingleWordFollowedByDigit() {
        PersistenceState state = PersistenceState.getInstance();

        boolean actual = state.isValidCmd("x 123");

        assertThat(actual).isTrue();
    }

    @Test
    void utilizeCmd_shouldFireSaveAllEventForJOS() {
        PersistenceState state = PersistenceState.getInstance();

        state.utilizeCmd("saveJOS", mockContext);

        verify(mockContext).fireSaveAllEvent(persistenceTypeCaptor.capture());
        assertThat(persistenceTypeCaptor.getValue()).isEqualTo(PersistenceType.JOS);
    }

    @Test
    void utilizeCmd_shouldFireSaveAllEventForJBP() {
        PersistenceState state = PersistenceState.getInstance();

        state.utilizeCmd("saveJBP", mockContext);

        verify(mockContext).fireSaveAllEvent(persistenceTypeCaptor.capture());
        assertThat(persistenceTypeCaptor.getValue()).isEqualTo(PersistenceType.JBP);
    }

    @Test
    void utilizeCmd_shouldFireIllegalInputEventForUnknownCommand() {
        PersistenceState state = PersistenceState.getInstance();

        state.utilizeCmd("x", mockContext);

        verify(mockContext).fireIllegalInputEvent(InputFailureMessages.BAD_ARGUMENTS);
    }

    @Test
    void utilizeCmd_shouldFireSaveItemEventWhenDigitWasPassed() {
        PersistenceState state = PersistenceState.getInstance();

        state.utilizeCmd("save 123", mockContext);

        verify(mockContext).fireSaveItemEvent(integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(123);
    }

    @Test
    void utilizeCmd_shouldFireBadArgsEventWhenOnlySaveIsPassed() {
        PersistenceState state = PersistenceState.getInstance();

        state.utilizeCmd("save", mockContext);

        verify(mockContext).fireIllegalInputEvent(InputFailureMessages.BAD_ARGUMENTS);
    }

    @Test
    void utilizeCmd_shouldFireLoadItemEvent() {
        PersistenceState state = PersistenceState.getInstance();

        state.utilizeCmd("load 123", mockContext);

        verify(mockContext).fireLoadItemEvent(integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(123);
    }

    @Test
    void utilizeCmd_shouldFireBadArgsEventWhenOnlyLoadIsPassed() {
        PersistenceState state = PersistenceState.getInstance();

        state.utilizeCmd("load", mockContext);

        verify(mockContext).fireIllegalInputEvent(InputFailureMessages.BAD_ARGUMENTS);
    }

    @Test
    void utilizeCmd_shouldFireLoadAllEventForJOS() {
        PersistenceState state = PersistenceState.getInstance();

        state.utilizeCmd("loadJOS", mockContext);

        verify(mockContext).fireLoadAllEvent(persistenceTypeCaptor.capture());
        assertThat(persistenceTypeCaptor.getValue()).isEqualTo(PersistenceType.JOS);
    }

    @Test
    void utilizeCmd_shouldFireLoadAllEventForJBP() {
        PersistenceState state = PersistenceState.getInstance();

        state.utilizeCmd("loadJBP", mockContext);

        verify(mockContext).fireLoadAllEvent(persistenceTypeCaptor.capture());
        assertThat(persistenceTypeCaptor.getValue()).isEqualTo(PersistenceType.JBP);
    }
}