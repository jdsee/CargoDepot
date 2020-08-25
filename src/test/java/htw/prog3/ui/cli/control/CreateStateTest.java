package htw.prog3.ui.cli.control;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.routing.input.create.cargo.AddCargoEvent;
import htw.prog3.routing.input.create.customer.AddCustomerEvent;
import htw.prog3.sm.core.CargoType;
import htw.prog3.ui.cli.control.CommandLineReader;
import htw.prog3.ui.cli.control.CreateState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class CreateStateTest {
    @Mock
    CommandLineReader mockContext;
    @Captor
    ArgumentCaptor<AddCustomerEvent> addCustomerEventCaptor;
    @Captor
    ArgumentCaptor<AddCargoEvent> addCargoEventCaptor;
    @Captor
    ArgumentCaptor<String> stringCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getInstance_shouldAlwaysReturnSameInstance() {
        CreateState state = CreateState.getInstance();
        CreateState other = CreateState.getInstance();

        assertThat(state)
                .isNotNull()
                .isSameAs(other);
    }

    @Test
    void utilizeCmd_shouldTriggerAddCustomerEvent() {
        CreateState state = CreateState.getInstance();

        state.utilizeCmd("customer", mockContext);

        verify(mockContext).fireAddCustomerEvent(addCustomerEventCaptor.capture());
        assertThat(addCustomerEventCaptor.getValue().getCustomerName()).isEqualTo("customer");
    }

    @Test
    void utilizeCmd_shouldTriggerAddCargoEvent() {
        CreateState state = CreateState.getInstance();

        state.utilizeCmd("uc name 123.456789 123 , y n", mockContext);

        verify(mockContext).fireAddCargoEvent(addCargoEventCaptor.capture());
        assertThat(addCargoEventCaptor.getValue())
                .extracting(
                        AddCargoEvent::getCargoType,
                        AddCargoEvent::getOwnerName,
                        AddCargoEvent::getValue,
                        AddCargoEvent::getDurationOfStorage,
                        AddCargoEvent::isFragile,
                        AddCargoEvent::isPressurized)
                .containsExactly(
                        CargoType.UNITISED_CARGO,
                        "name",
                        new BigDecimal("123.456789"),
                        Duration.ofSeconds(123),
                        true,
                        false);
    }

    @Test
    void utilizeCmd_shouldTriggerAddCargoEventForMultipleHazards() {
        CreateState state = CreateState.getInstance();

        state.utilizeCmd("uc name 123 123 toxic, explosive y n", mockContext);

        verify(mockContext).fireAddCargoEvent(addCargoEventCaptor.capture());
    }

    @Test
    void utilizeCmd_shouldTriggerIllegalInputEventForUnspecifiedInputFormat() {
        CreateState state = CreateState.getInstance();

        state.utilizeCmd("x x x", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(InputFailureMessages.BAD_ARGUMENTS);
    }

    @Test
    void utilizeCmd_shouldTriggerIllegalInputEventOnUnknownHazard() {
        CreateState state = CreateState.getInstance();

        state.utilizeCmd("uc name 123 123 ,... y n", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(InputFailureMessages.UNKNOWN_HAZARD_TYPE);
    }

    @Test
    void utilizeCmd_shouldTriggerIllegalInputEventOnUnknownCargoType() {
        CreateState state = CreateState.getInstance();

        state.utilizeCmd("unknown name 123 123 , y n", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(InputFailureMessages.UNKNOWN_CARGO_TYPE);
    }

    @Test
    void utilizeCmd_shouldTriggerIllegalInputEventOnValueParsingFailure() {
        CreateState state = CreateState.getInstance();

        state.utilizeCmd("uc name abc 123 , y n", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(InputFailureMessages.PARSING_VALUE_FAILED);
    }

    @Test
    void utilizeCmd_shouldTriggerIllegalInputEventOnDurationParsingFailure() {
        CreateState state = CreateState.getInstance();

        state.utilizeCmd("uc name 123 abc , y n", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(InputFailureMessages.PARSING_DURATION_FAILED);
    }

    @Test
    void utilizeCmd_shouldTriggerIllegalInputEventOnFaultyIsFragileFlag() {
        CreateState state = CreateState.getInstance();

        state.utilizeCmd("uc name 123 123 , y x", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(InputFailureMessages.PARSING_IS_FRAGILE_FAILED);
    }

    @Test
    void utilizeCmd_shouldTriggerIllegalInputEventOnFaultyIsPressurizedFlag() {
        CreateState state = CreateState.getInstance();

        state.utilizeCmd("uc name 123 123 , x y", mockContext);

        verify(mockContext).fireIllegalInputEvent(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(InputFailureMessages.PARSING_IS_PRESSURIZED_FAILED);
    }

    @Test
    void isValidCmd_shouldReturnTrueForSingleWord() {
        CreateState state = CreateState.getInstance();

        boolean actual = state.isValidCmd("single");

        assertThat(actual).isTrue();
    }

    @Test
    void isValidCmd_shouldReturnTrueForAddCargoSyntax() {
        CreateState state = CreateState.getInstance();

        boolean actual = state.isValidCmd("uc name 123.32423 123 , n y");

        assertThat(actual).isTrue();
    }

    @Test
    void isValidCmd_shouldReturnTrueForAddCargoWithOneHazard() {
        CreateState state = CreateState.getInstance();

        boolean actual = state.isValidCmd("uc name 123.32 123 TOXIC n y");

        assertThat(actual).isTrue();
    }

    @Test
    void isValidCmd_shouldReturnTrueForAddCargoWithAllHazards() {
        CreateState state = CreateState.getInstance();

        boolean actual = state.isValidCmd("uc name 123 123 explosive, toxic, flammable, radioactive n y");

        assertThat(actual).isTrue();
    }

    @Test
    void isValidCmd_shouldReturnTrueForFullTypeName() {
        CreateState state = CreateState.getInstance();

        boolean actual = state.isValidCmd(
                "MixedCargoLiquidBulkAndUnitised Beispielkunde 4000.50 86400 radioactive n y");

        assertThat(actual).isTrue();
    }

    @Test
    void getPromptName_shouldReturnProperName() {
        CreateState state = CreateState.getInstance();

        String actual = state.getPromptName();

        assertThat(actual).isEqualTo("create");
    }
}