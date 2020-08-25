package htw.prog3.simulation;

import htw.prog3.storageContract.cargo.Cargo;

public class AddCargoRunner implements Runnable {
    private final SimulationSelector simulationSelector;

    public AddCargoRunner(SimulationSelector simulationSelector) {
        this.simulationSelector = simulationSelector;
    }

    // not testable: infinite loop
    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) addCargo();
    }

    private void addCargo() {
        SmSimulator simulator = simulationSelector.getRandomSimulator();
        Cargo cargo = CargoGenerator.getRandomCargo();
        try {
            simulator.addCargo(cargo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
