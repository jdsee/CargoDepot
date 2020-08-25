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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineReader {
    public static final String EXIT_CMD = "exit";

    private final BufferedReader reader;
    private final EventRoutingConfigurator config;

    private ActionSuccessEventHandler actionSuccessEventHandler;
    private IllegalInputEventHandler illegalInputEventHandler;
    private AddCustomerEventHandler addCustomerEventHandler;
    private AddCargoEventHandler addCargoEventHandler;
    private ListCustomersReqEventHandler listCustomersReqEventHandler;
    private ListCargosReqEventHandler listCargosReqEventHandler;
    private ListHazardsReqEventHandler listHazardsReqEventHandler;
    private InspectCargoEventHandler inspectCargoEventHandler;
    private RemoveCargoEventHandler removeCargoEventHandler;
    private RemoveCustomerEventHandler removeCustomerEventHandler;
    private SaveAllEventHandler saveAllEventHandler;
    private SaveItemEventHandler saveItemEventHandler;
    private LoadItemEventHandler loadItemEventHandler;
    private LoadAllEventHandler loadAllEventHandler;

    private CommandLineReaderState actualState = CreateState.getInstance();
    private boolean exitRequested = false;

    public CommandLineReader(EventRoutingConfigurator config, BufferedReader in) {
        this.config = config;
        this.reader = in;
    }

    public CommandLineReader(EventRoutingConfigurator config) {
        this(config, new BufferedReader(new InputStreamReader(System.in)));
    }

    CommandLineReaderState getActualState() {
        return actualState;
    }

    void setActualState(CommandLineReaderState state) {
        this.actualState = state;
    }

    public boolean isExitRequested() {
        return exitRequested;
    }

    public void run() {
        try {
            printPrompt();
            String input = reader.readLine();
            processCmd(input);
        } catch (IOException e) {
            fireIllegalInputEvent(InputFailureMessages.IO_FAIL);
        }
    }

    private void printPrompt() {
        System.out.printf("\n[%s] -> ", actualState.getPromptName());
    }

    public void setActionSuccessEventHandler(ActionSuccessEventHandler actionSuccessEventHandler) {
        this.actionSuccessEventHandler = actionSuccessEventHandler;
    }

    public void setIllegalInputEventHandler(IllegalInputEventHandler illegalInputEventHandler) {
        this.illegalInputEventHandler = illegalInputEventHandler;
    }

    private void processCmd(String input) {
        if (null == input || EXIT_CMD.equals(input))
            exitRequested = true;
        else
            actualState.processCmd(input.trim(), this);
    }

    public void setAddCustomerEventHandler(AddCustomerEventHandler handler) {
        this.addCustomerEventHandler = handler;
    }

    public void setAddCargoEventHandler(AddCargoEventHandler handler) {
        this.addCargoEventHandler = handler;
    }

    public void setListCustomersReqEventHandler(ListCustomersReqEventHandler handler) {
        this.listCustomersReqEventHandler = handler;
    }

    public void setListCargosReqEventHandler(ListCargosReqEventHandler handler) {
        this.listCargosReqEventHandler = handler;
    }

    public void setListHazardsReqEventHandler(ListHazardsReqEventHandler handler) {
        this.listHazardsReqEventHandler = handler;
    }

    public void setRemoveCargoEventHandler(RemoveCargoEventHandler handler) {
        this.removeCargoEventHandler = handler;
    }

    public void setRemoveCustomerEventHandler(RemoveCustomerEventHandler handler) {
        this.removeCustomerEventHandler = handler;
    }

    public void setInspectCargoEventHandler(InspectCargoEventHandler handler) {
        this.inspectCargoEventHandler = handler;
    }

    public void setSaveAllEventHandler(SaveAllEventHandler handler) {
        this.saveAllEventHandler = handler;
    }

    public void setSaveItemEventHandler(SaveItemEventHandler saveItemEventHandler) {
        this.saveItemEventHandler = saveItemEventHandler;
    }

    public void setLoadItemEventHandler(LoadItemEventHandler handler) {
        this.loadItemEventHandler = handler;
    }

    public void setLoadAllEventHandler(LoadAllEventHandler loadAllEventHandler) {
        this.loadAllEventHandler = loadAllEventHandler;
    }

    void fireActionSuccessEvent(String message) {
        ActionSuccessEvent event = new ActionSuccessEvent(message, this);
        actionSuccessEventHandler.handle(event);
    }

    void fireIllegalInputEvent(String message) {
        IllegalInputEvent event = new IllegalInputEvent(message, IllegalInputEvent.Trigger.ANY, this);
        illegalInputEventHandler.handle(event);
    }

    void fireAddCustomerEvent(AddCustomerEvent event) {
        addCustomerEventHandler.handle(event);
    }

    void fireAddCargoEvent(AddCargoEvent event) {
        addCargoEventHandler.handle(event);
    }

    void fireListCustomersReqEvent(ListCustomersReqEvent event) {
        listCustomersReqEventHandler.handle(event);
    }

    void fireListCargosReqEvent(ListCargosReqEvent event) {
        listCargosReqEventHandler.handle(event);
    }

    void fireListHazardsReqEvent(ListHazardsReqEvent event) {
        listHazardsReqEventHandler.handle(event);
    }

    void fireInspectCargoEvent(InspectCargoEvent event) {
        inspectCargoEventHandler.handle(event);
    }

    void fireDeleteCargoEvent(RemoveCargoEvent event) {
        removeCargoEventHandler.handle(event);
    }

    void fireDeleteCustomerEvent(RemoveCustomerEvent event) {
        removeCustomerEventHandler.handle(event);
    }

    void fireSaveAllEvent(PersistenceType type) {
        if (null != saveAllEventHandler) {
            SaveAllEvent event = new SaveAllEvent(type, this);
            saveAllEventHandler.handle(event);
        }
    }

    public void fireLoadAllEvent(PersistenceType type) {
        if (null != loadAllEventHandler) {
            LoadAllEvent event = new LoadAllEvent(type, this);
            loadAllEventHandler.handle(event);
        }
    }

    void fireSaveItemEvent(int position) {
        if (null != saveItemEventHandler) {
            SaveItemEvent event = new SaveItemEvent(position, this);
            saveItemEventHandler.handle(event);
        }
    }

    public void fireLoadItemEvent(Integer position) {
        if (null != loadItemEventHandler) {
            LoadItemEvent event = new LoadItemEvent(position, this);
            loadItemEventHandler.handle(event);
        }

    }

    void fireViewConfigEvent(Class<? extends ViewConfigEventListener> type, boolean activation) {
        config.fireViewConfigurationEvent(type, activation);
    }

    void activateAddCargoEventListener() {
        config.activateEventListener(addCargoEventHandler);
    }

    void deactivateAddCargoEventListener() {
        config.deactivateEventListener(addCargoEventHandler);
    }
}