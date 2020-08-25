package htw.prog3.ui.cli.view.listener;

import htw.prog3.routing.config.ViewConfigEventHandler;
import htw.prog3.routing.view.update.cargos.UpdateCargoViewEvent;
import htw.prog3.sm.api.StorageFacade;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.UnitisedCargoImpl;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.ui.cli.view.listener.CliViewUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;

import static htw.prog3.storageContract.cargo.Hazard.TOXIC;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class CliViewUpdaterTest {
    @Mock
    StorageFacade mockStorageFacade;
    @Mock
    PrintStream mockOut;
    @Mock
    UpdateCargoViewEvent mockUpdateCargoViewEvent;
    @Mock
    ViewConfigEventHandler mockViewConfigHandler;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void properInitialization() {
        CliViewUpdater viewUpdater = new CliViewUpdater(mockStorageFacade);
        CliViewUpdater other = new CliViewUpdater(mockStorageFacade);

        assertThat(viewUpdater).isNotNull().isNotSameAs(other);
    }

    @Test
    void onUpdateCargoViewEvent_shouldActivateNewHazardChangeListener() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            StorageManagement storageManagement = StorageManagement.ofCapacity(10);
            StorageFacade storageFacade = new StorageFacade(storageManagement);
            CliViewUpdater viewUpdater = new CliViewUpdater(storageFacade);
            StorageManagement replacementManagement = StorageManagement.ofCapacity(10);
            storageFacade.setStorageManagement(replacementManagement);

            viewUpdater.onUpdateCargoViewEvent(mockUpdateCargoViewEvent);

            replacementManagement.addCustomer("dummy");
            replacementManagement.addCargo(createTestCargo("dummy", Hazard.TOXIC));
            verify(System.out).printf(
                    "<> Hazards changed --- Actually present: %s%n", new HashSet<>(singletonList(TOXIC)));
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void onUpdateCargoViewEvent_shouldActivateNewCriticalCapacityListener() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            StorageManagement storageManagement = StorageManagement.ofCapacity(10);
            StorageFacade storageFacade = new StorageFacade(storageManagement);
            CliViewUpdater viewUpdater = new CliViewUpdater(storageFacade);
            StorageManagement replacementManagement = StorageManagement.ofCapacity(2);
            storageFacade.setStorageManagement(replacementManagement);

            viewUpdater.onUpdateCargoViewEvent(mockUpdateCargoViewEvent);

            replacementManagement.addCustomer("dummy");
            replacementManagement.addCargo(createTestCargo("dummy"));
            verify(System.out).printf("<> Storage capacity critical --- " +
                    "%s space left. The actual maximum is: %d%n", "Only one", 2);
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void setViewConfigEventHandler() {
        StorageManagement storageManagement = StorageManagement.ofCapacity(10);
        StorageFacade storageFacade = new StorageFacade(storageManagement);
        CliViewUpdater viewUpdater = new CliViewUpdater(storageFacade);

        viewUpdater.setViewConfigEventHandler(mockViewConfigHandler);

        viewUpdater.onUpdateCargoViewEvent(mockUpdateCargoViewEvent);
        verify(mockViewConfigHandler, times(2)).addListener(any());
        verify(mockViewConfigHandler, times(2)).removeListener(any());
    }

    @SuppressWarnings("SameParameterValue")
    private Cargo createTestCargo(String customerName, Hazard... hazards) {
        return new UnitisedCargoImpl(new CustomerImpl(customerName), BigDecimal.TEN, Duration.ofDays(1),
                new HashSet<>(asList(hazards)), true);
    }
}