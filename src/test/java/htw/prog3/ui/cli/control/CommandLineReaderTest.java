package htw.prog3.ui.cli.control;

import htw.prog3.routing.EventRoutingConfigurator;
import htw.prog3.routing.config.ViewConfigEventListener;
import htw.prog3.routing.error.IllegalInputEvent;
import htw.prog3.routing.error.IllegalInputEventHandler;
import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.routing.input.create.cargo.AddCargoEvent;
import htw.prog3.routing.input.create.cargo.AddCargoEventHandler;
import htw.prog3.routing.input.create.customer.AddCustomerEvent;
import htw.prog3.routing.input.create.customer.AddCustomerEventHandler;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEvent;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEventHandler;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEvent;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEventHandler;
import htw.prog3.routing.input.listRequest.cargos.ListCargosReqEvent;
import htw.prog3.routing.input.listRequest.cargos.ListCargosReqEventHandler;
import htw.prog3.routing.input.listRequest.customers.ListCustomersReqEvent;
import htw.prog3.routing.input.listRequest.customers.ListCustomersReqEventHandler;
import htw.prog3.routing.input.listRequest.hazards.ListHazardsReqEvent;
import htw.prog3.routing.input.listRequest.hazards.ListHazardsReqEventHandler;
import htw.prog3.routing.input.update.inspect.InspectCargoEvent;
import htw.prog3.routing.input.update.inspect.InspectCargoEventHandler;
import htw.prog3.routing.persistence.all.PersistenceType;
import htw.prog3.routing.persistence.all.load.LoadAllEvent;
import htw.prog3.routing.persistence.all.load.LoadAllEventHandler;
import htw.prog3.routing.persistence.all.save.SaveAllEvent;
import htw.prog3.routing.persistence.all.save.SaveAllEventHandler;
import htw.prog3.routing.persistence.item.load.LoadItemEvent;
import htw.prog3.routing.persistence.item.load.LoadItemEventHandler;
import htw.prog3.routing.persistence.item.save.SaveItemEvent;
import htw.prog3.routing.persistence.item.save.SaveItemEventHandler;
import htw.prog3.routing.success.ActionSuccessEvent;
import htw.prog3.routing.success.ActionSuccessEventHandler;
import htw.prog3.ui.cli.control.CommandLineReader;
import htw.prog3.ui.cli.control.CommandLineReaderState;
import htw.prog3.ui.cli.control.CreateState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommandLineReaderTest {
    @Mock
    EventRoutingConfigurator mockConfig;
    @Mock
    BufferedReader mockIn;
    @Mock
    CommandLineReaderState mockState;
    @Captor
    ArgumentCaptor<IllegalInputEvent> illegalInputEventCaptor;
    @Mock
    ActionSuccessEventHandler mockActionSuccessHandler;
    @Mock
    IllegalInputEventHandler mockIllegalInputHandler;
    @Mock
    AddCustomerEventHandler mockAddCustomerHandler;
    @Mock
    AddCustomerEvent mockAddCustomerEvent;
    @Mock
    AddCargoEventHandler mockAddCargoHandler;
    @Mock
    AddCargoEvent mockAddCargoEvent;
    @Mock
    ListCustomersReqEventHandler mockListCustomersReqHandler;
    @Mock
    ListCustomersReqEvent mockListCustomersReqEvent;
    @Mock
    ListCargosReqEventHandler mockListCargosReqHandler;
    @Mock
    ListCargosReqEvent mockListCargosReqEvent;
    @Mock
    ListHazardsReqEventHandler mockListHazardsReqHandler;
    @Mock
    ListHazardsReqEvent mockListHazardsReqEvent;
    @Mock
    RemoveCargoEventHandler mockDeleteCargoHandler;
    @Mock
    RemoveCargoEvent mockRemoveCargoEvent;
    @Mock
    RemoveCustomerEventHandler mockDeleteCustomerHandler;
    @Mock
    RemoveCustomerEvent mockRemoveCustomerEvent;
    @Mock
    InspectCargoEventHandler mockInspectCargoHandler;
    @Mock
    InspectCargoEvent mockInspectCargoEvent;
    @Mock
    SaveAllEventHandler mockSaveAllHandler;
    @Mock
    PersistenceType mockPersistenceType;
    @Mock
    SaveItemEventHandler mockSaveItemHandler;
    @Captor
    ArgumentCaptor<SaveItemEvent> saveItemEventCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void defaultConstructor_returnsNonNullInstance() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        assertThat(reader).isNotNull();
    }

    @Test
    void constructorWithInputStream_returnsNonNullInstance() {
        CommandLineReader reader = new CommandLineReader(mockConfig, mockIn);

        assertThat(reader).isNotNull();
    }

    @Test
    void run_shouldDelegateCommandProcessingToActualState() throws IOException {
        doReturn("x").when(mockIn).readLine();
        CommandLineReader reader = new CommandLineReader(mockConfig, mockIn);
        reader.setActualState(mockState);

        reader.run();

        verify(mockState).processCmd("x", reader);
    }

    @Test
    void run_shouldSetExitRequestedFlagOnExitInput() throws IOException {
        doReturn("exit").when(mockIn).readLine();
        CommandLineReader reader = new CommandLineReader(mockConfig, mockIn);
        reader.setActualState(mockState);

        reader.run();

        assertThat(reader.isExitRequested()).isTrue();
    }

    @Test
    void run_shouldFireIllegalInputEventOnIoException() throws IOException {
        doThrow(IOException.class).when(mockIn).readLine();
        CommandLineReader reader = new CommandLineReader(mockConfig, mockIn);
        reader.setIllegalInputEventHandler(mockIllegalInputHandler);

        reader.run();

        verify(mockIllegalInputHandler).handle(illegalInputEventCaptor.capture());
        assertThat(illegalInputEventCaptor.getValue().getMessage()).isEqualTo(InputFailureMessages.IO_FAIL);
    }

    @Test
    void setActualState() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setActualState(mockState);

        assertThat(reader.getActualState()).isEqualTo(mockState);
    }

    @Test
    void getActualState_returnsAddStateInitially() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        CommandLineReaderState actualState = reader.getActualState();

        assertThat(actualState).isOfAnyClassIn(CreateState.class);
    }

    @Test
    void isExitRequested_shouldReturnFalseInitially() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        boolean actual = reader.isExitRequested();

        assertThat(actual).isFalse();
    }

    @Test
    void setActionSuccessEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setActionSuccessEventHandler(mockActionSuccessHandler);

        reader.fireActionSuccessEvent("x");
        verify(mockActionSuccessHandler).handle(any(ActionSuccessEvent.class));
    }

    @Test
    void setIllegalInputEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setIllegalInputEventHandler(mockIllegalInputHandler);

        reader.fireIllegalInputEvent("x");
        verify(mockIllegalInputHandler).handle(any(IllegalInputEvent.class));
    }

    @Test
    void setAddCustomerEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setAddCustomerEventHandler(mockAddCustomerHandler);

        reader.fireAddCustomerEvent(mockAddCustomerEvent);
        verify(mockAddCustomerHandler).handle(mockAddCustomerEvent);
    }

    @Test
    void setAddCargoEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setAddCargoEventHandler(mockAddCargoHandler);

        reader.fireAddCargoEvent(mockAddCargoEvent);
        verify(mockAddCargoHandler).handle(any(AddCargoEvent.class));
    }

    @Test
    void setListCustomersReqEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setListCustomersReqEventHandler(mockListCustomersReqHandler);

        reader.fireListCustomersReqEvent(mockListCustomersReqEvent);
        verify(mockListCustomersReqHandler).handle(any(ListCustomersReqEvent.class));
    }

    @Test
    void setListCargosReqEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setListCargosReqEventHandler(mockListCargosReqHandler);

        reader.fireListCargosReqEvent(mockListCargosReqEvent);
        verify(mockListCargosReqHandler).handle(any(ListCargosReqEvent.class));
    }

    @Test
    void setListHazardsReqEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setListHazardsReqEventHandler(mockListHazardsReqHandler);

        reader.fireListHazardsReqEvent(mockListHazardsReqEvent);
        verify(mockListHazardsReqHandler).handle(any(ListHazardsReqEvent.class));
    }

    @Mock
    LoadItemEventHandler mockLoadItemHandler;
    @Mock
    LoadAllEventHandler mockLoadAllHandler;

    @Test
    void setInspectCargoEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setInspectCargoEventHandler(mockInspectCargoHandler);

        reader.fireInspectCargoEvent(mockInspectCargoEvent);
        verify(mockInspectCargoHandler).handle(any(InspectCargoEvent.class));
    }

    @Test
    void setSaveAllEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setSaveAllEventHandler(mockSaveAllHandler);

        reader.fireSaveAllEvent(mockPersistenceType);
        verify(mockSaveAllHandler).handle(any(SaveAllEvent.class));
    }

    @Test
    void setSaveItemEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setSaveItemEventHandler(mockSaveItemHandler);

        reader.fireSaveItemEvent(123);
        verify(mockSaveItemHandler).handle(saveItemEventCaptor.capture());
        assertThat(saveItemEventCaptor.getValue())
                .extracting(SaveItemEvent::getStoragePosition)
                .containsExactly(123);
    }

    @Test
    void fireIllegalInputEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setIllegalInputEventHandler(mockIllegalInputHandler);

        reader.fireIllegalInputEvent("x");

        verify(mockIllegalInputHandler).handle(any(IllegalInputEvent.class));
    }

    @Test
    void fireActionSuccessEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setActionSuccessEventHandler(mockActionSuccessHandler);

        reader.fireActionSuccessEvent("x");

        verify(mockActionSuccessHandler).handle(any(ActionSuccessEvent.class));
    }

    @Test
    void fireAddCustomerEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setAddCustomerEventHandler(mockAddCustomerHandler);

        reader.fireAddCustomerEvent(mockAddCustomerEvent);

        verify(mockAddCustomerHandler).handle(any(AddCustomerEvent.class));
    }

    @Test
    void fireAddCargoEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setAddCargoEventHandler(mockAddCargoHandler);

        reader.fireAddCargoEvent(mockAddCargoEvent);

        verify(mockAddCargoHandler).handle(any(AddCargoEvent.class));
    }

    @Test
    void fireListCustomersReqEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setListCustomersReqEventHandler(mockListCustomersReqHandler);

        reader.fireListCustomersReqEvent(mockListCustomersReqEvent);

        verify(mockListCustomersReqHandler).handle(any(ListCustomersReqEvent.class));
    }

    @Test
    void fireListCargosViewEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setListCargosReqEventHandler(mockListCargosReqHandler);

        reader.fireListCargosReqEvent(mockListCargosReqEvent);

        verify(mockListCargosReqHandler).handle(any(ListCargosReqEvent.class));
    }

    @Test
    void fireListHazardsReqEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setListHazardsReqEventHandler(mockListHazardsReqHandler);

        reader.fireListHazardsReqEvent(mockListHazardsReqEvent);

        verify(mockListHazardsReqHandler).handle(any(ListHazardsReqEvent.class));
    }

    @Test
    void fireInspectCargoEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setInspectCargoEventHandler(mockInspectCargoHandler);

        reader.fireInspectCargoEvent(mockInspectCargoEvent);

        verify(mockInspectCargoHandler).handle(any(InspectCargoEvent.class));
    }

    @Test
    void setDeleteCargoEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setRemoveCargoEventHandler(mockDeleteCargoHandler);

        reader.fireDeleteCargoEvent(mockRemoveCargoEvent);
        verify(mockDeleteCargoHandler).handle(any(RemoveCargoEvent.class));
    }

    @Test
    void setDeleteCustomerEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setRemoveCustomerEventHandler(mockDeleteCustomerHandler);

        reader.fireDeleteCustomerEvent(mockRemoveCustomerEvent);
        verify(mockDeleteCustomerHandler).handle(any(RemoveCustomerEvent.class));
    }


    @Test
    void fireSaveAllEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setSaveAllEventHandler(mockSaveAllHandler);

        reader.fireSaveAllEvent(mockPersistenceType);

        verify(mockSaveAllHandler).handle(any(SaveAllEvent.class));
    }

    @Test
    void fireSaveItemEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setSaveItemEventHandler(mockSaveItemHandler);

        reader.fireSaveItemEvent(123);

        verify(mockSaveItemHandler).handle(any(SaveItemEvent.class));
    }

    @Test
    void activateAddCargoListener_shouldActivateListenerOnConfigurator() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        AddCargoEventHandler handler = new AddCargoEventHandler();
        reader.setAddCargoEventHandler(handler);

        reader.activateAddCargoEventListener();

        reader.fireAddCargoEvent(mockAddCargoEvent);
        verify(mockConfig).activateEventListener(handler);
    }

    @Test
    void deactivateAddCargoListener_shouldDeactivateListenerOnConfigurator() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        AddCargoEventHandler handler = new AddCargoEventHandler();
        reader.setAddCargoEventHandler(handler);

        reader.deactivateAddCargoEventListener();

        reader.fireAddCargoEvent(mockAddCargoEvent);
        verify(mockConfig).deactivateEventListener(handler);
    }

    @Test
    void fireViewConfigurationEvent_shouldDelegateToConfigurator() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.fireViewConfigEvent(ViewConfigEventListener.class, true);

        verify(mockConfig).fireViewConfigurationEvent(ViewConfigEventListener.class, true);
    }

    @Test
    void fireDeleteCargoEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setRemoveCargoEventHandler(mockDeleteCargoHandler);

        reader.fireDeleteCargoEvent(mockRemoveCargoEvent);

        verify(mockDeleteCargoHandler).handle(any(RemoveCargoEvent.class));
    }

    @Test
    void fireDeleteCustomerEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setRemoveCustomerEventHandler(mockDeleteCustomerHandler);

        reader.fireDeleteCustomerEvent(mockRemoveCustomerEvent);

        verify(mockDeleteCustomerHandler).handle(any(RemoveCustomerEvent.class));
    }

    @Test
    void setLoadItemEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setLoadItemEventHandler(mockLoadItemHandler);

        reader.fireLoadItemEvent(1);
        verify(mockLoadItemHandler).handle(any(LoadItemEvent.class));
    }

    @Test
    void fireLoadItemEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setLoadItemEventHandler(mockLoadItemHandler);

        reader.fireLoadItemEvent(1);

        verify(mockLoadItemHandler).handle(any(LoadItemEvent.class));
    }

    @Test
    void setLoadAllEventHandler() {
        CommandLineReader reader = new CommandLineReader(mockConfig);

        reader.setLoadAllEventHandler(mockLoadAllHandler);

        reader.fireLoadAllEvent(PersistenceType.JOS);
        verify(mockLoadAllHandler).handle(any(LoadAllEvent.class));
    }

    @Test
    void fireLoadAllEvent() {
        CommandLineReader reader = new CommandLineReader(mockConfig);
        reader.setLoadAllEventHandler(mockLoadAllHandler);

        reader.fireLoadAllEvent(PersistenceType.JBP);

        verify(mockLoadAllHandler).handle(any(LoadAllEvent.class));
    }
}