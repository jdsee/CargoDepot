package htw.prog3;

import htw.prog3.log.*;
import htw.prog3.routing.UiEventController;
import htw.prog3.routing.error.IllegalInputEventHandler;
import htw.prog3.routing.input.create.cargo.AddCargoEventHandler;
import htw.prog3.routing.input.create.customer.AddCustomerEventHandler;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEventHandler;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEventHandler;
import htw.prog3.routing.input.update.inspect.InspectCargoEventHandler;
import htw.prog3.routing.input.update.relocate.RelocateStorageItemEventHandler;
import htw.prog3.routing.persistence.all.load.LoadAllEventHandler;
import htw.prog3.routing.persistence.all.save.SaveAllEventHandler;
import htw.prog3.routing.persistence.item.load.LoadItemEventHandler;
import htw.prog3.routing.persistence.item.save.SaveItemEventHandler;
import htw.prog3.routing.view.update.cargos.UpdateCargoViewEventHandler;
import htw.prog3.routing.view.update.customers.UpdateCustomersViewEventHandler;
import htw.prog3.simulation.CargoGenerator;
import htw.prog3.sm.api.StorageFacade;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.CargoType;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.ui.gui.view.*;
import htw.prog3.util.SetupHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Callable;

public class StorageManagementApp extends Application {
    private SmLogManager logManager;

    private StorageViewModel storageVM;
    private StorageManipulationViewModel storageManipulationVM;
    private CustomersViewModel customersVM;
    private MenuViewModel menuVM;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        List<String> params = getParameters().getUnnamed();

        int capacity = SetupHelper.readCapacity(params.size() > 0 ? params.get(0) : null);
        FileOutputStream optLogStream = SetupHelper.readLogPath(params.size() > 1 ? params.get(1) : null);
        Locale logLang = SetupHelper.readLogLang(params.size() > 2 ? params.get(2) : "en");

        setupEnvironment(capacity, optLogStream, logLang);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Map<Class<?>, Callable<?>> creators = new HashMap<>();
        creators.put(StorageView.class, () -> new StorageView(storageVM));
        creators.put(CustomersView.class, () -> new CustomersView(customersVM));
        creators.put(StorageManipulationView.class, () -> new StorageManipulationView(storageManipulationVM));
        creators.put(MenuView.class, () -> new MenuView(menuVM));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_view.fxml"));
        loader.setControllerFactory(param -> {
            Callable<?> callable = creators.get(param);
            if (callable == null)
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException |
                        NoSuchMethodException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            else try {
                return callable.call();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });

        Parent root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Storage Management System");
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (logManager != null) {
            try {
                logManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void setupEnvironment(int capacity, FileOutputStream logStream, Locale logLang) {
        StorageManagement storageManagement = StorageManagement.ofCapacity(capacity);
        StorageFacade storageFacade = new StorageFacade(storageManagement);

        addSampleData(storageFacade);

        UpdateCargoViewEventHandler updateCargoViewHandler = new UpdateCargoViewEventHandler();
        UpdateCustomersViewEventHandler updateCustomersViewHandler = new UpdateCustomersViewEventHandler();

        IllegalInputEventHandler illegalInputHandler = new IllegalInputEventHandler();

        RelocateStorageItemEventHandler relocateStorageItemHandler = new RelocateStorageItemEventHandler();
        InspectCargoEventHandler inspectCargoHandler = new InspectCargoEventHandler();

        AddCargoEventHandler addCargoHandler = new AddCargoEventHandler();
        RemoveCargoEventHandler removeCargoHandler = new RemoveCargoEventHandler();

        AddCustomerEventHandler addCustomerHandler = new AddCustomerEventHandler();
        RemoveCustomerEventHandler removeCustomerHandler = new RemoveCustomerEventHandler();

        SaveAllEventHandler saveAllHandler = new SaveAllEventHandler();
        SaveItemEventHandler saveItemHandler = new SaveItemEventHandler();

        LoadAllEventHandler loadAllHandler = new LoadAllEventHandler();
        LoadItemEventHandler loadItemHandler = new LoadItemEventHandler();

        if (logStream != null) try {
            PrintWriter writer = new PrintWriter(logStream, true);

            LogDictionaryLoader loader = LogDictionaryLoader.from(logLang);
            ProcessLogDictionary processLogDict = loader.loadProcessLogDictionary();
            ProcessLogger processLogger = new ProcessLogger(storageFacade, writer, processLogDict);
            updateCargoViewHandler.addListener(processLogger);

            InteractionLogDictionary interactionLogDict = loader.loadInteractionLogDictionary();
            InteractionLogger interactionLogger = new InteractionLogger(writer, interactionLogDict);

            interactionLogger.registerAddCargoEventListener(addCargoHandler);
            interactionLogger.registerAddCustomerEventListener(addCustomerHandler);
            interactionLogger.registerRemoveCargoEventListener(removeCargoHandler);
            interactionLogger.registerRemoveCustomerEventListener(removeCustomerHandler);
            interactionLogger.registerInspectCargoEventListener(inspectCargoHandler);
            interactionLogger.registerRelocateStorageItemEventListener(relocateStorageItemHandler);
            interactionLogger.registerSaveItemEventListener(saveItemHandler);
            interactionLogger.registerLoadItemEventListener(loadItemHandler);

            logManager = SmLogManager.from(interactionLogger, processLogger);
        } catch (IOException e) {
            System.out.println("The logger can't be started due to problems with InputStream.");
        }

        UiEventController eventController = new UiEventController(storageFacade)
                .setUpdateCargoViewEventHandler(updateCargoViewHandler)
                .setUpdateCustomersViewEventHandler(updateCustomersViewHandler)
                .setIllegalInputEventHandler(illegalInputHandler);
        addCargoHandler.addListener(eventController);
        addCustomerHandler.addListener(eventController);
        removeCargoHandler.addListener(eventController);
        removeCustomerHandler.addListener(eventController);
        relocateStorageItemHandler.addListener(eventController);
        inspectCargoHandler.addListener(eventController);

        saveAllHandler.addListener(eventController);
        saveItemHandler.addListener(eventController);
        loadAllHandler.addListener(eventController);
        loadItemHandler.addListener(eventController);

        storageVM = new StorageViewModel();
        storageVM.setRelocateStorageItemHandler(relocateStorageItemHandler);
        updateCargoViewHandler.addListener(storageVM);

        storageManipulationVM = new StorageManipulationViewModel(storageVM)
                .setRemoveCargoEventHandler(removeCargoHandler)
                .setAddCargoEventHandler(addCargoHandler)
                .setSaveItemEventHandler(saveItemHandler)
                .setInspectCargoEventHandler(inspectCargoHandler);
        updateCustomersViewHandler.addListener(storageManipulationVM);

        customersVM = new CustomersViewModel()
                .setAddCustomerEventHandler(addCustomerHandler)
                .setRemoveCustomerEventHandler(removeCustomerHandler);
        updateCustomersViewHandler.addListener(customersVM);
        illegalInputHandler.addListener(customersVM);

        menuVM = new MenuViewModel()
                .setSaveAllEventHandler(saveAllHandler)
                .setLoadAllEventHandler(loadAllHandler)
                .setLoadItemEventHandler(loadItemHandler);

        eventController.updateView();
    }

    private void addSampleData(StorageFacade storageFacade) {
        try {
            // Add customers
            for (int i1 = 0; i1 < 10; i1++) {
                String s = "Customer" + i1;
                storageFacade.addCustomer(s);
            }
            // Add cargos
            for (int i = 0; i < 10; i++) {
                storageFacade.addCargo(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED,
                        storageFacade.getCustomerRecords().get("Customer" + i).getCustomer().getName(),
                        CargoGenerator.DEFAULT_VALUE,
                        CargoGenerator.DEFAULT_DURATION,
                        new HashSet<>(Arrays.asList(Hazard.EXPLOSIVE, Hazard.RADIOACTIVE)),
                        true,
                        false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
