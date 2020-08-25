package htw.prog3.simulation;

public class RearrangeCargoRunner implements Runnable {
    private final SmSimulator simulator;

    public RearrangeCargoRunner(SmSimulator simulator) {
        this.simulator = simulator;
    }

    // not testable: infinite loop
    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            try {
                simulator.rearrangeCargo();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
