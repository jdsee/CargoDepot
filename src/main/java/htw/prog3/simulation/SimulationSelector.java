package htw.prog3.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationSelector {
    private static final Random random = new Random();
    private final List<SmSimulator> simulators = new ArrayList<>();

    public void addSimulator(SmSimulator simulator) {
        simulators.add(simulator);
    }

    public SmSimulator get(int index) {
        return simulators.get(index);
    }

    public SmSimulator getRandomSimulator() {
        if (0 < simulators.size()) {
            int choice = random.nextInt(simulators.size());
            return simulators.get(choice);
        }
        return null;
    }

    // not testable: non deterministic
    public SmSimulator getRandomSimulator(SmSimulator skipIfSelected) {
        SmSimulator selection;
        do selection = getRandomSimulator();
        while (skipIfSelected.equals(selection));
        return selection;
    }
}
