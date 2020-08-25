package htw.prog3;

import htw.prog3.log.*;
import htw.prog3.routing.EventRoutingConfigurator;
import htw.prog3.routing.UiEventController;
import htw.prog3.routing.config.ViewConfigEventHandler;
import htw.prog3.routing.error.IllegalInputEventHandler;
import htw.prog3.routing.input.create.cargo.AddCargoEventHandler;
import htw.prog3.routing.input.create.customer.AddCustomerEventHandler;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEventHandler;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEventHandler;
import htw.prog3.routing.input.listRequest.cargos.ListCargosReqEventHandler;
import htw.prog3.routing.input.listRequest.customers.ListCustomersReqEventHandler;
import htw.prog3.routing.input.listRequest.hazards.ListHazardsReqEventHandler;
import htw.prog3.routing.input.update.inspect.InspectCargoEventHandler;
import htw.prog3.routing.persistence.all.load.LoadAllEventHandler;
import htw.prog3.routing.persistence.all.save.SaveAllEventHandler;
import htw.prog3.routing.persistence.item.load.LoadItemEventHandler;
import htw.prog3.routing.persistence.item.save.SaveItemEventHandler;
import htw.prog3.routing.success.ActionSuccessEventHandler;
import htw.prog3.routing.view.listResponse.cargos.ListCargosResEventHandler;
import htw.prog3.routing.view.listResponse.customers.ListCustomersResEventHandler;
import htw.prog3.routing.view.listResponse.hazards.ListHazardsResEventHandler;
import htw.prog3.routing.view.update.cargos.UpdateCargoViewEventHandler;
import htw.prog3.sm.api.StorageFacade;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.ui.cli.control.CommandLineReader;
import htw.prog3.ui.cli.view.CommandLineWriter;
import htw.prog3.ui.cli.view.listener.CliViewUpdater;
import htw.prog3.ui.cli.view.listener.CriticalCapacityListener;
import htw.prog3.ui.cli.view.listener.HazardChangeListener;
import htw.prog3.util.SetupHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class CommandLineApp {
    private SmLogManager logManager;

    public static void main(String[] args) {
        CommandLineApp app = new CommandLineApp();
        CommandLineReader reader = app.handleArgs(args);

        app.start(reader);
    }

    private CommandLineReader handleArgs(String[] args) {
        int capacity = SetupHelper.readCapacity(args.length > 0 ? args[0] : null);
        FileOutputStream logStream = SetupHelper.readLogPath(args.length > 1 ? args[1] : null);
        Locale logLang = SetupHelper.readLogLang(args.length > 2 ? args[2] : "en");

        return setUpEnvironment(capacity, logStream, logLang);
    }

    private void start(CommandLineReader reader) {
        do {
            reader.run();
        } while (!reader.isExitRequested());
        exit();
    }

    private void exit() {
        if (logManager != null) {
            try {
                logManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    @SuppressWarnings("DuplicatedCode")
    private CommandLineReader setUpEnvironment(int capacity, FileOutputStream logStream, Locale locale) {
        StorageManagement storageManagement = StorageManagement.ofCapacity(capacity);
        StorageFacade storageFacade = new StorageFacade(storageManagement);

        ActionSuccessEventHandler successHandler = new ActionSuccessEventHandler();
        IllegalInputEventHandler failureHandler = new IllegalInputEventHandler();

        AddCargoEventHandler addCargoEventHandler = new AddCargoEventHandler();
        AddCustomerEventHandler addCustomerEventHandler = new AddCustomerEventHandler();

        ListCargosReqEventHandler listCargosReqEventHandler = new ListCargosReqEventHandler();
        ListCustomersReqEventHandler listCustomersReqEventHandler = new ListCustomersReqEventHandler();
        ListHazardsReqEventHandler listHazardsReqEventHandler = new ListHazardsReqEventHandler();

        ListCargosResEventHandler listCargosResEventHandler = new ListCargosResEventHandler();
        ListCustomersResEventHandler listCustomersResEventHandler = new ListCustomersResEventHandler();
        ListHazardsResEventHandler listHazardsResEventHandler = new ListHazardsResEventHandler();

        RemoveCargoEventHandler removeCargoEventHandler = new RemoveCargoEventHandler();
        RemoveCustomerEventHandler removeCustomerEventHandler = new RemoveCustomerEventHandler();

        InspectCargoEventHandler inspectCargoEventHandler = new InspectCargoEventHandler();

        SaveAllEventHandler saveAllEventHandler = new SaveAllEventHandler();
        LoadAllEventHandler loadAllEventHandler = new LoadAllEventHandler();
        SaveItemEventHandler saveItemEventHandler = new SaveItemEventHandler();
        LoadItemEventHandler loadItemEventHandler = new LoadItemEventHandler();

        UpdateCargoViewEventHandler updateCargoViewEventHandler = new UpdateCargoViewEventHandler();

        if (null != logStream) try {
            PrintWriter writer = new PrintWriter(logStream, true);

            LogDictionaryLoader loader = LogDictionaryLoader.from(locale);
            ProcessLogDictionary processLogDict = loader.loadProcessLogDictionary();
            ProcessLogger processLogger = new ProcessLogger(storageFacade, writer, processLogDict);
            updateCargoViewEventHandler.addListener(processLogger);

            InteractionLogDictionary interactionLogDict = loader.loadInteractionLogDictionary();
            InteractionLogger interactionLogger = new InteractionLogger(writer, interactionLogDict);

            interactionLogger.registerAddCargoEventListener(addCargoEventHandler);
            interactionLogger.registerAddCustomerEventListener(addCustomerEventHandler);
            interactionLogger.registerRemoveCargoEventListener(removeCargoEventHandler);
            interactionLogger.registerRemoveCustomerEventListener(removeCustomerEventHandler);
            interactionLogger.registerInspectCargoEventListener(inspectCargoEventHandler);
            interactionLogger.registerSaveItemEventListener(saveItemEventHandler);
            interactionLogger.registerLoadItemEventListener(loadItemEventHandler);
            interactionLogger.registerListCargosReqEventListener(listCargosReqEventHandler);
            interactionLogger.registerListCustomersReqEventListener(listCustomersReqEventHandler);
            interactionLogger.registerListHazardsReqEventListener(listHazardsReqEventHandler);

            logManager = SmLogManager.from(interactionLogger, processLogger);
        } catch (IOException e) {
            System.out.println("The logger can't be started due to problems with InputStream.");
        }

        UiEventController eventController = new UiEventController(storageFacade)
                .setActionSuccessEventHandler(successHandler)
                .setIllegalInputEventHandler(failureHandler)
                .setListCargosResEventHandler(listCargosResEventHandler)
                .setListCustomersResEventHandler(listCustomersResEventHandler)
                .setListHazardsResEventHandler(listHazardsResEventHandler)
                .setUpdateCargoViewEventHandler(updateCargoViewEventHandler);

        addCargoEventHandler.addListener(eventController);
        addCustomerEventHandler.addListener(eventController);
        removeCargoEventHandler.addListener(eventController);
        removeCustomerEventHandler.addListener(eventController);
        inspectCargoEventHandler.addListener(eventController);
        listCargosReqEventHandler.addListener(eventController);
        listCustomersReqEventHandler.addListener(eventController);
        listHazardsReqEventHandler.addListener(eventController);
        saveAllEventHandler.addListener(eventController);
        loadAllEventHandler.addListener(eventController);
        saveItemEventHandler.addListener(eventController);
        loadItemEventHandler.addListener(eventController);

        CriticalCapacityListener capacityListener = new CriticalCapacityListener(storageManagement);
        HazardChangeListener hazardChangeListener = new HazardChangeListener(storageFacade);

        ViewConfigEventHandler viewConfigEventHandler = new ViewConfigEventHandler();
        viewConfigEventHandler.addListener(hazardChangeListener);
        viewConfigEventHandler.addListener(capacityListener);

        CliViewUpdater viewUpdater = new CliViewUpdater(storageFacade);
        viewUpdater.setViewConfigEventHandler(viewConfigEventHandler);
        updateCargoViewEventHandler.addListener(viewUpdater);

        EventRoutingConfigurator config = new EventRoutingConfigurator(eventController);
        config.setViewConfigurationEventHandler(viewConfigEventHandler);

        CommandLineReader reader = new CommandLineReader(config);
        reader.setActionSuccessEventHandler(successHandler);
        reader.setIllegalInputEventHandler(failureHandler);
        reader.setAddCargoEventHandler(addCargoEventHandler);
        reader.setAddCustomerEventHandler(addCustomerEventHandler);
        reader.setListCargosReqEventHandler(listCargosReqEventHandler);
        reader.setListCustomersReqEventHandler(listCustomersReqEventHandler);
        reader.setListHazardsReqEventHandler(listHazardsReqEventHandler);
        reader.setRemoveCargoEventHandler(removeCargoEventHandler);
        reader.setRemoveCustomerEventHandler(removeCustomerEventHandler);
        reader.setInspectCargoEventHandler(inspectCargoEventHandler);
        reader.setSaveAllEventHandler(saveAllEventHandler);
        reader.setLoadAllEventHandler(loadAllEventHandler);
        reader.setSaveItemEventHandler(saveItemEventHandler);
        reader.setLoadItemEventHandler(loadItemEventHandler);

        CommandLineWriter writer = new CommandLineWriter();
        successHandler.addListener(writer);
        failureHandler.addListener(writer);
        listCargosResEventHandler.addListener(writer);
        listCustomersResEventHandler.addListener(writer);
        listHazardsResEventHandler.addListener(writer);

        return reader;
    }
}
