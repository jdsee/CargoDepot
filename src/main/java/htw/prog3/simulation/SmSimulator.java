package htw.prog3.simulation;

import htw.prog3.sm.api.StorageManagement;
import htw.prog3.storageContract.cargo.Cargo;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SmSimulator {
    private final StorageManagement management;
    private final Lock lock = new ReentrantLock();
    private final Condition storageFree = lock.newCondition();
    private final Condition storageFull = lock.newCondition();
    private final int id;
    private final Queue<Integer> storagePositions = new LinkedList<>();
    private final SimulationSelector simulationSelector;

    public SmSimulator(SimulationSelector simulationSelector, StorageManagement management, int id) {
        this.simulationSelector = simulationSelector;
        this.management = management;
        this.id = id;
    }

    public void addCargo(Cargo cargo) throws InterruptedException {
        System.out.printf("%s: WAITING FOR LOCK in storage(%s)%n", Thread.currentThread().getName(), this.id);
        lock.lock();
        System.out.printf("%s: ACHIEVED LOCK in storage(%s)%n", Thread.currentThread().getName(), this.id);
        try {
            while (isFullStorage()) {
                System.out.printf("%s: WAIT - storage(%s) full%n", Thread.currentThread().getName(), this.id);
                storageFree.await();
                System.out.printf("%s: NOTIFIED -  storage(%s) free%n", Thread.currentThread().getName(), this.id);
            }
            int pos = management.addCargo(cargo);
            storagePositions.add(pos);
            System.out.printf("%s: ADDED cargo(position = %d) to storage(%s), actual load=%d%n",
                    Thread.currentThread().getName(), pos, this.id, management.getItemCount());
            if (isFullStorage())
                storageFull.signal();
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void rearrangeCargo() throws InterruptedException {
        lock.lock();
        try {
            while (!isFullStorage()) {
                System.out.printf("%s: WAIT (REARRANGE) - storage(%s) free%n", Thread.currentThread().getName(), this.id);
                storageFull.await();
                System.out.printf("%s: NOTIFIED (REARRANGE) - storage(%s) full%n", Thread.currentThread().getName(), this.id);
            }
            SmSimulator target = simulationSelector.getRandomSimulator(this);

            System.out.printf("%s: START REARRANGEMENT from storage(%s) --> storage(%s)%n",
                    Thread.currentThread().getName(), this.id, target.id);
            final int furthestCargoPosition = storagePositions.peek();
            Cargo cargo = management.getCargo(furthestCargoPosition);
            target.addCargo(cargo);
            removeFurthestCargo();
            System.out.printf("%s: REMOVED cargo(position %d) from storage(%s), actual load=%d%n",
                    Thread.currentThread().getName(), furthestCargoPosition, this.id, management.getItemCount());
            storageFree.signal();
        } finally {
            lock.unlock();
        }
    }

    private void removeFurthestCargo() {
        if (!storagePositions.isEmpty()) {
            int furthestPosition = storagePositions.poll();
            management.removeCargo(furthestPosition);
        }
    }

    private boolean isFullStorage() {
        return !management.hasFreeCapacity();
    }
}
