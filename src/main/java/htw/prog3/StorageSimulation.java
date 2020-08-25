package htw.prog3;

import htw.prog3.log.LogDictionaryLoader;
import htw.prog3.log.ProcessLogDictionary;
import htw.prog3.log.ProcessLogger;
import htw.prog3.simulation.*;
import htw.prog3.sm.api.StorageFacade;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.CustomerAdministration;
import htw.prog3.sm.core.Storage;
import htw.prog3.sm.core.StorageManagementImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class StorageSimulation {
    public static final int SIMULATION_CAPACITY = 1_000;
    private static final List<Thread> threads = new LinkedList<>();

    /**
     * This simulation might run into a deadlock before every storage has been
     * used to capacity. That is due to the random selection of storage in the
     * rearrange runners.
     * In most cases every storage is full before the deadlock occurs.
     */
    public static void main(String[] args) throws IOException {
        SimulationSelector simulationSelector = new SimulationSelector();
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        customerAdministration.addCustomer(CargoGenerator.DEFAULT_OWNER);

        PrintWriter outWriter = new PrintWriter(System.out, true);
        ProcessLogDictionary logDictionary = LogDictionaryLoader.from(Locale.ENGLISH).loadProcessLogDictionary();

        for (int i = 0; i < 3; i++) {
            Storage storage = Storage.ofCapacity(SIMULATION_CAPACITY);
            StorageManagement management = new StorageManagementImpl(storage, customerAdministration);
            simulationSelector.addSimulator(new SmSimulator(simulationSelector, management, i));
            new ProcessLogger(new StorageFacade(management), outWriter, logDictionary);
        }

        synchronized (System.out) {
            threads.add(new Thread(new RearrangeCargoRunner(simulationSelector.get(0))));
            threads.add(new Thread(new RearrangeCargoRunner(simulationSelector.get(1))));
            threads.add(new Thread(new RearrangeCargoRunner(simulationSelector.get(2))));
            threads.add(new Thread(new AddCargoRunner(simulationSelector)));
            threads.add(new Thread(new AddCargoRunner(simulationSelector)));
            for (Thread t : threads) t.start();
        }
    }
}
