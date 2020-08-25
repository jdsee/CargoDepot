package htw.prog3.ui.cli.controller;

import htw.prog3.persistence.jbp.XmlEncodingStrategy;
import htw.prog3.persistence.jos.SerializationStrategy;
import htw.prog3.routing.UiEventController;
import htw.prog3.routing.error.IllegalInputEvent;
import htw.prog3.routing.error.IllegalInputEventHandler;
import htw.prog3.routing.input.create.customer.AddCustomerEvent;
import htw.prog3.routing.persistence.all.PersistenceType;
import htw.prog3.routing.persistence.all.save.SaveAllEvent;
import htw.prog3.routing.success.ActionSuccessEvent;
import htw.prog3.routing.success.ActionSuccessEventHandler;
import htw.prog3.routing.success.ActionSuccessMessages;
import htw.prog3.sm.api.StorageFacade;
import htw.prog3.sm.core.FailureMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SuppressWarnings("ResultOfMethodCallIgnored")
class UiEventControllerTest {
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    AddCustomerEvent mockAddCustomerEvent;
    @Mock
    StorageFacade mockFacade;
    @Mock
    ActionSuccessEventHandler mockActionSuccessEventHandler;

    @Captor
    ArgumentCaptor<ActionSuccessEvent> actionSuccessEventCaptor;
    @Mock
    IllegalInputEventHandler mockIllegalInputEventHandler;
    @Captor
    ArgumentCaptor<IllegalInputEvent> illegalInputEventCaptor;

    @Test
    void onAddCustomerEvent_accessesValuesInEvent_Test() {
        UiEventController controller = new UiEventController(mockFacade);
        controller.setActionSuccessEventHandler(mockActionSuccessEventHandler);
        doReturn("x").when(mockAddCustomerEvent).getCustomerName();

        controller.onAddCustomerEvent(mockAddCustomerEvent);

        verify(mockAddCustomerEvent).getCustomerName();
        verifyNoMoreInteractions(mockAddCustomerEvent);
    }

    @Test
    void onAddCustomerEvent_storageMangerIsCalledWithArgsFromEvent_Test() {
        UiEventController controller = new UiEventController(mockFacade);
        controller.setActionSuccessEventHandler(mockActionSuccessEventHandler);
        when(mockAddCustomerEvent.getCustomerName()).thenReturn("x");

        controller.onAddCustomerEvent(mockAddCustomerEvent);

        verify(mockFacade).addCustomer("x");
    }

    @Test
    void onAddCustomerEvent_noReactionOnNullValue_Test() {
        UiEventController controller = new UiEventController(mockFacade);
        controller.setActionSuccessEventHandler(mockActionSuccessEventHandler);
        controller.setIllegalInputEventHandler(mockIllegalInputEventHandler);

        controller.onAddCustomerEvent(null);

        verifyNoInteractions(mockFacade);
        verifyNoInteractions(mockActionSuccessEventHandler);
        verifyNoInteractions(mockIllegalInputEventHandler);
    }

    @Test
    void onAddCustomerEvent_firesResponseEventOnSuccess_Test() {
        UiEventController controller = new UiEventController(mockFacade);
        controller.setActionSuccessEventHandler(mockActionSuccessEventHandler);
        when(mockAddCustomerEvent.getCustomerName()).thenReturn("x");

        controller.onAddCustomerEvent(mockAddCustomerEvent);

        verify(mockActionSuccessEventHandler).handle(actionSuccessEventCaptor.capture());
        assertThat(actionSuccessEventCaptor.getValue().getMessage())
                .isEqualTo("x " + ActionSuccessMessages.WAS_SUCCESSFULLY_ADDED);
    }

    @Test
    void onAddCustomerEvent_firesNoFailureEventOnSuccess_Test() {
        UiEventController controller = new UiEventController(mockFacade);
        controller.setIllegalInputEventHandler(mockIllegalInputEventHandler);
        controller.setActionSuccessEventHandler(mockActionSuccessEventHandler);
        when(mockAddCustomerEvent.getCustomerName()).thenReturn("x");

        controller.onAddCustomerEvent(mockAddCustomerEvent);

        verifyNoInteractions(mockIllegalInputEventHandler);
    }

    @Test
    void onAddCustomerEvent_firesIllegalInputEventIfCustomerNameIsAmbiguous_Test() {
        UiEventController controller = new UiEventController(mockFacade);
        controller.setIllegalInputEventHandler(mockIllegalInputEventHandler);
        doReturn("x").when(mockAddCustomerEvent).getCustomerName();
        doReturn(true).when(mockFacade).isPresentCustomer("x");

        controller.onAddCustomerEvent(mockAddCustomerEvent);

        verify(mockIllegalInputEventHandler).handle(illegalInputEventCaptor.capture());
        assertThat(illegalInputEventCaptor.getValue().getMessage())
                .isEqualTo(FailureMessages.customerNameAmbiguous("x"));
    }

    @Test
    void onAddCustomerEvent_doesNotModifyCustomerAdministrationIfCustomerNameIsAmbiguous_Test() {
        UiEventController controller = new UiEventController(mockFacade);
        controller.setActionSuccessEventHandler(mockActionSuccessEventHandler);
        controller.setIllegalInputEventHandler(mockIllegalInputEventHandler);
        doReturn("x").when(mockAddCustomerEvent).getCustomerName();
        doReturn(true).when(mockFacade).isPresentCustomer("x");

        controller.onAddCustomerEvent(mockAddCustomerEvent);

        verifyNoInteractions(mockActionSuccessEventHandler);
        verify(mockFacade, times(0)).addCustomer("x");
    }

    @Test
    void onSaveAllEvent_shouldCallSerializationForJosType() {
        UiEventController listener = new UiEventController(mockFacade);
        SaveAllEvent event = new SaveAllEvent(PersistenceType.JOS, this);

        listener.onSaveAllEvent(event);

        verify(mockFacade).save(any(SerializationStrategy.class));
    }

    @Test
    void onSaveAllEvent_shouldCallXmlEncodingForJbpType() {
        UiEventController listener = new UiEventController(mockFacade);
        SaveAllEvent event = new SaveAllEvent(PersistenceType.JBP, this);

        listener.onSaveAllEvent(event);

        verify(mockFacade).save(any(XmlEncodingStrategy.class));
    }
}