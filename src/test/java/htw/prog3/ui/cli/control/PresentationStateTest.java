package htw.prog3.ui.cli.control;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.routing.input.listRequest.cargos.ListCargosReqEvent;
import htw.prog3.routing.input.listRequest.customers.ListCustomersReqEvent;
import htw.prog3.routing.input.listRequest.hazards.ListHazardsReqEvent;
import htw.prog3.ui.cli.control.CommandLineReader;
import htw.prog3.ui.cli.control.PresentationState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class PresentationStateTest {
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
        PresentationState state = PresentationState.getInstance();
        PresentationState other = PresentationState.getInstance();

        assertThat(state)
                .isNotNull()
                .isSameAs(other);
    }

    @Test
    void utilizeCmd_shouldFireListCustomersReqEventForCustomerInput() {
        PresentationState state = PresentationState.getInstance();

        state.utilizeCmd("customer", mockContext);

        verify(mockContext).fireListCustomersReqEvent(any(ListCustomersReqEvent.class));
    }

    @Test
    void utilizeCmd_shouldFireListCargosReqEventForCargoInput() {
        PresentationState state = PresentationState.getInstance();

        state.utilizeCmd("cargo", mockContext);

        verify(mockContext).fireListCargosReqEvent(any(ListCargosReqEvent.class));
    }

    @Test
    void utilizeCmd_shouldFireListHazardsReqEventForHazardInput() {
        PresentationState state = PresentationState.getInstance();

        state.utilizeCmd("hazard", mockContext);

        verify(mockContext).fireListHazardsReqEvent(any(ListHazardsReqEvent.class));
    }

    @Test
    void utilizeCmd_shouldFireIllegalInputEventOnFaultyInclusiveFlagForHazards() {
        PresentationState state = PresentationState.getInstance();

        state.utilizeCmd("hazard x", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(InputFailureMessages.PARSING_INCLUSIVE_HAZARDS_REQUESTED_FAILED);
    }

    @Test
    void utilizeCmd_shouldFireIllegalInputEventForWrongInput() {
        PresentationState state = PresentationState.getInstance();

        state.utilizeCmd("x", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(InputFailureMessages.BAD_ARGUMENTS);
    }

    @Test
    void isValidCmd_shouldReturnTrueForSingleWord() {
        PresentationState state = PresentationState.getInstance();

        boolean actual = state.isValidCmd("singleWord");

        assertThat(actual).isTrue();
    }

    @Test
    void isValidCmd_shouldReturnTrueForTwoWords() {
        PresentationState state = PresentationState.getInstance();

        boolean actual = state.isValidCmd("two words");

        assertThat(actual).isTrue();
    }

    @Test
    void getPromptName_shouldReturnProperName() {
        PresentationState state = PresentationState.getInstance();

        String actual = state.getPromptName();

        assertThat(actual).isEqualTo("read");
    }
}