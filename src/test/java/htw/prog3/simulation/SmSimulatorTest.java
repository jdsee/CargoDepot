package htw.prog3.simulation;

import htw.prog3.simulation.SimulationSelector;
import htw.prog3.simulation.SmSimulator;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.UnitisedCargoImpl;
import htw.prog3.storageContract.cargo.Cargo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SmSimulatorTest {
    // The following tests use reflection for setting the lock and conditions
    // I thought passing those attributes in the constructor wouldn't be reasonable
    public static final String LOCK_FIELD_NAME = "lock";
    public static final String STORAGE_FULL_FIELD_NAME = "storageFull";
    public static final String STORAGE_FREE_FIELD_NAME = "storageFree";
    @Mock
    SimulationSelector mockSelector;
    @Mock
    StorageManagement mockManagement;
    @Mock
    Cargo mockCargo;
    @Mock
    Lock mockLock;
    @Mock
    Condition mockFull;
    @Mock
    Condition mockFree;
    @Mock
    SmSimulator mockTargetSimulator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void addCargo_shouldAddCargoToStorageManagement() throws InterruptedException {
        doReturn(true).when(mockManagement).hasFreeCapacity();
        SmSimulator simulation = new SmSimulator(mockSelector, mockManagement, 0);

        simulation.addCargo(mockCargo);

        verify(mockManagement).addCargo(mockCargo);
    }

    @Test
    void addCargo_shouldAddCargoToStorage() throws InterruptedException {
        StorageManagement management = spy(StorageManagement.create());
        management.addCustomer("x");
        Cargo cargo = createTestCargo("x");
        SmSimulator simulation = new SmSimulator(mockSelector, management, 0);

        simulation.addCargo(cargo);

        verify(management).addCargo(any(Cargo.class));
    }

    @Test
    void addCargo_shouldAcquireLockBeforeAddingCargoAndReleaseAfterwards()
            throws InterruptedException, NoSuchFieldException {
        SmSimulator simulator = new SmSimulator(mockSelector, mockManagement, 0);
        FieldSetter.setField(simulator, simulator.getClass().getDeclaredField(LOCK_FIELD_NAME), mockLock);
        doReturn(true).when(mockManagement).hasFreeCapacity();

        simulator.addCargo(mockCargo);

        InOrder inOrder = inOrder(mockLock, mockManagement);
        inOrder.verify(mockLock).lock();
        inOrder.verify(mockManagement).addCargo(any());
        inOrder.verify(mockLock).unlock();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void addCargo_shouldSignalFullConditionWhenFull() throws InterruptedException, NoSuchFieldException {
        SmSimulator simulator = new SmSimulator(mockSelector, mockManagement, 0);
        FieldSetter.setField(simulator, simulator.getClass().getDeclaredField(STORAGE_FULL_FIELD_NAME), mockFull);
        doReturn(true, false).when(mockManagement).hasFreeCapacity();

        simulator.addCargo(mockCargo);

        verify(mockFull).signal();
    }

    @Test
    void addCargo_shouldAwaitFreeConditionIfFull_Test() throws InterruptedException, NoSuchFieldException {
        SmSimulator simulator = new SmSimulator(mockSelector, mockManagement, 0);
        FieldSetter.setField(simulator, simulator.getClass().getDeclaredField(STORAGE_FREE_FIELD_NAME), mockFree);
        doReturn(false, true).when(mockManagement).hasFreeCapacity();

        simulator.addCargo(mockCargo);

        verify(mockFree).await();
    }

    @Test
    void rearrangeCargo_shouldChooseFirstStoredCargoForRearrangement() throws InterruptedException {
        StorageManagement management = StorageManagement.ofCapacity(2);
        management.addCustomer("x");
        SmSimulator simulator = new SmSimulator(mockSelector, management, 1);
        doReturn(mockTargetSimulator).when(mockSelector).getRandomSimulator(simulator);
        Cargo firstCargo = createTestCargo();
        Cargo cargo = createTestCargo();
        simulator.addCargo(firstCargo);
        simulator.addCargo(cargo);

        simulator.rearrangeCargo();

        verify(mockTargetSimulator).addCargo(firstCargo);
        verify(mockTargetSimulator, times(0)).addCargo(cargo);
    }

    @Test
    void rearrangeCargo_shouldAddCargoToTargetBeforeRemovingFromSource() throws InterruptedException {
        StorageManagement spyManagement = spy(StorageManagement.ofCapacity(2));
        spyManagement.addCustomer("x");
        SmSimulator simulator = new SmSimulator(mockSelector, spyManagement, 1);
        doReturn(mockTargetSimulator).when(mockSelector).getRandomSimulator(simulator);
        Cargo firstCargo = createTestCargo();
        Cargo cargo = createTestCargo();
        simulator.addCargo(firstCargo);
        simulator.addCargo(cargo);

        simulator.rearrangeCargo();

        InOrder inOrder = inOrder(mockTargetSimulator, spyManagement);
        inOrder.verify(mockTargetSimulator).addCargo(firstCargo);
        inOrder.verify(spyManagement).removeCargo(0);
    }

    @Test
    void rearrangeCargo_shouldAwaitFullConditionIfNotFull() throws NoSuchFieldException, InterruptedException {
        SmSimulator simulator = new SmSimulator(mockSelector, mockManagement, 1);
        doReturn(true, true, true, false).when(mockManagement).hasFreeCapacity();
        doReturn(mockTargetSimulator).when(mockSelector).getRandomSimulator(simulator);
        simulator.addCargo(mockCargo);
        FieldSetter.setField(simulator, simulator.getClass().getDeclaredField(STORAGE_FULL_FIELD_NAME), mockFull);

        simulator.rearrangeCargo();

        verify(mockFull).await();
    }

    @Test
    void rearrangeCargo_shouldSignalFreeConditionAfterRearrangement() throws NoSuchFieldException, InterruptedException {
        SmSimulator simulator = new SmSimulator(mockSelector, mockManagement, 1);
        doReturn(true, true, false).when(mockManagement).hasFreeCapacity();
        doReturn(mockTargetSimulator).when(mockSelector).getRandomSimulator(simulator);
        simulator.addCargo(mockCargo);
        FieldSetter.setField(simulator, simulator.getClass().getDeclaredField(STORAGE_FREE_FIELD_NAME), mockFree);

        simulator.rearrangeCargo();

        verify(mockFree).signal();
    }

    private Cargo createTestCargo() {
        return createTestCargo("x");
    }

    private Cargo createTestCargo(String customerName) {
        return new UnitisedCargoImpl(new CustomerImpl("x"), BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true);
    }
}